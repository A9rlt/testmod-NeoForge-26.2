package net.schblackos.testmod.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class ModFoods {

    public static final FoodProperties TEST_FOOD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3f)
            .build();



    public static final Consumable TEST_FOOD_CONSUMABLE = Consumables.defaultFood()
            .consumeSeconds(2.5f)
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(
                    MobEffects.NAUSEA,
                    400),
                    0.10f))
            .build();
}
