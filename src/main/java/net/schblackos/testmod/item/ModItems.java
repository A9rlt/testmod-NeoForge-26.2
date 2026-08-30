package net.schblackos.testmod.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Testmod.MODID);

    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerSimpleItem("silver_ingot");
    public static final DeferredItem<Item> RAW_SILVER = ITEMS.registerSimpleItem("raw_silver");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

    }
}
