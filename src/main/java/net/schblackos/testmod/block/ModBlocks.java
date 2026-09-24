package net.schblackos.testmod.block;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.block.custom.TestBlock;
import net.schblackos.testmod.item.ModItems;

import java.util.function.Consumer;
import java.util.function.Function;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Testmod.MODID);

    // Generates the new block alongside its properties

    //.noLootTable

    /*NORMAL BLOCKS*/

        //Full Bocks

    public static final DeferredBlock<Block> SILVER_BLOCK = registerBlock("silver_block",
            properties -> new Block(
                    properties
                            .strength(4f)
                            .sound(SoundType.IRON)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> RAW_SILVER_BLOCK = registerBlock("raw_silver_block",
            properties -> new Block(
                    properties
                            .strength(4f)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> SILVER_ORE = registerBlock("silver_ore",
            properties -> new Block(
                    properties
                            .strength(4f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DEEPSLATE_SILVER_ORE = registerBlock("deepslate_silver_ore",
            properties -> new Block(
                    properties
                            .strength(4f)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> NETHER_SILVER_ORE = registerBlock("nether_silver_ore",
            properties -> new Block(
                    properties
                            .strength(4f)
                            .sound(SoundType.NETHERRACK)
                            .requiresCorrectToolForDrops()));



        //Slabs

    public static final DeferredBlock<Block> TEST_SLAB = registerBlock("test_slab",
            properties -> new SlabBlock(
                    properties
                            .strength(2f)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops()));

        //Stairs

    public static final DeferredBlock<Block> TEST_STAIRS = registerBlock("test_stairs",
            properties -> new StairBlock(ModBlocks.SILVER_BLOCK.get().defaultBlockState(),
                    properties
                            .strength(2f)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops()));

        //Fences

    public static final DeferredBlock<Block> TEST_FENCE = registerBlock("test_fence",
            properties -> new FenceBlock(
                    properties
                            .strength(2f)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops()));

        //Fence Gates

    public static final DeferredBlock<Block> TEST_FENCE_GATE = registerBlock("test_fence_gate",
            properties -> new FenceGateBlock(WoodType.OAK,
                    properties
                            .strength(2f)
                            .sound(SoundType.AMETHYST)
                            .requiresCorrectToolForDrops()));

        //Walls
        public static final DeferredBlock<Block> TEST_WALL = registerBlock("test_wall",
                properties -> new WallBlock(
                        properties
                                .strength(2f)
                                .sound(SoundType.AMETHYST)
                                .requiresCorrectToolForDrops()));

        //Doors

        //Trapdoors

        //Buttons

    public static final DeferredBlock<Block> TEST_BUTTON = registerBlock("test_button",
            properties -> new ButtonBlock(BlockSetType.IRON,10,properties
                    .forceSolidOn()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollision()
                    .strength(0.5f)
                    .pushReaction(PushReaction.DESTROY)));



        //Pressure Plates

    public static final DeferredBlock<Block> TEST_PRESSURE_PLATE = registerBlock("test_pressure_plate",
            properties -> new PressurePlateBlock(BlockSetType.IRON, properties
                    .forceSolidOn()
                    .instrument(NoteBlockInstrument.BASS)
                    .noCollision()
                    .strength(0.5f)
                    .pushReaction(PushReaction.DESTROY)));



    /*ADVANCED BLOCKS*/
    public static final DeferredBlock<Block> TEST_BLOCK = registerBlock("test_block",
            properties -> new TestBlock(
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
