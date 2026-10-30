package net.schblackos.testmod.item.custom.baseitem;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BaseBow extends BowItem {

    private final int arrowCount;
    private final int drawTicks;
    private final float accuracy;
    private final float spreadAngle;
    private final float arrowSpeed;

    public BaseBow(Properties properties, int arrowCount, int drawTicks,
                   float accuracy, float spreadAngle, float arrowSpeed) {

        super(properties);

        if (arrowCount < 1 || drawTicks < 1 ||
                accuracy < 0 || accuracy > 100 ||
                spreadAngle < 0 || arrowSpeed <= 0) {
            throw new IllegalArgumentException(
                    "Invalid bow configuration"
            );
        }

        this.arrowCount = arrowCount;
        this.drawTicks = drawTicks;
        this.accuracy = accuracy;
        this.spreadAngle = spreadAngle;
        this.arrowSpeed = arrowSpeed;
    }


    @Override
    public boolean releaseUsing(ItemStack stack, net.minecraft.world.level.Level level,
                                LivingEntity entity, int remainingTime) {

        int timeHeld =
                getUseDuration(stack, entity) - remainingTime;

        // Scale the charge to vanilla bow power.
        int effectiveCharge = (int) Math.min(
                MAX_DRAW_DURATION,
                Math.max(0L, (long) timeHeld * MAX_DRAW_DURATION / drawTicks)
        );

        int adjustedRemaining = getUseDuration(stack, entity) - effectiveCharge;

        return super.releaseUsing(stack, level, entity, adjustedRemaining);
    }


    @Override
    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon,
                         List<ItemStack> projectiles, float power, float uncertainty, boolean isCrit,
                         LivingEntity targetOverride) {

        if (projectiles.isEmpty()) {
            return;
        }

        List<ItemStack> arrows = new ArrayList<>(projectiles);

        if (shooter instanceof Player player) {

            // Consume an additional arrow for each extra projectile
            while (arrows.size() < arrowCount) {

                ItemStack ammo = player.getProjectile(weapon);

                // Stop if the player has no arrows remaining
                if (ammo.isEmpty()) {
                    break;
                }

                ItemStack usedArrow = useAmmo(
                        weapon,
                        ammo,
                        shooter,
                        false
                );

                if (usedArrow.isEmpty()) {
                    break;
                }

                arrows.add(usedArrow);
            }
        }

        super.shoot(level, shooter, hand, weapon, arrows, power, uncertainty, isCrit, targetOverride);
    }


    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float power,
                                   float uncertainty, float angle, LivingEntity targetOverride
    ) {
        // 100 accuracy = no random deviation.
        // 80 accuracy = approximately vanilla uncertainty.

        float adjustedUncertainty =
                uncertainty * (100f - accuracy) / 20f;

        float offset =
                (index - (arrowCount - 1) / 2.0f)
                        * spreadAngle;

        float finalAngle = angle + offset;

        float finalSpeed = power * arrowSpeed;

        super.shootProjectile(shooter, projectile, index, finalSpeed, adjustedUncertainty, finalAngle, targetOverride);
    }



    protected int getArrowCount() {
        return arrowCount;
    }

    protected int getDrawTicks() {
        return drawTicks;
    }

    protected float getAccuracy() {
        return accuracy;
    }

    protected float getSpreadAngle() {
        return spreadAngle;
    }

    protected float getArrowSpeed() {
        return arrowSpeed;
    }
}