package net.schblackos.testmod.stats;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.item.ModItemStats;

public final class ModCustomStats {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Testmod.MODID);

    public static final double BASE_DAMAGE = 1.0D;
    public static final double BASE_MAGIC_DAMAGE = 0.0D;
    public static final double BASE_CRITICAL_CHANCE = 0.0D;
    public static final double BASE_DEFENCE = 0.0D;
    public static final double BASE_RESISTANCE = 0.0D;
    public static final double BASE_EVASION = 0.0D;
    public static final double BASE_ATTACK_SPEED = 4.0D;
    public static final double BASE_FIRE_RATE = 2.0D;

    // Vanilla attributes reused alongside custom combat attributes.

    /*ITEMS*/
    public static final Holder<Attribute> DAMAGE = Attributes.ATTACK_DAMAGE;
    public static final Holder<Attribute> CRITICAL_CHANCE = percentage("critical_chance", BASE_CRITICAL_CHANCE);
    public static final Holder<Attribute> MAGIC_DAMAGE = flat("magic_damage", BASE_MAGIC_DAMAGE);
    public static final Holder<Attribute> ATTACK_SPEED = Attributes.ATTACK_SPEED;
    public static final Holder<Attribute> FIRE_RATE = fireRate("fire_rate", BASE_FIRE_RATE);

    /*ARMOUR*/
    public static final Holder<Attribute> DEFENCE = Attributes.ARMOR;
    public static final Holder<Attribute> RESISTANCE = flat("resistance", BASE_RESISTANCE);
    public static final Holder<Attribute> EVASION = percentage("evasion", BASE_EVASION);

    private ModCustomStats() {
    }

    private static Holder<Attribute> percentage(String name, double base) {
        return ATTRIBUTES.register(name, () -> new RangedAttribute(
                "attribute.testmod." + name, base,
                0.0D,
                100.0D)
                .setSyncable(true));
    }

    private static Holder<Attribute> fireRate(String name, double base) {
        return ATTRIBUTES.register(name, () -> new RangedAttribute(
                "attribute.testmod." + name, base,
                0.1D,
                100.0D)
                .setSyncable(true));
    }

    private static Holder<Attribute> flat(String name, double base) {
        return ATTRIBUTES.register(name, () -> new RangedAttribute(
                "attribute.testmod." + name, base,
                0.0D,
                2048.0D)
                .setSyncable(true));
    }

    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
        bus.addListener(ModCustomStats::addEntityAttributes);
        bus.addListener(ModItemStats::modifyItemDefaults);
    }

    private static void addEntityAttributes(EntityAttributeModificationEvent event) {
        DAMAGE.value().setSyncable(true);

        for (var type : event.getTypes()) {
            event.add(type, CRITICAL_CHANCE);
            event.add(type, MAGIC_DAMAGE);
            event.add(type, RESISTANCE);
            event.add(type, EVASION);
            event.add(type, FIRE_RATE);
            event.add(type, ATTACK_SPEED);
        }
        event.add(EntityTypes.PLAYER, DAMAGE, BASE_DAMAGE);
        event.add(EntityTypes.PLAYER, DEFENCE, BASE_DEFENCE);
    }
}
