package net.schblackos.testmod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.item.ModItems;
import net.schblackos.testmod.tags.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
     public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Testmod.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {

         tag(ModTags.Items.TRANSFORMABLE_ITEMS)
                 .add(ItemIds.IRON_INGOT)
                 .add(ItemIds.COPPER_INGOT)
                 .add(ModItems.getRK(ModItems.SILVER_INGOT.get()))
                 .add(BlockItemIds.REDSTONE_DUST.item());



    }
}
