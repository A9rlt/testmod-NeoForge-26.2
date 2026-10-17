package net.schblackos.testmod.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.schblackos.testmod.datagen.ModEntities;
import net.schblackos.testmod.item.ModItems;
import org.jspecify.annotations.Nullable;

public class TestArrowEntity extends AbstractArrow {


    public static final double GRAVITY = 0.05d;
    public static final float AIR_DRAG = 0.99f;
    public static final double ARROW_BASE_DAMAGE = 2.0f;

    public TestArrowEntity(EntityType<? extends TestArrowEntity> type, Level level) {
        super(type, level);
        this.setBaseDamage(ARROW_BASE_DAMAGE);
    }

    public TestArrowEntity(Level level, LivingEntity owner, ItemStack ammo,
                           @Nullable ItemStack firedFromWeapon) {
        super(ModEntities.TEST_ARROW.get(), owner, level,
                ammo.copyWithCount(1), firedFromWeapon);
        this.setBaseDamage(ARROW_BASE_DAMAGE);
    }



    @Override
    protected double getDefaultGravity() {
        return GRAVITY;
    }

    @Override
    protected float getAirDrag() {
        return AIR_DRAG;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.TEST_ARROW_ITEM.get());
    }
}
