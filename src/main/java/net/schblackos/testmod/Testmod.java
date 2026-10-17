package net.schblackos.testmod;

import net.schblackos.testmod.block.ModBlocks;
import net.schblackos.testmod.creativemodetab.ModCreativeModeTabs;
import net.schblackos.testmod.datacomponents.ModDataComponents;
import net.schblackos.testmod.datagen.ModEntities;
import net.schblackos.testmod.item.ModItems;
import net.schblackos.testmod.stats.ModStats;
import net.schblackos.testmod.stats.StatsGameTests;
import net.schblackos.testmod.network.ModNetworking;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Testmod.MODID)
public class Testmod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "testmod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Testmod(IEventBus modEventBus, ModContainer modContainer) {

        // Registers the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModDataComponents.register(modEventBus);

        // Registers the new made mod tab lists
        ModCreativeModeTabs.register(modEventBus);

        // Registers the new made mod items and blocks
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        // Registers the new made entities
        ModEntities.register(modEventBus);
        ModStats.register(modEventBus);
        ModNetworking.register(modEventBus);
        StatsGameTests.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        // Registers the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Registers our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Allows mod items and blocks to be seen in creative tabs
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

        /*ITEMS*/
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.SILVER_INGOT);
            event.accept(ModItems.RAW_SILVER);
        }

        /*ORES*/
        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.SILVER_BLOCK);
        }

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
