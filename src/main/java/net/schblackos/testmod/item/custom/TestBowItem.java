package net.schblackos.testmod.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.schblackos.testmod.datagen.ModEntities;
import net.schblackos.testmod.entity.custom.TestArrow;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class TestBowItem extends ProjectileWeaponItem {

    public static final int MAX_DRAW_DURATION = 10;
    public static final int DEFAULT_RANGE = 20;

    public TestBowItem(Item.Properties properties) {
        super(properties);
    }

    private static float getCustomPowerForTime(int timeHeld){

        float pow = timeHeld /10f ;

        pow = (pow * pow + pow * 2f) / 3f ;

        if (pow > 1f){
            pow = 1f;
        }
        return pow;
    }


    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime) {

        if (!(entity instanceof Player player)){

            return false;
        }

        ItemStack projectile = player.getProjectile(itemStack);

        if (projectile.isEmpty()) {
            return false;
        }

        int timeHeld = this.getUseDuration(itemStack, entity) - remainingTime;

        float power = getCustomPowerForTime(timeHeld);

        if (power < 0.1f){

            return false;
        }

        List<ItemStack> firedProjectiles = draw(itemStack, projectile, player);

        if (level instanceof ServerLevel serverLevel && !firedProjectiles.isEmpty()){

            this.shoot(
                    serverLevel,
                    player,
                    player.getUsedItemHand(),
                    itemStack,
                    firedProjectiles,
                    power * 5f,
                    1f,
                    power == 1f,
                    null
            );
        }

        return true;
    }




    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity user) {
        return 72000;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return DEFAULT_RANGE;
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon,
                                          ItemStack projectile, boolean isCrit) {


        TestArrow arrow = new TestArrow(ModEntities.TEST_ARROW.get(), level);

        arrow.setOwner(shooter);

        if (isCrit) {arrow.setCritArrow(true);}

        return arrow;
    }

    @Override
    protected void shootProjectile(
            LivingEntity shooter,
            Projectile projectile,
            int index,
            float power,
            float uncertainty,
            float angle,
            @Nullable LivingEntity targetOverride) {

        projectile.shootFromRotation(
                shooter,
                shooter.getXRot(),
                shooter.getYRot() + angle,
                0.0F,
                power,
                uncertainty
        );
    }
}

