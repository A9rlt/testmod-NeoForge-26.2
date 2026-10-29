package net.schblackos.testmod.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.stats.ModCustomStats;

import java.util.ArrayList;

/* Edit item values here. Unlisted items keep their existing damage and defence. */
public final class ModItemStats {
    private ModItemStats() {
    }

    public static void modifyItemDefaults(ModifyDefaultComponentsEvent event) {
        //Damage here is the total with the default player base damage of 1.

        //Overriding vanilla weapons
        weapon(event, Items.WOODEN_SWORD, 5.0D, 1.0D);

        //Preserve current arrow damage; add a 1% critical chance to these bows.
        //Overriding bow and arrow damage
        rangedWeapon(event, Items.BOW, 0.0D, 1.0D);
        rangedWeapon(event, ModItems.TEST_BOW, 0.0D, 1.0D);
        rangedWeapon(event, ModItems.TEST_CUSTOMBOW_ITEM, 0.0D, 1.0D);

        //Defence, flat magic Resistance, Evasion %. Vanilla armour already supplies Defence.
        //Overriding vanilla armour
        armour(event, Items.LEATHER_HELMET, 1.0D, 0.0D, 0.0D);
        armour(event, Items.LEATHER_CHESTPLATE, 2.0D, 0.0D, 0.0D);
        armour(event, Items.LEATHER_LEGGINGS, 1.0D, 0.0D, 0.0D);
        armour(event, Items.LEATHER_BOOTS, 1.0D, 0.0D, 0.0D);

        //Magic Damage Items
        weapon(event, ModItems.TEST_STAFF, 1d, 5d, 0d);
    }

    public static void weapon(ModifyDefaultComponentsEvent event, ItemLike item,
                              double totalDamage, double criticalChance) {
        weapon(event, item, totalDamage, 0.0D, criticalChance);
    }

    /* Physical total with base Damage 1, additional magic damage, critical chance %. */
    public static void weapon(ModifyDefaultComponentsEvent event, ItemLike item,
                              double totalDamage, double magicDamage, double criticalChance) {
        validateNonNegative(totalDamage, "Damage");
        configureWeapon(event, item, totalDamage - ModCustomStats.BASE_DAMAGE, magicDamage, criticalChance);
    }

    /* For bows the damage value is a bonus added to each arrow's normal damage. */
    public static void rangedWeapon(ModifyDefaultComponentsEvent event, ItemLike item,
                                    double damageBonus, double criticalChance) {
        rangedWeapon(event, item, damageBonus, 0.0D, criticalChance);
    }

    public static void rangedWeapon(ModifyDefaultComponentsEvent event, ItemLike item,
                                    double damageBonus, double magicDamage, double criticalChance) {
        validateNonNegative(damageBonus, "Projectile damage bonus");
        configureWeapon(event, item, damageBonus, magicDamage, criticalChance);
    }

    private static void configureWeapon(ModifyDefaultComponentsEvent event, ItemLike item,
                                         double damageBonus, double magicDamage, double criticalChance) {
        validateNonNegative(magicDamage, "Magic Damage");
        validatePercentage(criticalChance, "Critical Chance");
        event.modify(item, (components, lookup, currentItem) -> {
            var entries = new ArrayList<>(components.getOrDefault(
                    DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers());
            entries.removeIf(entry -> entry.slot() == EquipmentSlotGroup.MAINHAND
                    && (entry.attribute().equals(ModCustomStats.DAMAGE)
                    || entry.attribute().equals(ModCustomStats.MAGIC_DAMAGE)
                    || entry.attribute().equals(ModCustomStats.CRITICAL_CHANCE)));
            // The vanilla ID lets the tooltip display base 1 + item 4 as Damage 5.
            entries.add(new ItemAttributeModifiers.Entry(ModCustomStats.DAMAGE,
                    new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, damageBonus,
                            AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
            add(entries, ModCustomStats.CRITICAL_CHANCE, "critical_chance", criticalChance, EquipmentSlotGroup.MAINHAND);
            add(entries, ModCustomStats.MAGIC_DAMAGE, "magic_damage", magicDamage, EquipmentSlotGroup.MAINHAND);
            components.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(entries));
        });
    }

    public static void armour(ModifyDefaultComponentsEvent event, ItemLike item,
                              double defence, double resistance, double evasion) {
        validateNonNegative(defence, "Defence");
        validateNonNegative(resistance, "Resistance");
        validatePercentage(evasion, "Evasion");
        event.modify(item, (components, lookup, currentItem) -> {
            var equippable = components.get(DataComponents.EQUIPPABLE);
            if (equippable == null || !equippable.slot().isArmor()) {
                throw new IllegalArgumentException("Armour stats require an armour equipment slot: " + currentItem);
            }
            var slot = EquipmentSlotGroup.bySlot(equippable.slot());
            var entries = new ArrayList<>(components.getOrDefault(
                    DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers());
            entries.removeIf(entry -> entry.attribute().equals(ModCustomStats.DEFENCE)
                    || entry.attribute().equals(ModCustomStats.RESISTANCE)
                    || entry.attribute().equals(ModCustomStats.EVASION));
            add(entries, ModCustomStats.DEFENCE, "defence", defence, slot);
            add(entries, ModCustomStats.RESISTANCE, "resistance", resistance, slot);
            add(entries, ModCustomStats.EVASION, "evasion", evasion, slot);
            components.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(entries));
        });
    }

    private static void add(ArrayList<ItemAttributeModifiers.Entry> entries, Holder<Attribute> attribute,
                             String name, double amount, EquipmentSlotGroup slot) {
        if (amount == 0.0D) {
            return;
        }
        // Different slot IDs let all four pieces stack and unequip independently.
        var id = Identifier.fromNamespaceAndPath(Testmod.MODID, "stats/" + name + "/" + slot.getSerializedName());
        entries.add(new ItemAttributeModifiers.Entry(attribute,
                new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE), slot));
    }

    private static void validateNonNegative(double value, String stat) {
        if (!Double.isFinite(value) || value < 0.0D) {
            throw new IllegalArgumentException(stat + " must be a finite non-negative value");
        }
    }

    private static void validatePercentage(double value, String stat) {
        validateNonNegative(value, stat);
        if (value > 100.0D) {
            throw new IllegalArgumentException(stat + " must be between 0 and 100");
        }
    }
}
