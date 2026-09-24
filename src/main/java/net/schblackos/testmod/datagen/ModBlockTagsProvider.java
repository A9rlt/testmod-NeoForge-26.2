package net.schblackos.testmod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.block.ModBlocks;
import net.schblackos.testmod.tags.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Testmod.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.getRK(ModBlocks.SILVER_ORE.get()))
                .add(ModBlocks.getRK(ModBlocks.DEEPSLATE_SILVER_ORE.get()))
                .add(ModBlocks.getRK(ModBlocks.NETHER_SILVER_ORE.get()))
                .add(ModBlocks.getRK(ModBlocks.RAW_SILVER_BLOCK.get()))
                .add(ModBlocks.getRK(ModBlocks.SILVER_BLOCK.get()))
                .add(ModBlocks.getRK(ModBlocks.TEST_BLOCK.get()))
                .add(ModBlocks.getRK(ModBlocks.TEST_SLAB.get()))
                .add(ModBlocks.getRK(ModBlocks.TEST_STAIRS.get()));

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.getRK(ModBlocks.SILVER_ORE.get()))
                .add(ModBlocks.getRK(ModBlocks.DEEPSLATE_SILVER_ORE.get()))
                .add(ModBlocks.getRK(ModBlocks.NETHER_SILVER_ORE.get()));


        tag(ModTags.Blocks.METAL_DETECTABLES)
                .addTag(Tags.Blocks.ORES);


        tag(BlockTags.STAIRS)
                .add(ModBlocks.getRK(ModBlocks.TEST_STAIRS.get()));

        tag(BlockTags.SLABS)
                .add(ModBlocks.getRK(ModBlocks.TEST_SLAB.get()));

        tag(BlockTags.PRESSURE_PLATES)
                .add(ModBlocks.getRK(ModBlocks.TEST_PRESSURE_PLATE.get()));

        tag(BlockTags.BUTTONS)
                .add(ModBlocks.getRK(ModBlocks.TEST_BUTTON.get()));

        //Allows connection to other fences/walls (vanilla)
        tag(BlockTags.FENCES).add(ModBlocks.TEST_FENCE.getKey());
        tag(BlockTags.FENCE_GATES).add(ModBlocks.TEST_FENCE_GATE.getKey());
        tag(BlockTags.WALLS).add(ModBlocks.TEST_WALL.getKey());
    }
}
