package net.schblackos.testmod.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.level.Level;

public class TestArrow extends Arrow {

    public TestArrow(EntityType<? extends Arrow> type, Level level) {
        super(type, level);

    }



    @Override
    protected double getDefaultGravity() {
        return -0.5d;
    }

    @Override
    protected float getAirDrag() {
        return 1.0F;
    }
}
