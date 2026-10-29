package net.schblackos.testmod.event;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
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
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.network.style.DamageNumbers;
import net.schblackos.testmod.stats.ModCustomStats;
import net.schblackos.testmod.stats.StatsMath;
import net.schblackos.testmod.tags.ModTags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = Testmod.MODID)
public final class ModCombatEvents {
    private static final String SHOT_STATS = "testmod_shot_stats";
    private static final String SHOT_CRITICAL_CHANCE = "testmod_shot_critical_chance";
    private static final String SHOT_DAMAGE_BONUS = "testmod_shot_damage_bonus";
    private static final String SHOT_MAGIC_DAMAGE = "testmod_shot_magic_damage";

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
                && StatsMath.succeeds(player.getAttributeValue(ModCustomStats.CRITICAL_CHANCE), player.getRandom().nextDouble());
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
            data.putDouble(SHOT_CRITICAL_CHANCE, statWithWeapon(owner, ModCustomStats.CRITICAL_CHANCE, weapon));
            data.putDouble(SHOT_MAGIC_DAMAGE, statWithWeapon(owner, ModCustomStats.MAGIC_DAMAGE, weapon));
            // Normal arrow damage remains; item Damage and increased player base add a bonus.
            data.putDouble(SHOT_DAMAGE_BONUS,
                    Math.max(0.0D, owner instanceof Player
                            ? statWithWeapon(owner, ModCustomStats.DAMAGE, weapon) - ModCustomStats.BASE_DAMAGE
                            : weapon.getAttributeModifiers().compute(ModCustomStats.DAMAGE, 0.0D, EquipmentSlot.MAINHAND)));
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
                && StatsMath.succeeds(victim.getAttributeValue(ModCustomStats.EVASION), victim.getRandom().nextDouble())) {
            event.setCanceled(true);
            DamageNumbers.trackHit(event, false);
            return;
        }

        boolean magicSource = source.is(ModTags.DamageTypes.MAGIC_DAMAGE);
        float physical = magicSource ? 0.0F : event.getAmount();
        float magic = magicSource ? event.getAmount() : 0.0F;

        if (source.getDirectEntity() instanceof AbstractArrow arrow
                && arrow.getPersistentData().getBooleanOr(SHOT_STATS, false)) {
            var data = arrow.getPersistentData();
            physical += (float) data.getDoubleOr(SHOT_DAMAGE_BONUS, 0.0D);
            magic += (float) data.getDoubleOr(SHOT_MAGIC_DAMAGE, 0.0D);
            critical = StatsMath.succeeds(data.getDoubleOr(SHOT_CRITICAL_CHANCE, 0.0D), arrow.getRandom().nextDouble());
            if (critical) {
                physical = StatsMath.criticalDamage(physical);
                magic = StatsMath.criticalDamage(magic);
            }
        } else if (source.is(DamageTypes.SPEAR) && source.getEntity() instanceof LivingEntity attacker) {
            boolean charging = attacker.isUsingItem()
                    && attacker.getUseItem().has(DataComponents.KINETIC_WEAPON);
            ItemStack weapon = charging ? attacker.getUseItem() : attacker.getMainHandItem();
            magic += (float) statWithWeapon(attacker, ModCustomStats.MAGIC_DAMAGE, weapon)
                    * (charging ? 1.0F : attackStrength(attacker));
            critical = StatsMath.succeeds(statWithWeapon(attacker, ModCustomStats.CRITICAL_CHANCE, weapon),
                    attacker.getRandom().nextDouble());
            if (critical) {
                physical = StatsMath.criticalDamage(physical);
                magic = StatsMath.criticalDamage(magic);
            }
        } else if (source.getEntity() instanceof LivingEntity attacker) {
            if (magicSource) {
                magic += (float) attacker.getAttributeValue(ModCustomStats.MAGIC_DAMAGE);
            } else if (source.getDirectEntity() == attacker && isWeaponAttack(source)) {
                // Vanilla has already applied the critical multiplier to physical damage.
                float bonus = (float) attacker.getAttributeValue(ModCustomStats.MAGIC_DAMAGE) * attackStrength(attacker);
                magic += critical ? StatsMath.criticalDamage(bonus) : bonus;
            }
        }

        float total = physical + magic;
        event.setAmount(total);
        DamageNumbers.trackHit(event, critical);

        // One hurt event preserves shields, immunity frames, and one damage number.
        // Split any shield/iframe-adjusted amount in the original channel proportions.
        float magicFraction = total > 0.0F ? magic / total : 0.0F;
        event.addReductionModifier(DamageContainer.Reduction.ARMOR, (container, vanillaReduction) -> {
            float incoming = container.getNewDamage();
            float magicPart = incoming * magicFraction;
            float physicalPart = incoming - magicPart;
            double defence = source.is(DamageTypeTags.BYPASSES_ARMOR)
                    ? 0.0D : victim.getAttributeValue(ModCustomStats.DEFENCE);
            double resistance = source.is(DamageTypeTags.BYPASSES_RESISTANCE)
                    || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                    ? 0.0D : victim.getAttributeValue(ModCustomStats.RESISTANCE);
            float remaining = StatsMath.combinedDamage(physicalPart, magicPart, defence, resistance);
            return Math.max(0.0F, incoming - remaining);
        });
    }

    private static boolean isWeaponAttack(DamageSource source) {
        return source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK)
                || source.is(DamageTypes.MOB_ATTACK_NO_AGGRO) || source.is(DamageTypes.MACE_SMASH);
    }

    private static float attackStrength(LivingEntity attacker) {
        if (!(attacker instanceof Player player)) {
            return 1.0F;
        }
        float strength = player.getAttackStrengthScale(0.5F);
        return 0.2F + strength * strength * 0.8F;
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Copy base values and permanent bonuses, never the old equipment modifiers.
        for (var attribute : List.of(ModCustomStats.DAMAGE, ModCustomStats.MAGIC_DAMAGE, ModCustomStats.DEFENCE,
                ModCustomStats.CRITICAL_CHANCE, ModCustomStats.RESISTANCE, ModCustomStats.EVASION)) {
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
                    format(player.getAttributeValue(ModCustomStats.DAMAGE)),
                    format(player.getAttributeValue(ModCustomStats.MAGIC_DAMAGE)),
                    format(player.getAttributeValue(ModCustomStats.CRITICAL_CHANCE)),
                    format(player.getAttributeValue(ModCustomStats.DEFENCE)),
                    format(player.getAttributeValue(ModCustomStats.RESISTANCE)),
                    format(player.getAttributeValue(ModCustomStats.EVASION))), false);
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
