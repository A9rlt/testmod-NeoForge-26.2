package net.schblackos.testmod.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.food.ModFoods;
import net.schblackos.testmod.item.custom.TestArrowItem;
import net.schblackos.testmod.item.custom.TestDataItem;
import net.schblackos.testmod.item.custom.TestItem;

import java.util.function.Consumer;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Testmod.MODID);

    /*NORMAL ITEMS*/

        //Food Items
    public static final DeferredItem<Item> TEST_FOOD = ITEMS.registerItem("test_food",
            properties -> new Item(properties.food(ModFoods.TEST_FOOD, ModFoods.TEST_FOOD_CONSUMABLE)){

                // This is so clapped lol
                @Override
                public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display,
                                            Consumer<Component> builder, TooltipFlag tooltipFlag) {
                    builder.accept(Component.translatable("tooltip.testmod.onion.tootltip"));

                    super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
                }

            });


        //Fuels
    public static final DeferredItem<Item> TEST_FUEL = ITEMS.registerSimpleItem("test_fuel");

        //Swords
    public static final DeferredItem<Item> TEST_SWORD = ITEMS.registerItem("test_sword",
            properties -> new Item(properties.sword(
                    ModToolTiers.TEST,
                    3f,
                    -2.4f)));
        //Pickaxes
    public static final DeferredItem<Item> TEST_PICKAXE = ITEMS.registerItem("test_pickaxe",
            properties -> new Item(properties.pickaxe(
                    ModToolTiers.TEST,
                    1f,
                    -2.8f)));

        //Shovels
    public static final DeferredItem<Item> TEST_SHOVEL = ITEMS.registerItem("test_shovel",
            properties -> new ShovelItem(
                    ModToolTiers.TEST,
                    1.5f,
                    -3.0f,
                    properties));

        //Axes
    public static final DeferredItem<Item> TEST_AXE = ITEMS.registerItem("test_axe",
            properties -> new AxeItem(
                    ModToolTiers.TEST,
                    6f,
                    -3.2f,
                    properties));

        //Hoes (HAHAHAHHAA)
    public static final DeferredItem<Item> TEST_HOE = ITEMS.registerItem("test_hoe",
            properties -> new HoeItem(
                    ModToolTiers.TEST,
                    0f,
                    -3.4f,
                    properties));

        //Spears
    public static final DeferredItem<Item> TEST_SPEAR = ITEMS.registerItem("test_spear",
            properties -> new Item(properties.spear(
                    ModToolTiers.TEST,
                    0.95f,
                    0.7f,
                    0.7f,
                    3.5f,
                    13,
                    8.5f,
                    5.1f,
                    13.37f,
                    4.67f)));

        //Bows
    public static final DeferredItem<Item> TEST_BOW = ITEMS.registerItem("test_bow",
            properties -> new BowItem(properties.durability(500)));

        //Helmets
    public static final DeferredItem<Item> TEST_HELMET = ITEMS.registerItem("test_helmet",
            properties -> new Item(properties.humanoidArmor(
                    ModArmorMaterial.TEST_ARMOR_MATERIAL,
                    ArmorType.HELMET)));
        //Chestplates
    public static final DeferredItem<Item> TEST_CHESTPLATE = ITEMS.registerItem("test_chestplate",
            properties -> new Item(properties.humanoidArmor(
                    ModArmorMaterial.TEST_ARMOR_MATERIAL,
                    ArmorType.CHESTPLATE)));
        //Leggings
    public static final DeferredItem<Item> TEST_LEGGINGS = ITEMS.registerItem("test_leggings",
            properties -> new Item(properties.humanoidArmor(
                    ModArmorMaterial.TEST_ARMOR_MATERIAL,
                    ArmorType.LEGGINGS)));
        //Boots
    public static final DeferredItem<Item> TEST_BOOTS = ITEMS.registerItem("test_boots",
            properties -> new Item(properties.humanoidArmor(
                    ModArmorMaterial.TEST_ARMOR_MATERIAL,
                    ArmorType.BOOTS)));


        //Misc
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerSimpleItem("silver_ingot");
    public static final DeferredItem<Item> RAW_SILVER = ITEMS.registerSimpleItem("raw_silver");


    /*ADVANCED ITEMS*/

    public static final DeferredItem<Item> TEST_ITEM =ITEMS.registerItem("test_item",
            properties -> new TestItem(properties
                    .durability(64)
                    .useCooldown(0.5f)
                    .stacksTo(1)));


    public static final DeferredItem<Item> TEST_DATA_ITEM =ITEMS.registerItem("test_data_item",
            properties -> new TestDataItem(properties.stacksTo(1)));

    public static final DeferredItem<TestArrowItem> TEST_ARROW_ITEM = ITEMS.registerItem("test_arrow_item",
            TestArrowItem::new);



    public static ResourceKey<Item> getRK(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).get();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

    }
}
