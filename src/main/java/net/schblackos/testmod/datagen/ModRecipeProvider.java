package net.schblackos.testmod.datagen;

import com.jcraft.jorbis.Block;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.block.ModBlocks;
import net.schblackos.testmod.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "TestMod Recipes";
        }
    }


    @Override
    protected void buildRecipes() {


        /*MISC SECTION*/

            //Shaped Recipes
        shaped(
                RecipeCategory.MISC,
                ModBlocks.SILVER_BLOCK.get())

                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', ModItems.SILVER_INGOT.get())

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModItems.SILVER_INGOT))
                .group("silver")
                .save(output);

        shaped(
                RecipeCategory.TOOLS,
                ModItems.TEST_SWORD.get())

                .pattern("X")
                .pattern("X")
                .pattern("O")
                .define('X', ModItems.RAW_SILVER.get())
                .define('O', Items.STICK)

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModItems.SILVER_INGOT))
                .group("silver")
                .save(output);

        shaped(
                RecipeCategory.TOOLS,
                ModItems.TEST_PICKAXE.get())

                .pattern("XXX")
                .pattern(" O ")
                .pattern(" O ")
                .define('X', ModItems.RAW_SILVER.get())
                .define('O', Items.STICK)

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModItems.SILVER_INGOT))
                .group("silver")
                .save(output);

        shaped(
                RecipeCategory.TOOLS,
                ModItems.TEST_AXE.get())

                .pattern("XX")
                .pattern("OX")
                .pattern("O ")
                .define('X', ModItems.RAW_SILVER.get())
                .define('O', Items.STICK)

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModItems.SILVER_INGOT))
                .group("silver")
                .save(output);

        shaped(
                RecipeCategory.TOOLS,
                ModItems.TEST_SHOVEL.get())

                .pattern("X")
                .pattern("O")
                .pattern("O")
                .define('X', ModItems.RAW_SILVER.get())
                .define('O', Items.STICK)

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModItems.SILVER_INGOT))
                .group("silver")
                .save(output);

        shaped(
                RecipeCategory.TOOLS,
                ModItems.TEST_HOE.get())

                .pattern("XX")
                .pattern("O ")
                .pattern("O ")
                .define('X', ModItems.RAW_SILVER.get())
                .define('O', Items.STICK)

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModItems.SILVER_INGOT))
                .group("silver")
                .save(output);

        shaped(
                RecipeCategory.TOOLS,
                ModItems.TEST_SPEAR.get())

                .pattern("  X")
                .pattern(" O ")
                .pattern("O  ")
                .define('X', ModItems.RAW_SILVER.get())
                .define('O', Items.STICK)

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModItems.SILVER_INGOT))
                .group("silver")
                .save(output);

            //Shapeless Recipes
        shapeless(
                RecipeCategory.MISC,
                ModItems.SILVER_INGOT.get(), 9)
                .requires(ModBlocks.SILVER_BLOCK)
                .unlockedBy(getHasName(ModBlocks.SILVER_BLOCK.get()), has(ModBlocks.SILVER_BLOCK))
                .group("silver")
                .save(output /*add {,"silver from ______"} for duplicate recipes*/);


            //Stair Recipes
        stairBuilder(ModBlocks.TEST_STAIRS.get(), Ingredient.of(ModBlocks.SILVER_BLOCK.get()))

                .unlockedBy(getHasName(ModBlocks.SILVER_BLOCK.get()), has(ModBlocks.SILVER_BLOCK))
                .group("test_blocks")
                .save(output);

            //Slab Recipes
        slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.TEST_SLAB.get(), ModBlocks.SILVER_BLOCK.get());

            //Button Recipes
        buttonBuilder(ModBlocks.TEST_BUTTON.get(), Ingredient.of(ModItems.SILVER_INGOT.get()))

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModBlocks.SILVER_BLOCK))
                .group("test_blocks")
                .save(output);

            //Pressure Plates Recipes
        pressurePlate(ModBlocks.TEST_PRESSURE_PLATE.get(), ModItems.SILVER_INGOT.get());

            //Fence recipes
        fenceBuilder(ModBlocks.TEST_FENCE.get(), Ingredient.of(ModItems.SILVER_INGOT.get()))

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModBlocks.SILVER_BLOCK))
                .group("test_blocks")
                .save(output);

            //Fencegates
        fenceGateBuilder(ModBlocks.TEST_FENCE_GATE.get(), Ingredient.of(ModItems.SILVER_INGOT.get()))

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModBlocks.SILVER_BLOCK))
                .group("test_blocks")
                .save(output);

            //Walls
        wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.TEST_WALL.get(), ModBlocks.SILVER_BLOCK.get());

            //Doors
        doorBuilder(ModBlocks.TEST_DOOR.get(), Ingredient.of(ModItems.SILVER_INGOT.get()))

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModBlocks.SILVER_BLOCK))
                .group("test_blocks")
                .save(output);

            //Trapdoors
        trapdoorBuilder(ModBlocks.TEST_TRAPDOOR.get(), Ingredient.of(ModItems.SILVER_INGOT.get()))

                .unlockedBy(getHasName(ModItems.SILVER_INGOT.get()), has(ModBlocks.SILVER_BLOCK))
                .group("test_blocks")
                .save(output);



        /*ITEM RECIPES*/

        List<ItemLike> SILVER_SMELTABLES_INGOTS = List.of(
                ModItems.RAW_SILVER,
                ModBlocks.SILVER_ORE,
                ModBlocks.DEEPSLATE_SILVER_ORE,
                ModBlocks.NETHER_SILVER_ORE);



        List<ItemLike> SMELTABLES_BLOCKS = List.of(
                ModItems.RAW_SILVER
        );


        /*SMELTING AND BLASTING*/

            //Smelting
        oreSmelting(SILVER_SMELTABLES_INGOTS,

                RecipeCategory.MISC,
                CookingBookCategory.MISC,
                ModItems.SILVER_INGOT.get() ,
                0.25f,
                200,
                "silver");


            //Blasting
        oreBlasting(SILVER_SMELTABLES_INGOTS,

                RecipeCategory.MISC,
                CookingBookCategory.MISC,
                ModItems.SILVER_INGOT.get(),
                0.25f,
                100,
                "silver");


    }

    @Override
    protected <T extends AbstractCookingRecipe> void oreCooking(AbstractCookingRecipe.Factory<T> factory, List<ItemLike> smeltables,
                                                                RecipeCategory craftingCategory, CookingBookCategory cookingCategory, ItemLike result,
                                                                float experience, int cookingTime, String group, String fromDesc) {
        for (ItemLike itemlike : smeltables) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), craftingCategory, cookingCategory, result, experience, cookingTime, factory).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(output, Testmod.MODID + ":" + getItemName(result) + fromDesc + "_" + getItemName(itemlike));


        }
    }
}