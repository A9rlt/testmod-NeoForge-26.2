package net.schblackos.testmod.stats;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.entity.custom.TestArrowEntity;
import net.schblackos.testmod.item.ModItems;
import net.schblackos.testmod.network.DamageNumberPayload;

/** Integration tests run by the existing Gradle runGameTestServer task. */
public final class StatsGameTests {
    private static final DeferredRegister<Consumer<GameTestHelper>> FUNCTIONS =
            DeferredRegister.create(Registries.TEST_FUNCTION, Testmod.MODID);
    private static final Map<String, ResourceKey<Consumer<GameTestHelper>>> TESTS = new LinkedHashMap<>();

    static {
        addTest("stats_player_defaults_and_equipment", StatsGameTests::defaultsAndEquipment);
        addTest("stats_armour_equip_and_remove", StatsGameTests::armourEquipAndRemove);
        addTest("stats_sword_damage_and_critical_chance", StatsGameTests::swordDamageAndCriticalChance);
        addTest("stats_defence_and_resistance", StatsGameTests::defenceAndResistance);
        addTest("stats_evasion_respects_damage_source", StatsGameTests::evasionRespectsDamageSource);
        addTest("stats_arrow_snapshots_firing_weapon", StatsGameTests::arrowSnapshotsFiringWeapon);
        addTest("stats_player_clone_preserves_base_stats", StatsGameTests::playerClonePreservesBaseStats);
        addTest("stats_spear_critical_chance", StatsGameTests::spearCriticalChance);
        addTest("stats_damage_numbers_resolved_damage", StatsGameTests::damageNumbersResolvedDamage);
        addTest("stats_damage_numbers_hit_isolation", StatsGameTests::damageNumbersHitIsolation);
        addTest("stats_damage_numbers_suppression", StatsGameTests::damageNumbersSuppression);
        addTest("stats_damage_numbers_melee_context", StatsGameTests::damageNumbersMeleeContext);
    }

    private StatsGameTests() {}

    public static void register(IEventBus modEventBus) {
        FUNCTIONS.register(modEventBus);
        modEventBus.addListener(StatsGameTests::registerTests);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Testmod.MODID, path);
    }

    private static void addTest(String name, Consumer<GameTestHelper> test) {
        FUNCTIONS.register(name, () -> test);
        TESTS.put(name, ResourceKey.create(Registries.TEST_FUNCTION, id(name)));
    }

    private static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(
                id("stats_environment"), new TestEnvironmentDefinition.SetDifficulty(Difficulty.NORMAL));
        TESTS.forEach((name, function) -> event.registerTest(id(name), new FunctionGameTestInstance(
                function, new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 40, 0, true))));
    }

    private static ServerPlayer player(GameTestHelper helper) {
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        // The helper's mock is not logged in. Supply a local listener so normal server
        // combat, critical animations and equipment updates can send their packets.
        new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player,
                CommonListenerCookie.createInitial(player.getGameProfile(), false));
        Vec3 pos = helper.absoluteVec(new Vec3(1.5, 2, 1.5));
        player.setPos(pos);
        player.setNoGravity(true);
        player.setOnGround(true);
        return player;
    }

    private static void defaultsAndEquipment(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        near(helper, player.getAttributeValue(ModStats.DAMAGE), 1.0, "Unarmed damage");
        near(helper, player.getAttributeValue(ModStats.CRITICAL_CHANCE), 0.0, "Base critical chance");
        near(helper, player.getAttributeValue(ModStats.RESISTANCE), 0.0, "Base resistance");
        near(helper, player.getAttributeValue(ModStats.EVASION), 0.0, "Base evasion");

        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
        player.doTick();
        near(helper, player.getAttributeValue(ModStats.DAMAGE), 5.0, "Wooden sword total damage");
        near(helper, player.getAttributeValue(ModStats.CRITICAL_CHANCE), 1.0, "Wooden sword critical chance");

        player.setItemSlot(EquipmentSlot.OFFHAND, player.getMainHandItem());
        player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        player.doTick();
        near(helper, player.getAttributeValue(ModStats.DAMAGE), 1.0, "Damage after moving sword to offhand");
        near(helper, player.getAttributeValue(ModStats.CRITICAL_CHANCE), 0.0, "Critical chance after unequipping sword");
        helper.succeed();
    }

    private static void armourEquipAndRemove(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.TEST_HELMET.get()));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.TEST_CHESTPLATE.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.TEST_LEGGINGS.get()));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.TEST_BOOTS.get()));
        player.doTick();
        near(helper, player.getAttributeValue(ModStats.DEFENCE), 26.0, "Full custom armour defence");
        near(helper, player.getAttributeValue(ModStats.RESISTANCE), 8.0, "Full custom armour resistance");
        near(helper, player.getAttributeValue(ModStats.EVASION), 4.0, "Full custom armour evasion");

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            player.setItemSlot(slot, ItemStack.EMPTY);
        }
        player.doTick();
        near(helper, player.getAttributeValue(ModStats.DEFENCE), 0.0, "Defence after removing armour");
        near(helper, player.getAttributeValue(ModStats.RESISTANCE), 0.0, "Resistance after removing armour");
        near(helper, player.getAttributeValue(ModStats.EVASION), 0.0, "Evasion after removing armour");
        helper.succeed();
    }

    private static void swordDamageAndCriticalChance(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
        chargeAttack(player);
        player.getAttribute(ModStats.CRITICAL_CHANCE).removeModifiers();
        player.getAttribute(ModStats.CRITICAL_CHANCE).setBaseValue(0.0);
        // Falling would be a guaranteed vanilla critical. Zero chance must still
        // produce the configured 5 damage under the custom critical system.
        player.setOnGround(false);
        player.fallDistance = 1.0;
        Cow normalTarget = target(helper);
        float health = normalTarget.getHealth();
        player.attack(normalTarget);
        near(helper, health - normalTarget.getHealth(), 5.0, "Sword hit with zero critical chance");

        chargeAttack(player);
        player.setOnGround(true);
        player.fallDistance = 0.0;
        player.getAttribute(ModStats.CRITICAL_CHANCE).removeModifiers();
        player.getAttribute(ModStats.CRITICAL_CHANCE).setBaseValue(100.0);
        Cow criticalTarget = target(helper);
        health = criticalTarget.getHealth();
        player.attack(criticalTarget);
        near(helper, health - criticalTarget.getHealth(), 7.5, "Guaranteed critical sword hit");
        helper.succeed();
    }

    private static void defenceAndResistance(GameTestHelper helper) {
        ServerPlayer attacker = player(helper);
        Cow target = target(helper);
        target.getAttribute(ModStats.DEFENCE).setBaseValue(3.0);
        target.getAttribute(ModStats.RESISTANCE).setBaseValue(20.0);
        float health = target.getHealth();
        helper.assertTrue(target.hurtServer(helper.getLevel(), target.damageSources().mobAttack(attacker), 10.0F),
                "The test attack should damage the target");
        near(helper, health - target.getHealth(), 5.6, "(10 damage - 3 defence) * 80%");

        Cow bypassTarget = target(helper);
        bypassTarget.getAttribute(ModStats.DEFENCE).setBaseValue(100.0);
        health = bypassTarget.getHealth();
        bypassTarget.hurtServer(helper.getLevel(), bypassTarget.damageSources().fall(), 4.0F);
        near(helper, health - bypassTarget.getHealth(), 4.0, "Fall damage bypasses defence");
        helper.succeed();
    }

    private static void evasionRespectsDamageSource(GameTestHelper helper) {
        ServerPlayer attacker = player(helper);
        Cow target = target(helper);
        target.getAttribute(ModStats.EVASION).setBaseValue(100.0);
        float health = target.getHealth();
        boolean hit = target.hurtServer(helper.getLevel(), target.damageSources().mobAttack(attacker), 4.0F);
        helper.assertFalse(hit, "100% evasion should cancel entity attacks");
        near(helper, target.getHealth(), health, "Evaded attack leaves health unchanged");
        target.hurtServer(helper.getLevel(), target.damageSources().fall(), 4.0F);
        near(helper, health - target.getHealth(), 4.0, "Evasion cannot avoid falling damage");
        helper.succeed();
    }

    private static void arrowSnapshotsFiringWeapon(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        ItemStack bow = new ItemStack(ModItems.TEST_CUSTOMBOW_ITEM.get());
        player.setItemSlot(EquipmentSlot.MAINHAND, bow);
        player.getAttribute(ModStats.DAMAGE).setBaseValue(3.0);
        player.getAttribute(ModStats.CRITICAL_CHANCE).setBaseValue(100.0);
        player.doTick();

        TestArrowEntity arrow = new TestArrowEntity(helper.getLevel(), player,
                new ItemStack(ModItems.TEST_ARROW_ITEM.get()), bow);
        arrow.setCritArrow(true);
        helper.assertTrue(helper.getLevel().addFreshEntity(arrow), "The custom arrow should spawn");
        helper.assertFalse(arrow.isCritArrow(), "Vanilla charged-arrow critical damage must be disabled");

        // Switching weapons and changing base stats after firing must not change this shot.
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
        player.getAttribute(ModStats.DAMAGE).setBaseValue(1.0);
        player.getAttribute(ModStats.CRITICAL_CHANCE).setBaseValue(0.0);
        player.doTick();
        Cow target = target(helper);
        float health = target.getHealth();
        target.hurtServer(helper.getLevel(), target.damageSources().arrow(arrow, player), 4.0F);
        near(helper, health - target.getHealth(), 9.0,
                "Arrow retains launch damage bonus 2 and guaranteed critical: (4 + 2) * 1.5");
        helper.assertTrue(arrow.getWeaponItem() != null
                        && arrow.getWeaponItem().is(ModItems.TEST_CUSTOMBOW_ITEM.get()),
                "The arrow should retain its actual firing bow");

        // A main-hand sword supplies +4 melee damage, but must not boost an off-hand bow.
        player.setItemSlot(EquipmentSlot.OFFHAND, bow.copy());
        player.doTick();
        TestArrowEntity offhandArrow = new TestArrowEntity(helper.getLevel(), player,
                new ItemStack(ModItems.TEST_ARROW_ITEM.get()), player.getOffhandItem());
        helper.assertTrue(helper.getLevel().addFreshEntity(offhandArrow), "The off-hand arrow should spawn");
        near(helper, offhandArrow.getPersistentData().getDoubleOr("testmod_shot_damage_bonus", -1.0),
                0.0, "Off-hand bow does not inherit main-hand sword damage");
        near(helper, offhandArrow.getPersistentData().getDoubleOr("testmod_shot_critical_chance", -1.0),
                1.0, "Off-hand bow has its own critical chance");
        arrow.discard();
        offhandArrow.discard();
        helper.succeed();
    }

    private static void playerClonePreservesBaseStats(GameTestHelper helper) {
        ServerPlayer original = player(helper);
        original.getAttribute(ModStats.DAMAGE).setBaseValue(3.0);
        original.getAttribute(ModStats.DEFENCE).setBaseValue(3.0);
        original.getAttribute(ModStats.CRITICAL_CHANCE).setBaseValue(7.0);
        original.getAttribute(ModStats.RESISTANCE).setBaseValue(8.0);
        original.getAttribute(ModStats.EVASION).setBaseValue(9.0);
        Identifier permanentBonus = id("stats_test_permanent_critical_bonus");
        original.getAttribute(ModStats.CRITICAL_CHANCE).addPermanentModifier(new AttributeModifier(
                permanentBonus, 2.0, AttributeModifier.Operation.ADD_VALUE));
        original.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
        original.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.TEST_HELMET.get()));
        original.doTick();
        near(helper, original.getAttributeValue(ModStats.DAMAGE), 7.0, "Original equipped damage");
        near(helper, original.getAttributeValue(ModStats.CRITICAL_CHANCE), 10.0,
                "Original critical chance with permanent and sword bonuses");

        ServerPlayer replacement = player(helper);
        NeoForge.EVENT_BUS.post(new PlayerEvent.Clone(replacement, original, true));
        replacement.doTick();
        near(helper, replacement.getAttributeBaseValue(ModStats.DAMAGE), 3.0, "Respawn base damage");
        near(helper, replacement.getAttributeValue(ModStats.DAMAGE), 3.0,
                "Respawn does not copy sword damage");
        near(helper, replacement.getAttributeValue(ModStats.DEFENCE), 3.0,
                "Respawn preserves base defence without old helmet");
        near(helper, replacement.getAttributeBaseValue(ModStats.CRITICAL_CHANCE), 7.0,
                "Respawn base critical chance");
        near(helper, replacement.getAttributeValue(ModStats.CRITICAL_CHANCE), 9.0,
                "Respawn preserves permanent critical bonus without old sword");
        helper.assertTrue(replacement.getAttribute(ModStats.CRITICAL_CHANCE).getModifier(permanentBonus) != null,
                "The permanent critical bonus must retain its modifier ID");
        near(helper, replacement.getAttributeValue(ModStats.RESISTANCE), 8.0,
                "Respawn resistance excludes old helmet bonus");
        near(helper, replacement.getAttributeValue(ModStats.EVASION), 9.0,
                "Respawn evasion excludes old helmet bonus");
        helper.assertTrue(replacement.getMainHandItem().isEmpty()
                        && replacement.getItemBySlot(EquipmentSlot.HEAD).isEmpty(),
                "Stat persistence must not copy equipment");
        helper.succeed();
    }

    private static void spearCriticalChance(GameTestHelper helper) {
        ServerPlayer player = player(helper);
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.TEST_SPEAR.get()));
        // Stabs have their own attack mechanics; supply a direct, fixed damage value
        // and full cooldown so this test isolates the custom critical chance.
        chargeAttack(player);
        player.getAttribute(ModStats.CRITICAL_CHANCE).removeModifiers();
        player.getAttribute(ModStats.CRITICAL_CHANCE).setBaseValue(0.0);
        Cow normalTarget = target(helper);
        float health = normalTarget.getHealth();
        helper.assertTrue(player.stabAttack(EquipmentSlot.MAINHAND, normalTarget, 4.0F,
                true, false, false), "The normal spear stab should hit");
        near(helper, health - normalTarget.getHealth(), 4.0, "Spear stab with zero critical chance");

        player.getAttribute(ModStats.CRITICAL_CHANCE).setBaseValue(100.0);
        Cow criticalTarget = target(helper);
        health = criticalTarget.getHealth();
        helper.assertTrue(player.stabAttack(EquipmentSlot.MAINHAND, criticalTarget, 4.0F,
                true, false, false), "The critical spear stab should hit");
        near(helper, health - criticalTarget.getHealth(), 6.0, "Guaranteed critical spear stab");
        helper.succeed();
    }

    private static void damageNumbersResolvedDamage(GameTestHelper helper) {
        ServerPlayer attacker = player(helper);
        Cow victim = target(helper);
        DamageSource source = victim.damageSources().playerAttack(attacker);
        DamageContainer normal = trackedHit(victim, source, 10.0F, false);
        // The display must report final damage, including the part absorbed by extra hearts.
        DamageNumberPayload number = DamageNumbers.createNumber(resolvedHit(victim, normal, 5.6F, 2.0F));
        assertNumber(helper, number, 5.6F, false, "Mitigated normal hit");

        DamageContainer critical = trackedHit(victim, source, 7.5F, true);
        assertNumber(helper, DamageNumbers.createNumber(resolvedHit(victim, critical, 7.5F, 7.5F)),
                7.5F, true, "Critical hit fully absorbed by extra hearts");

        DamageContainer overkill = trackedHit(victim, source, 10.0F, true);
        victim.setHealth(3.0F);
        DamageNumbers.beforeHealthDamage(new LivingDamageEvent.Pre(victim, overkill));
        assertNumber(helper, DamageNumbers.createNumber(resolvedHit(victim, overkill, 10.0F, 0.0F)),
                3.0F, true, "Overkill is limited to health available immediately before damage");
        helper.succeed();
    }

    private static void damageNumbersHitIsolation(GameTestHelper helper) {
        ServerPlayer attacker = player(helper);
        Cow first = target(helper);
        Cow second = target(helper);
        DamageSource source = first.damageSources().playerAttack(attacker);
        DamageContainer critical = trackedHit(first, source, 7.5F, true);
        helper.assertTrue(DamageNumbers.createNumber(resolvedHit(second, critical, 7.5F, 0.0F)) == null,
                "A critical hit must not be assigned to a different victim sharing its damage source");
        DamageContainer normal = trackedHit(second, source, 5.0F, false);
        assertNumber(helper, DamageNumbers.createNumber(resolvedHit(second, normal, 5.0F, 0.0F)),
                5.0F, false, "A second victim keeps its own normal-hit flag");
        LivingDamageEvent.Post firstPost = resolvedHit(first, critical, 7.5F, 0.0F);
        assertNumber(helper, DamageNumbers.createNumber(firstPost), 7.5F, true,
                "The first victim retains its critical-hit flag");
        helper.assertTrue(DamageNumbers.createNumber(firstPost) == null,
                "The same hit must only display one number");

        trackedHit(first, source, 7.5F, true);
        DamageContainer replacement = trackedHit(first, source, 5.0F, false);
        assertNumber(helper, DamageNumbers.createNumber(resolvedHit(first, replacement, 5.0F, 0.0F)),
                5.0F, false, "A later normal hit replaces an abandoned critical-hit flag");
        helper.succeed();
    }

    private static void damageNumbersSuppression(GameTestHelper helper) {
        ServerPlayer attacker = player(helper);
        Cow victim = target(helper);
        DamageSource source = victim.damageSources().playerAttack(attacker);
        DamageContainer canceled = trackedHit(victim, source, 7.5F, true);
        LivingIncomingDamageEvent canceledEvent = new LivingIncomingDamageEvent(victim, canceled);
        canceledEvent.setCanceled(true);
        DamageNumbers.trackHit(canceledEvent, true);
        helper.assertTrue(DamageNumbers.createNumber(resolvedHit(victim, canceled, 7.5F, 0.0F)) == null,
                "Canceled damage must not retain a displayable critical hit");

        DamageContainer zero = trackedHit(victim, source, 5.0F, true);
        helper.assertTrue(DamageNumbers.createNumber(resolvedHit(victim, zero, 0.0F, 0.0F)) == null,
                "Fully prevented damage must not display a number");
        helper.assertTrue(DamageNumbers.createNumber(resolvedHit(victim, zero, 5.0F, 0.0F)) == null,
                "A zero-damage result still consumes its hit context");

        DamageContainer invalid = trackedHit(victim, source, 5.0F, false);
        helper.assertTrue(DamageNumbers.createNumber(resolvedHit(victim, invalid, 5.0F,
                        Float.POSITIVE_INFINITY)) == null,
                "A non-finite result must not be sent to the client");
        helper.succeed();
    }

    private static void damageNumbersMeleeContext(GameTestHelper helper) {
        ServerPlayer attacker = player(helper);
        Cow victim = target(helper);
        LivingIncomingDamageEvent incoming = new LivingIncomingDamageEvent(victim,
                new DamageContainer(victim.damageSources().playerAttack(attacker), 5.0F));
        CriticalHitEvent critical = new CriticalHitEvent(attacker, victim, 1.5F, true);
        DamageNumbers.trackMeleeCritical(critical);
        // Another mod may change this event after our critical chance handler has run.
        critical.setCriticalHit(false);
        helper.assertFalse(DamageNumbers.consumeMeleeCritical(incoming),
                "The display must use the final critical event result");

        critical.setCriticalHit(true);
        DamageNumbers.trackMeleeCritical(critical);
        helper.assertTrue(DamageNumbers.consumeMeleeCritical(incoming),
                "A matching critical melee hit must retain its critical flag");
        helper.assertFalse(DamageNumbers.consumeMeleeCritical(incoming),
                "A melee critical flag must only apply once");

        DamageNumbers.trackMeleeCritical(critical);
        // Mock player doTick() does not advance tickCount. Wait for an actual
        // server tick to exercise the production end-of-tick cleanup instead.
        helper.runAfterDelay(1, () -> {
            helper.assertFalse(DamageNumbers.consumeMeleeCritical(incoming),
                    "A missed attack must not mark a later tick's damage as critical");
            helper.succeed();
        });
    }

    private static DamageContainer trackedHit(Cow victim, DamageSource source, float damage, boolean critical) {
        DamageContainer container = new DamageContainer(source, damage);
        DamageNumbers.trackHit(new LivingIncomingDamageEvent(victim, container), critical);
        return container;
    }

    private static LivingDamageEvent.Post resolvedHit(Cow victim, DamageContainer container,
                                                       float damage, float absorption) {
        container.setNewDamage(damage);
        container.captureInflictedDamage();
        container.setReduction(DamageContainer.Reduction.ABSORPTION, absorption);
        return new LivingDamageEvent.Post(victim, container);
    }

    private static void assertNumber(GameTestHelper helper, DamageNumberPayload number, float damage,
                                     boolean critical, String name) {
        helper.assertTrue(number != null, name + " should create a damage number");
        near(helper, number.damage(), damage, name + " amount");
        helper.assertTrue(number.critical() == critical, name + " critical flag");
    }

    private static Cow target(GameTestHelper helper) {
        Cow target = helper.spawnWithNoFreeWill(EntityTypes.COW, 2, 2, 2);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
        target.setHealth(40.0F);
        target.setNoGravity(true);
        return target;
    }

    private static void chargeAttack(ServerPlayer player) {
        for (int tick = 0; tick < 20; tick++) {
            player.doTick();
        }
    }

    private static void near(GameTestHelper helper, double actual, double expected, String name) {
        helper.assertTrue(Math.abs(actual - expected) < 0.0001,
                name + ": expected " + expected + ", got " + actual);
    }
}
