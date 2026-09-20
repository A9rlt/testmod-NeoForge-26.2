package net.schblackos.testmod.tags;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.schblackos.testmod.Testmod;

public class ModTags {
    public static class Blocks {

        public static final TagKey<Block> METAL_DETECTABLES = createTag("metal_detectables");

        private static TagKey<Block> createTag(String name){
            return  BlockTags.create(Identifier.fromNamespaceAndPath(Testmod.MODID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> TRANSFORMABLE_ITEMS = createTag("transformable_items");

        private static TagKey <Item> createTag(String name){
            return  ItemTags.create(Identifier.fromNamespaceAndPath(Testmod.MODID, name));
        }
    }
}
