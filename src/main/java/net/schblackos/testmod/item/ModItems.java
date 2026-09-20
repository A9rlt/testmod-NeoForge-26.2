package net.schblackos.testmod.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.food.ModFoods;
import net.schblackos.testmod.item.custom.MetalDetectorItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Testmod.MODID);

    //Normal Items
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerSimpleItem("silver_ingot");
    public static final DeferredItem<Item> RAW_SILVER = ITEMS.registerSimpleItem("raw_silver");


    //Advanced Items
    public static final DeferredItem<Item> METAL_DETECTOR =ITEMS.registerItem("metal_detector",
            properties -> new MetalDetectorItem(properties
                    .durability(64)
                    .useCooldown(0.5f)
                    .stacksTo(1)));


    //Food Items
    public static final DeferredItem<Item> ONION = ITEMS.registerItem("onion",
            properties -> new Item(properties.food(ModFoods.ONION, ModFoods.ONION_CONSUMABLE)));


    //Fuels
    public static final DeferredItem<Item> BROWN_COAL = ITEMS.registerSimpleItem("brown_coal");







    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

    }
}
