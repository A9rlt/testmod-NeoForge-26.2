package net.schblackos.testmod.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TestCustomBowItem extends BowItem {

    public static final int DEFAULT_RANGE = 20;
    public static final int PROJECTILES_PER_SHOT = 3;
    public static final float ARROW_SPREAD_DEGREES = 10.0F;

    public TestCustomBowItem(Item.Properties properties) {
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


        if (level instanceof ServerLevel serverLevel) {
            List<ItemStack> firedProjectiles = new ArrayList<>(PROJECTILES_PER_SHOT);
            for (int i = 0; i < PROJECTILES_PER_SHOT; i++) {

                ItemStack ammo = player.getProjectile(itemStack);
                if (ammo.isEmpty()) {
                    break;
                }


                ItemStack consumedArrow = useAmmo(itemStack, ammo, player, false);
                if (consumedArrow.isEmpty()) {
                    break;
                }
                firedProjectiles.add(consumedArrow);
            }

            if (firedProjectiles.isEmpty()) {
                return false;
            }

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

        return ARROW_ONLY.or(stack -> stack.getItem() instanceof ArrowItem);
    }

    @Override
    public int getDefaultProjectileRange() {
        return DEFAULT_RANGE;
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



        float spreadAngle = switch (index) {
            case 1 -> -ARROW_SPREAD_DEGREES;
            case 2 -> ARROW_SPREAD_DEGREES;
            default -> 0.0F;
        };

        projectile.shootFromRotation(
                shooter,
                shooter.getXRot(),
                shooter.getYRot() + angle + spreadAngle,
                0.0F,
                power,
                uncertainty
        );
    }
}

