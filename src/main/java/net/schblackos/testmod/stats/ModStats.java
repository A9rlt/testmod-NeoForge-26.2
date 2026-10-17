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

/** Shared player/equipment stats. Percentage values use 1 = 1%, not 0.01. */
public final class ModStats {
    public static final double BASE_DAMAGE = 1.0D;
    public static final double BASE_CRITICAL_CHANCE = 0.0D;
    public static final double BASE_DEFENCE = 0.0D;
    public static final double BASE_RESISTANCE = 0.0D;
    public static final double BASE_EVASION = 0.0D;

    // Reuse these vanilla attributes so attack cooldown, equipment, and the armour bar work.
    public static final Holder<Attribute> DAMAGE = Attributes.ATTACK_DAMAGE;
    public static final Holder<Attribute> DEFENCE = Attributes.ARMOR;

    private static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, Testmod.MODID);

    public static final Holder<Attribute> CRITICAL_CHANCE = percentage("critical_chance", BASE_CRITICAL_CHANCE);
    public static final Holder<Attribute> RESISTANCE = percentage("resistance", BASE_RESISTANCE);
    public static final Holder<Attribute> EVASION = percentage("evasion", BASE_EVASION);

    private ModStats() {
    }

    private static Holder<Attribute> percentage(String name, double base) {
        return ATTRIBUTES.register(name, () -> new RangedAttribute(
                "attribute.testmod." + name, base, 0.0D, 100.0D).setSyncable(true));
    }

    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
        bus.addListener(ModStats::addEntityAttributes);
        bus.addListener(ModItemStats::modifyItemDefaults);
    }

    private static void addEntityAttributes(EntityAttributeModificationEvent event) {
        DAMAGE.value().setSyncable(true);
        // Mobs can wear the same armour, so they need the same attributes as players.
        for (var type : event.getTypes()) {
            event.add(type, CRITICAL_CHANCE);
            event.add(type, RESISTANCE);
            event.add(type, EVASION);
        }
        event.add(EntityTypes.PLAYER, DAMAGE, BASE_DAMAGE);
        event.add(EntityTypes.PLAYER, DEFENCE, BASE_DEFENCE);
    }
}
