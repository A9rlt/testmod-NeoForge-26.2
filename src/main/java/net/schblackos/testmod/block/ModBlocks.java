package net.schblackos.testmod.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.block.custom.MagicSpeedBlock;
import net.schblackos.testmod.item.ModItems;

import java.util.function.Consumer;
import java.util.function.Function;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Testmod.MODID);

    // Generates the new block alongside its properties

    //.noLootTable

    /*NORMAL BLOCKS*/
    public static final DeferredBlock<Block> SILVER_BLOCK = registerBlock("silver_block",
            properties -> new Block(
                    properties.strength(4f)
                            .sound(SoundType.IRON)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> RAW_SILVER_BLOCK = registerBlock("raw_silver_block",
            properties -> new Block(
                    properties.strength(4f)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> SILVER_ORE = registerBlock("silver_ore",
            properties -> new Block(
                    properties.strength(4f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DEEPSLATE_SILVER_ORE = registerBlock("deepslate_silver_ore",
            properties -> new Block(
                    properties.strength(4f)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> NETHER_SILVER_ORE = registerBlock("nether_silver_ore",
            properties -> new Block(
                    properties.strength(4f)
                            .sound(SoundType.NETHERRACK)
                            .requiresCorrectToolForDrops()));




    /*ADVANCED BLOCKS*/
    public static final DeferredBlock<Block> MAGIC_SPEED_BLOCK = registerBlock("magic_speed_block",
            properties -> new MagicSpeedBlock(
                    properties.strength(4f)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops()),
            Component.translatable("tooltip.testmod.magic_speed_block.tooltip"));






    /*===========================================================================TOOLTIP REGISTRATION====================================================================*/
    private static<T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function, Component... components) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn, components);
        return toReturn;
    }

                                                                                                //Can add multiple components
    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block, Component... components){
        ModItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()){
            @Override
            public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
                for(var component : components){
                    builder.accept(component);
                }
                super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
            }
        });
    }

    /*===========================================================================BLOCK REGISTRATION=====================================================================*/
    private static<T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        // Registers the block and saves it to "toReturn" variable
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        // Saved block variable gets passed here to register the item for the block
        registerBlockItem(name, toReturn);
        // return saves it to an accessible field
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    public static ResourceKey<Block> getRK(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).get();
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
