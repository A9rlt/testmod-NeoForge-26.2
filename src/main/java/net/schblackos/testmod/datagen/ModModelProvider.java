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

            //Items
        itemModels.generateFlatItem(ModItems.SILVER_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RAW_SILVER.get(), ModelTemplates.FLAT_ITEM);

            //Advanced items
        itemModels.generateFlatItem(ModItems.TEST_ITEM.get(), ModelTemplates.FLAT_ITEM);

            //Foods
        itemModels.generateFlatItem(ModItems.ONION.get(), ModelTemplates.FLAT_ITEM);

            //Fuels
        itemModels.generateFlatItem(ModItems.BROWN_COAL.get(), ModelTemplates.FLAT_ITEM);



        /*BLOCK MODELS*/

            //Blocks
        //blockModels.createTrivialCube(ModBlocks.SILVER_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.RAW_SILVER_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.SILVER_ORE.get());
        blockModels.createTrivialCube(ModBlocks.DEEPSLATE_SILVER_ORE.get());
        blockModels.createTrivialCube(ModBlocks.NETHER_SILVER_ORE.get());


            //Block Families
        blockModels.family(ModBlocks.SILVER_BLOCK.get())
                .stairs(ModBlocks.TEST_STAIRS.get())
                .slab(ModBlocks.TEST_SLAB.get())
                .pressurePlate(ModBlocks.TEST_PRESSURE_PLATE.get())
                .button(ModBlocks.TEST_BUTTON.get())
                .fence(ModBlocks.TEST_FENCE.get())
                .fenceGate(ModBlocks.TEST_FENCE_GATE.get())
                .wall(ModBlocks.TEST_WALL.get())
                .door(ModBlocks.TEST_DOOR.get())
                .trapdoor(ModBlocks.TEST_TRAPDOOR.get());
                //Trapdoor has to be the final in a family cuz it pulls a void


            //Advanced Blocks
        blockModels.createTrivialCube(ModBlocks.TEST_BLOCK.get());

    }
}
