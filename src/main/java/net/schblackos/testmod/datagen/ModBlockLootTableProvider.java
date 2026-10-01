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


        /*SOLID DROPS*/

            //Normal Blocks
        dropSelf(ModBlocks.SILVER_BLOCK.get());
        dropSelf(ModBlocks.RAW_SILVER_BLOCK.get());

            //Stairs
        dropSelf(ModBlocks.TEST_STAIRS.get());

            //Slabs
        dropSelf(ModBlocks.TEST_SLAB.get());
        add(ModBlocks.TEST_SLAB.get(), this::createSlabItemTable);

            //Fences
        dropSelf(ModBlocks.TEST_FENCE.get());

            //Fencegates
        dropSelf(ModBlocks.TEST_FENCE_GATE.get());

            //Walls
        dropSelf(ModBlocks.TEST_WALL.get());

            //Doors
        dropSelf(ModBlocks.TEST_DOOR.get());
        add(ModBlocks.TEST_DOOR.get(), this::createDoorTable);

            //Trapdoors
        dropSelf(ModBlocks.TEST_TRAPDOOR.get());

            //Buttons
        dropSelf(ModBlocks.TEST_BUTTON.get());

            //Pressure Plates
        dropSelf(ModBlocks.TEST_PRESSURE_PLATE.get());


        //ADVANCED ORES

            //Test Block
        dropSelf(ModBlocks.TEST_BLOCK.get());

            //Test Lamp
        dropSelf(ModBlocks.TEST_LAMP.get());


        /*DROPS FROM ORES*/

        //Ores
        add(ModBlocks.SILVER_ORE.get(),createOreDrop(ModBlocks.SILVER_ORE.get(), ModItems.RAW_SILVER.get()));
        add(ModBlocks.DEEPSLATE_SILVER_ORE.get(),createOreDrop(ModBlocks.DEEPSLATE_SILVER_ORE.get(), ModItems.RAW_SILVER.get()));
        add(ModBlocks.NETHER_SILVER_ORE.get(),createOreDrop(ModBlocks.NETHER_SILVER_ORE.get(), ModItems.RAW_SILVER.get()));

        //createMultipleOreDrop minDrop maxDrop
    }

    //Converts the list of the blocks into a data type the call can use
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
