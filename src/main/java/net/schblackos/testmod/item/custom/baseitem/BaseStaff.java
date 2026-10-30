package net.schblackos.testmod.item.custom.baseitem;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.stats.ModCustomStats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Testmod.MODID)
public class BaseStaff extends Item {

    private final ParticleOptions particleType;
    private final double beamRange;
    private final int beamDuration;
    private final int advancedCooldown;
    private final boolean hasAdvancedAttack;

    private static final Map<UUID, Long> lastFireTime =
            new HashMap<>();

    private static final Map<UUID, BeamData> activeBeams =
            new HashMap<>();

    private record BeamData(
            Vec3 start,
            Vec3 direction,
            ParticleOptions particle,
            double range,
            int ticksLeft
    ) {}

    public BaseStaff(Properties properties, ParticleOptions particleType,
                     double beamRange, int beamDuration, int advancedCooldown, boolean hasAdvancedAttack
    ) {
        super(properties);

        this.particleType = particleType;
        this.beamRange = beamRange;
        this.beamDuration = beamDuration;
        this.advancedCooldown = advancedCooldown;
        this.hasAdvancedAttack = hasAdvancedAttack;
    }


    public final void basicAttack(ServerLevel level, ServerPlayer player) {

        UUID playerId = player.getUUID();
        long currentTime = level.getGameTime();

        double fireRate = player.getAttributeValue(ModCustomStats.FIRE_RATE);

        int cooldownTicks = Math.max(
                1,
                (int) Math.ceil(20.0 / Math.max(0.1, fireRate))
        );

        Long previousFire = lastFireTime.get(playerId);

        if (previousFire != null && currentTime - previousFire < cooldownTicks) {
            return;
        }

        lastFireTime.put(playerId, currentTime);

        Vec3 direction = player.getLookAngle();


        double yaw = Math.toRadians(player.getYRot());

        Vec3 right = new Vec3(
                Math.cos(yaw),
                0,
                Math.sin(yaw)
        );

        Vec3 start = player.getEyePosition()
                .add(right.scale(0.4))
                .add(0, -0.3, 0);

        activeBeams.put(playerId, new BeamData(start, direction, particleType, beamRange, beamDuration));

        hitBasicTarget(level, player);
    }


    protected void hitBasicTarget(ServerLevel level, ServerPlayer player) {
        HitResult result = ProjectileUtil.getHitResultOnViewVector(player,
                        entity -> entity instanceof LivingEntity
                                && entity.isAlive()
                                && entity != player,
                        beamRange
                );

        if (!(result instanceof EntityHitResult entityHit)) {
            return;
        }

        if (!(entityHit.getEntity() instanceof LivingEntity target)) {
            return;
        }

        double physicalDamage = player.getAttributeValue(
                ModCustomStats.DAMAGE
        );

        double magicDamage = player.getAttributeValue(
                ModCustomStats.MAGIC_DAMAGE
        );

        float totalDamage = (float) Math.max(
                0,
                physicalDamage + magicDamage
        );

        if (totalDamage > 0) {target.hurtServer(level,
                    player.damageSources().playerAttack(player),
                    totalDamage
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

            advancedAttack(serverLevel, serverPlayer);

            serverPlayer.getCooldowns().addCooldown(stack, advancedCooldown);
        }

        return InteractionResult.SUCCESS;

    }

    protected void advancedAttack(ServerLevel level, ServerPlayer player) {

        Vec3 direction = player.getLookAngle();

        SmallFireball fireball = new SmallFireball(level, player, direction);

        Vec3 spawn = player.getEyePosition().add(direction.scale(0.6));

        fireball.setPos(
                spawn.x,
                spawn.y,
                spawn.z
        );

        level.addFreshEntity(fireball);

    }


    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        UUID playerId = player.getUUID();
        BeamData beam = activeBeams.get(playerId);

        if (beam == null) {
            return;
        }

        if (player.level() instanceof ServerLevel level) {
            spawnBeam(level, beam);
        }

        if (beam.ticksLeft() <= 1) {
            activeBeams.remove(playerId);
        } else {
            activeBeams.put(
                    playerId,
                    new BeamData(
                            beam.start(),
                            beam.direction(),
                            beam.particle(),
                            beam.range(),
                            beam.ticksLeft() - 1
                    )
            );
        }
    }

    private static void spawnBeam(ServerLevel level, BeamData beam) {

        for (double distance = 0;
             distance < beam.range();
             distance += 0.5) {

            Vec3 pos = beam.start().add(
                    beam.direction().scale(distance)
            );

            level.sendParticles(
                    beam.particle(),
                    pos.x,
                    pos.y,
                    pos.z,
                    1,
                    0,
                    0,
                    0,
                    0
            );
        }
    }
}