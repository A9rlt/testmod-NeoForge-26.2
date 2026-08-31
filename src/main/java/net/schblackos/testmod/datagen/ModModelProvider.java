package net.schblackos.testmod.datagen;


import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.block.ModBlocks;
import net.schblackos.testmod.item.ModItems;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Testmod.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels){

        /*ITEM MODELS*/
        itemModels.generateFlatItem(ModItems.SILVER_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RAW_SILVER.get(), ModelTemplates.FLAT_ITEM);


        /*BLOCK MODELS*/
        blockModels.createTrivialCube(ModBlocks.SILVER_BLOCK.get());

    }
}
