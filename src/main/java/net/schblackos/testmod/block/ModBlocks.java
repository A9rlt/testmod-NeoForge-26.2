package net.schblackos.testmod.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.item.ModItems;

import java.util.function.Function;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Testmod.MODID);

    // Generates the new block alongside its properties
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


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
