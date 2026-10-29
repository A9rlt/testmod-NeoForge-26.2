package net.schblackos.testmod.stats;

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
import net.schblackos.testmod.item.ModItems;

import java.util.ArrayList;

/** Edit item values here. Unlisted items keep their existing damage and defence. */
public final class ModItemStats {
    private ModItemStats() {
    }

    public static void modifyItemDefaults(ModifyDefaultComponentsEvent event) {
        // Damage here is the total with the default player base damage of 1.
        weapon(event, Items.WOODEN_SWORD, 5.0D, 1.0D);


        // Preserve current arrow damage; add a 1% critical chance to these bows.
        rangedWeapon(event, Items.BOW, 0.0D, 1.0D);
        rangedWeapon(event, ModItems.TEST_BOW, 0.0D, 1.0D);
        rangedWeapon(event, ModItems.TEST_CUSTOMBOW_ITEM, 0.0D, 1.0D);

        // Defence, Resistance %, Evasion %. Vanilla armour already supplies Defence.
        armour(event, ModItems.TEST_HELMET, 5.0D, 2.0D, 1.0D);
        armour(event, ModItems.TEST_CHESTPLATE, 9.0D, 2.0D, 1.0D);
        armour(event, ModItems.TEST_LEGGINGS, 7.0D, 2.0D, 1.0D);
        armour(event, ModItems.TEST_BOOTS, 5.0D, 2.0D, 1.0D);
    }

    public static void weapon(ModifyDefaultComponentsEvent event, ItemLike item,
                              double totalDamage, double criticalChance) {
        validateNonNegative(totalDamage, "Damage");
        configureWeapon(event, item, totalDamage - ModStats.BASE_DAMAGE, criticalChance);
    }

    /** For bows the damage value is a bonus added to each arrow's normal damage. */
    public static void rangedWeapon(ModifyDefaultComponentsEvent event, ItemLike item,
                                    double damageBonus, double criticalChance) {
        validateNonNegative(damageBonus, "Projectile damage bonus");
        configureWeapon(event, item, damageBonus, criticalChance);
    }

    private static void configureWeapon(ModifyDefaultComponentsEvent event, ItemLike item,
                                         double damageBonus, double criticalChance) {
        validatePercentage(criticalChance, "Critical Chance");
        event.modify(item, (components, lookup, currentItem) -> {
            var entries = new ArrayList<>(components.getOrDefault(
                    DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers());
            entries.removeIf(entry -> entry.slot() == EquipmentSlotGroup.MAINHAND
                    && (entry.attribute().equals(ModStats.DAMAGE)
                    || entry.attribute().equals(ModStats.CRITICAL_CHANCE)));
            // The vanilla ID lets the tooltip display base 1 + item 4 as Damage 5.
            entries.add(new ItemAttributeModifiers.Entry(ModStats.DAMAGE,
                    new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, damageBonus,
                            AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
            add(entries, ModStats.CRITICAL_CHANCE, "critical_chance", criticalChance, EquipmentSlotGroup.MAINHAND);
            components.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(entries));
        });
    }

    public static void armour(ModifyDefaultComponentsEvent event, ItemLike item,
                              double defence, double resistance, double evasion) {
        validateNonNegative(defence, "Defence");
        validatePercentage(resistance, "Resistance");
        validatePercentage(evasion, "Evasion");
        event.modify(item, (components, lookup, currentItem) -> {
            var equippable = components.get(DataComponents.EQUIPPABLE);
            if (equippable == null || !equippable.slot().isArmor()) {
                throw new IllegalArgumentException("Armour stats require an armour equipment slot: " + currentItem);
            }
            var slot = EquipmentSlotGroup.bySlot(equippable.slot());
            var entries = new ArrayList<>(components.getOrDefault(
                    DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers());
            entries.removeIf(entry -> entry.attribute().equals(ModStats.DEFENCE)
                    || entry.attribute().equals(ModStats.RESISTANCE)
                    || entry.attribute().equals(ModStats.EVASION));
            add(entries, ModStats.DEFENCE, "defence", defence, slot);
            add(entries, ModStats.RESISTANCE, "resistance", resistance, slot);
            add(entries, ModStats.EVASION, "evasion", evasion, slot);
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
