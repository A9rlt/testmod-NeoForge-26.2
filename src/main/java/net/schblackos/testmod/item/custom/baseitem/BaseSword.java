package net.schblackos.testmod.item.custom.baseitem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;

import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.stats.ModCustomStats;

@EventBusSubscriber(modid = Testmod.MODID)
public class BaseSword extends Item {

    private final float swingRange;
    private final float attackReach;
    private final int advancedCooldown;
    private final boolean hasAdvancedAttack;

    public BaseSword(Properties properties, float swingRange,
                     float attackReach, int advancedCooldown,
                     boolean hasAdvancedAttack) {

        super(properties.component(
                DataComponents.ATTACK_RANGE,
                new AttackRange(
                        0f,
                        attackReach,
                        0f,
                        attackReach,
                        0.3f,
                        1f
                )
        ));

        this.swingRange = swingRange;
        this.attackReach = attackReach;
        this.advancedCooldown = advancedCooldown;
        this.hasAdvancedAttack = hasAdvancedAttack;
    }


    @SubscribeEvent
    public static void onSweepAttack(SweepAttackEvent event) {

        Player player = event.getEntity();

        if (!(player.getMainHandItem().getItem()
                instanceof BaseSword sword)) {
            return;
        }

        if (!event.isVanillaSweep() || !event.isSweeping()) {
            return;
        }

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        event.setSweeping(false);
        event.setCanceled(true);

        Vec3 centre = event.getTarget().position();

        var nearby = level.getEntitiesOfClass(
                LivingEntity.class,
                event.getTarget().getBoundingBox().inflate(
                        sword.swingRange,
                        0.25,
                        sword.swingRange
                ),
                target -> target.isAlive()
                        && target != player
                        && target != event.getTarget()
                        && player.canAttack(target)
                        && player.hasLineOfSight(target)
        );

        for (LivingEntity target : nearby) {

            if (target.position().distanceToSqr(centre) > sword.swingRange * sword.swingRange) {
                continue;
            }


            target.hurtServer(level, player.damageSources().playerAttack(player),
                    1.0f
            );
        }
    }



    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        if (!hasAdvancedAttack) {
            return InteractionResult.PASS;
        }

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }

        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {

            performAdvancedAttack(serverLevel, serverPlayer);

            serverPlayer.getCooldowns().addCooldown(
                    stack,
                    advancedCooldown
            );
        }

        return InteractionResult.SUCCESS;
    }


    protected void performAdvancedAttack(ServerLevel level, ServerPlayer player) {


        double damage = player.getAttributeValue(
                ModCustomStats.DAMAGE
        );

        var nearby = level.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(
                        swingRange,
                        1.0,
                        swingRange
                ),
                target -> target != player
                        && target.isAlive()
                        && player.canAttack(target)
                        && player.hasLineOfSight(target)
        );

        for (LivingEntity target : nearby) {

            if (target.position().distanceToSqr(
                    player.position()
            ) > swingRange * swingRange) {
                continue;
            }

            target.hurtServer(level, player.damageSources().playerAttack(player), (float) damage
            );
        }
    }


    protected float getSwingRange() {
        return swingRange;
    }

    protected float getAttackReach() {
        return attackReach;
    }
}