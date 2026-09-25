package net.schblackos.testmod.item;

import com.google.common.collect.Maps;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.tags.ModTags;

import java.util.Map;


public class ModArmorMaterial {
    public static final ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID =
            ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));

    //Make more keys for other sets
    public static final ResourceKey<EquipmentAsset> TEST_KEY =
            ResourceKey.create(ROOT_ID, Identifier.fromNamespaceAndPath(Testmod.MODID, "test"));


    public static final ArmorMaterial TEST_ARMOR_MATERIAL = new ArmorMaterial(
            1200,
            makeDefense(5, 7, 9, 5, 11),
            16,
            SoundEvents.ARMOR_EQUIP_IRON,
            2f,
            0.1f,
            ModTags.Items.SILVER_REPAIRABLE,
            TEST_KEY
    );


    private static Map<ArmorType, Integer > makeDefense(int boots,
                                                        int legs,
                                                        int chest,
                                                        int helm,
                                                        int body) {
        return Maps.newEnumMap(Map.of(
                ArmorType.BOOTS, boots,
                ArmorType.LEGGINGS, legs,
                ArmorType.CHESTPLATE, chest,
                ArmorType.HELMET, helm,
                ArmorType.BODY, body));
    }

}
