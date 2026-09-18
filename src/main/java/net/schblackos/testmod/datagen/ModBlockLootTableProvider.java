package net.schblackos.testmod.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.schblackos.testmod.block.ModBlocks;
import net.schblackos.testmod.item.ModItems;


import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public  ModBlockLootTableProvider(HolderLookup.Provider registries) {
         super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {

        //SOLID BLOCKS
        dropSelf(ModBlocks.SILVER_BLOCK.get());
        dropSelf(ModBlocks.RAW_SILVER_BLOCK.get());


        //TABLE DROPS
        /*
        createMultipleOreDrop minDrop maxDrop
        */
        add(ModBlocks.SILVER_ORE.get(),createOreDrop(ModBlocks.SILVER_ORE.get(), ModItems.RAW_SILVER.get()));
        add(ModBlocks.DEEPSLATE_SILVER_ORE.get(),createOreDrop(ModBlocks.DEEPSLATE_SILVER_ORE.get(), ModItems.RAW_SILVER.get()));
        add(ModBlocks.NETHER_SILVER_ORE.get(),createOreDrop(ModBlocks.NETHER_SILVER_ORE.get(), ModItems.RAW_SILVER.get()));
    }

    //Converts the list of the blocks into a data type the call can use
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
