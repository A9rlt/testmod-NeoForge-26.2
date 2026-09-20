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
    public static final Supplier<CreativeModeTab> SILVER_ITEMS_TAB = CREATIVE_MODE_TABS.register("silver_items_tab",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SILVER_INGOT.get()))
                .title(Component.translatable("creativetab.testmod.silver_items"))
                .displayItems((itemDisplayParameters, output) -> {

                    /*ModItems*/

                        //NORMAL ITEMS
                    output.accept(ModItems.SILVER_INGOT);
                    output.accept(ModItems.RAW_SILVER);

                        //ADVANCED ITEMS
                    output.accept(ModItems.TEST_ITEM);

                        //FOODS
                    output.accept(ModItems.ONION);


                        //FUELS
                    output.accept(ModItems.BROWN_COAL);


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

                        //Doors

                        //Trapdoors

                        //Buttons

                        //Pressure Plates

                        //ADVANCED BLOCKS
                    output.accept(ModBlocks.TEST_BLOCK);

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
