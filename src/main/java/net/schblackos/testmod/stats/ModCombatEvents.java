package net.schblackos.testmod.stats;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.schblackos.testmod.Testmod;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = Testmod.MODID)
public final class ModCombatEvents {
    private static final String SHOT_STATS = "testmod_shot_stats";
    private static final String SHOT_CRITICAL_CHANCE = "testmod_shot_critical_chance";
    private static final String SHOT_DAMAGE_BONUS = "testmod_shot_damage_bonus";

    private ModCombatEvents() {
    }

    @SubscribeEvent
    public static void onMeleeCritical(CriticalHitEvent event) {
        var player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (player.getMainHandItem().has(DataComponents.PIERCING_WEAPON)) {
            // Spear jabs/charges roll below because they bypass this event normally.
            event.setCriticalHit(false);
            event.setDamageMultiplier(1.0F);
            DamageNumbers.trackMeleeCritical(event);
            return;
        }
        // Retain attack cooldown: weak spam attacks cannot roll critical hits.
        boolean critical = player.getAttackStrengthScale(0.5F) > 0.9F
                && StatsMath.succeeds(player.getAttributeValue(ModStats.CRITICAL_CHANCE), player.getRandom().nextDouble());
        event.setCriticalHit(critical);
        event.setDamageMultiplier(critical ? 1.5F : 1.0F);
        event.setDisableSweep(critical);
        DamageNumbers.trackMeleeCritical(event);
    }

    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof AbstractArrow arrow)
                || !(arrow.getOwner() instanceof LivingEntity owner) || arrow.getWeaponItem() == null) {
            return;
        }
        var data = arrow.getPersistentData();
        if (!data.getBooleanOr(SHOT_STATS, false)) {
            ItemStack weapon = arrow.getWeaponItem();
            data.putBoolean(SHOT_STATS, true);
            data.putDouble(SHOT_CRITICAL_CHANCE, statWithWeapon(owner, ModStats.CRITICAL_CHANCE, weapon));
            // Normal arrow damage remains; item Damage and increased player base add a bonus.
            data.putDouble(SHOT_DAMAGE_BONUS,
                    Math.max(0.0D, owner instanceof Player
                            ? statWithWeapon(owner, ModStats.DAMAGE, weapon) - ModStats.BASE_DAMAGE
                            : weapon.getAttributeModifiers().compute(ModStats.DAMAGE, 0.0D, EquipmentSlot.MAINHAND)));
        }
        // A fully charged bow otherwise adds vanilla's separate guaranteed/random crit bonus.
        arrow.setCritArrow(false);
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        var source = event.getSource();
        boolean critical = DamageNumbers.consumeMeleeCritical(event);
        if ((source.getEntity() != null || source.getDirectEntity() instanceof Projectile)
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                && StatsMath.succeeds(victim.getAttributeValue(ModStats.EVASION), victim.getRandom().nextDouble())) {
            event.setCanceled(true);
            DamageNumbers.trackHit(event, false);
            return;
        }

        if (source.getDirectEntity() instanceof AbstractArrow arrow
                && arrow.getPersistentData().getBooleanOr(SHOT_STATS, false)) {
            var data = arrow.getPersistentData();
            float damage = event.getAmount() + (float) data.getDoubleOr(SHOT_DAMAGE_BONUS, 0.0D);
            if (StatsMath.succeeds(data.getDoubleOr(SHOT_CRITICAL_CHANCE, 0.0D), arrow.getRandom().nextDouble())) {
                damage = StatsMath.criticalDamage(damage);
                critical = true;
            }
            event.setAmount(damage);
        } else if (source.is(DamageTypes.SPEAR) && source.getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.isUsingItem() ? attacker.getUseItem() : attacker.getMainHandItem();
            if (StatsMath.succeeds(statWithWeapon(attacker, ModStats.CRITICAL_CHANCE, weapon),
                    attacker.getRandom().nextDouble())) {
                event.setAmount(StatsMath.criticalDamage(event.getAmount()));
                critical = true;
            }
        }

        DamageNumbers.trackHit(event, critical);

        event.addReductionModifier(DamageContainer.Reduction.ARMOR, (container, vanillaReduction) -> {
            if (source.is(DamageTypeTags.BYPASSES_ARMOR)) {
                return vanillaReduction;
            }
            float incoming = container.getNewDamage();
            return incoming - StatsMath.afterDefence(incoming, victim.getAttributeValue(ModStats.DEFENCE));
        });
    }

    @SubscribeEvent
    public static void onDamageAfterArmour(LivingDamageEvent.Pre event) {
        if (!event.getSource().is(DamageTypeTags.BYPASSES_RESISTANCE)
                && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            event.setNewDamage(StatsMath.afterResistance(event.getNewDamage(),
                    event.getEntity().getAttributeValue(ModStats.RESISTANCE)));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Copy base values and permanent bonuses, never the old equipment modifiers.
        for (var attribute : List.of(ModStats.DAMAGE, ModStats.DEFENCE,
                ModStats.CRITICAL_CHANCE, ModStats.RESISTANCE, ModStats.EVASION)) {
            copyPermanentStat(event, attribute);
        }
    }

    private static void copyPermanentStat(PlayerEvent.Clone event, Holder<Attribute> attribute) {
        var original = event.getOriginal().getAttribute(attribute);
        var replacement = event.getEntity().getAttribute(attribute);
        if (original != null && replacement != null) {
            replacement.apply(original.pack());
        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("teststats").executes(context -> {
            var player = context.getSource().getPlayerOrException();
            context.getSource().sendSuccess(() -> Component.translatable("command.testmod.stats",
                    format(player.getAttributeValue(ModStats.DAMAGE)),
                    format(player.getAttributeValue(ModStats.CRITICAL_CHANCE)),
                    format(player.getAttributeValue(ModStats.DEFENCE)),
                    format(player.getAttributeValue(ModStats.RESISTANCE)),
                    format(player.getAttributeValue(ModStats.EVASION))), false);
            return 1;
        }));
    }

    private static String format(double value) {
        return net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value);
    }

    /** Replace equipped hand stats with the actual firing weapon, including off-hand bows. */
    private static double statWithWeapon(LivingEntity owner, Holder<Attribute> attribute, ItemStack weapon) {
        AttributeInstance original = owner.getAttribute(attribute);
        AttributeInstance snapshot = new AttributeInstance(attribute, ignored -> { });
        if (original != null) {
            snapshot.setBaseValue(original.getBaseValue());
            original.getModifiers().forEach(snapshot::addTransientModifier);
        }
        Set<Identifier> handModifiers = new HashSet<>();
        for (var slot : new EquipmentSlot[] {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
            owner.getItemBySlot(slot).getAttributeModifiers().forEach(slot, (candidate, modifier) -> {
                if (candidate.equals(attribute)) {
                    handModifiers.add(modifier.id());
                }
            });
        }
        handModifiers.forEach(snapshot::removeModifier);
        weapon.getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (candidate, modifier) -> {
            if (candidate.equals(attribute)) {
                snapshot.removeModifier(modifier.id());
                snapshot.addTransientModifier(modifier);
            }
        });
        return snapshot.getValue();
    }
}
