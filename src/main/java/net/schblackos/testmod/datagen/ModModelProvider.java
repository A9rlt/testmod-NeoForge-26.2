package net.schblackos.testmod.datagen;


import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.block.ModBlocks;
import net.schblackos.testmod.item.ModArmorMaterial;
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
        itemModels.generateFlatItem(ModItems.TEST_FOOD.get(), ModelTemplates.FLAT_ITEM);

            //Fuels
        itemModels.generateFlatItem(ModItems.TEST_FUEL.get(), ModelTemplates.FLAT_ITEM);

            //Tools
        itemModels.generateFlatItem(ModItems.TEST_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.TEST_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.TEST_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.TEST_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.TEST_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateSpear(ModItems.TEST_SPEAR.get());

            //Armor
        itemModels.generateTrimmableItem(ModItems.TEST_HELMET.get(), ModArmorMaterial.TEST_KEY,
                ItemModelGenerators.TRIM_PREFIX_HELMET, false);
        itemModels.generateTrimmableItem(ModItems.TEST_CHESTPLATE.get(), ModArmorMaterial.TEST_KEY,
                ItemModelGenerators.TRIM_PREFIX_CHESTPLATE, false);
        itemModels.generateTrimmableItem(ModItems.TEST_LEGGINGS.get(), ModArmorMaterial.TEST_KEY,
                ItemModelGenerators.TRIM_PREFIX_LEGGINGS, false);
        itemModels.generateTrimmableItem(ModItems.TEST_BOOTS.get(), ModArmorMaterial.TEST_KEY,
                ItemModelGenerators.TRIM_PREFIX_BOOTS, false);

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
