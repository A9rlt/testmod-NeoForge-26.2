package net.schblackos.testmod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.block.ModBlocks;

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
                .add(ModBlocks.getRK(ModBlocks.SILVER_BLOCK.get()));

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.getRK(ModBlocks.SILVER_ORE.get()))
                .add(ModBlocks.getRK(ModBlocks.DEEPSLATE_SILVER_ORE.get()))
                .add(ModBlocks.getRK(ModBlocks.NETHER_SILVER_ORE.get()));

    }
}
