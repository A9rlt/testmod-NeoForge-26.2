package net.schblackos.testmod.tags;

import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.block.Block;
import net.schblackos.testmod.Testmod;

public class ModTags {
    public static class DamageTypes {
        public static final TagKey<DamageType> MAGIC_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE,
                Identifier.fromNamespaceAndPath(Testmod.MODID, "magic_damage"));
    }

    public static class Blocks {

        public static final TagKey<Block> METAL_DETECTABLES = createTag("metal_detectables");

        public static final TagKey<Block> NEEDS_SILVER_TOOL = createTag("needs_azurite_tool");
        public static final TagKey<Block> INCORRECT_FOR_SILVER_TOOL = createTag("incorrect_for_azurite_tool");

        private static TagKey<Block> createTag(String name){
            return  BlockTags.create(Identifier.fromNamespaceAndPath(Testmod.MODID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");

        public static final TagKey<Item> SILVER_REPAIRABLE = createTag("silver_repairable");

        private static TagKey <Item> createTag(String name){
            return  ItemTags.create(Identifier.fromNamespaceAndPath(Testmod.MODID, name));
        }
    }
}
