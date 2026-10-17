package net.schblackos.testmod.item.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.schblackos.testmod.entity.custom.TestArrowEntity;
import org.jspecify.annotations.Nullable;

public class TestArrowItem extends ArrowItem {

    public TestArrowItem(Properties properties) {
        super(properties);
    }


    @Override
    public AbstractArrow createArrow(Level level, ItemStack itemStack,
                                     LivingEntity owner, @Nullable ItemStack firedFromWeapon) {

        return new TestArrowEntity(level, owner, itemStack, firedFromWeapon);
    }
}

