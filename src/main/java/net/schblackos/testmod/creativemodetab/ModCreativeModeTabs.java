package net.schblackos.testmod.creativemodetab;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.block.ModBlocks;
import net.schblackos.testmod.item.ModItems;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Testmod.MODID);

    // Adds tab for the items use .withTabsBefore/After to choose where to place the tabs
    public static final Supplier<CreativeModeTab> TESTMOD_TAB = CREATIVE_MODE_TABS.register("testmod_tab",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SILVER_INGOT.get()))
                .title(Component.translatable("creativetab.testmod.silver_items"))
                .displayItems((itemDisplayParameters, output) -> {

                    /*ModItems*/

                        //NORMAL ITEMS
                    output.accept(ModItems.SILVER_INGOT);
                    output.accept(ModItems.RAW_SILVER);

                        //ADVANCED ITEMS
                    output.accept(ModItems.TEST_ITEM);

                        //Foods
                    output.accept(ModItems.TEST_FOOD);


                        //Fuels
                    output.accept(ModItems.TEST_FUEL);

                        //Tools
                    output.accept(ModItems.TEST_SWORD);
                    output.accept(ModItems.TEST_PICKAXE);
                    output.accept(ModItems.TEST_SHOVEL);
                    output.accept(ModItems.TEST_AXE);
                    output.accept(ModItems.TEST_HOE);
                    output.accept(ModItems.TEST_SPEAR);

                        //Armor
                    output.accept(ModItems.TEST_HELMET);
                    output.accept(ModItems.TEST_CHESTPLATE);
                    output.accept(ModItems.TEST_LEGGINGS);
                    output.accept(ModItems.TEST_BOOTS);


                    /*ModBlocks*/

                    //NORMAL BLOCKS
                    output.accept(ModBlocks.RAW_SILVER_BLOCK);
                    output.accept(ModBlocks.SILVER_BLOCK);
                    output.accept(ModBlocks.DEEPSLATE_SILVER_ORE);
                    output.accept(ModBlocks.SILVER_ORE);
                    output.accept(ModBlocks.NETHER_SILVER_ORE);

                        //Slabs
                    output.accept(ModBlocks.TEST_SLAB);

                        //Stairs
                    output.accept(ModBlocks.TEST_STAIRS);

                        //Fences
                    output.accept(ModBlocks.TEST_FENCE);

                        //Fencegates
                    output.accept(ModBlocks.TEST_FENCE_GATE);

                        //Walls
                    output.accept(ModBlocks.TEST_WALL);

                        //Doors
                    output.accept(ModBlocks.TEST_DOOR);

                        //Trapdoors
                    output.accept(ModBlocks.TEST_TRAPDOOR);

                        //Buttons
                    output.accept(ModBlocks.TEST_BUTTON);

                        //Pressure Plates
                    output.accept(ModBlocks.TEST_PRESSURE_PLATE);

                    //ADVANCED BLOCKS

                        //Test Block
                    output.accept(ModBlocks.TEST_BLOCK);

                        //Test Lamp
                    output.accept(ModBlocks.TEST_LAMP);

                }).build());

    // Adds tab for blocks
    public static final Supplier<CreativeModeTab> SILVER_BLOCKS_TAB = CREATIVE_MODE_TABS.register("silver_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SILVER_INGOT.get()))
                    .title(Component.translatable("creativetab.testmod.silver_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {


                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
