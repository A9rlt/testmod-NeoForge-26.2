package net.schblackos.testmod.item.custom;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.stats.ModCustomStats;

@EventBusSubscriber(modid = Testmod.MODID)
public class TestStaff extends Item {
    private static final int BEAM_DURATION = 5;
    private static final double BEAM_RANGE = 20.0D;
    private static final Map<UUID, BeamData> ACTIVE_BEAMS = new HashMap<>();

    public TestStaff(Properties properties) {
        super(properties);
    }

    /** The server determines the target, damage and fire-rate cooldown for every shot. */
    public static void shoot(ServerPlayer player) {
        ItemStack staff = player.getMainHandItem();
        if (!(staff.getItem() instanceof TestStaff) || !player.isAlive() || player.isSpectator()
                || player.getCooldowns().isOnCooldown(staff)) {
            return;
        }
        double fireRate = player.getAttributeValue(ModCustomStats.FIRE_RATE);
        if (!Double.isFinite(fireRate) || fireRate <= 0.0D) {
            return;
        }
        player.getCooldowns().addCooldown(staff, Math.max(1, (int) Math.ceil(20.0D / fireRate)));

        ServerLevel level = player.level();
        Vec3 origin = player.getEyePosition();
        Vec3 ray = player.getLookAngle().scale(BEAM_RANGE);
        HitResult hit = level.clipIncludingBorder(new ClipContext(origin, origin.add(ray),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        var entityHit = ProjectileUtil.getEntityHitResult(level, player, origin, hit.getLocation(),
                player.getBoundingBox().expandTowards(ray).inflate(1.0D),
                entity -> entity instanceof LivingEntity && entity.isAlive() && entity.isPickable()
                        && !entity.isSpectator(), 0.0F);
        if (entityHit != null) {
            hit = entityHit;
        }

        Vec3 right = new Vec3(-Math.cos(Math.toRadians(player.getYRot())), 0.0D,
                -Math.sin(Math.toRadians(player.getYRot())));
        Vec3 start = player.getEyePosition().add(right.scale(0.4D)).add(0.0D, -0.3D, 0.0D);
        ACTIVE_BEAMS.put(player.getUUID(), new BeamData(level, start, hit.getLocation(), BEAM_DURATION));

        if (hit instanceof EntityHitResult targetHit && targetHit.getEntity() instanceof LivingEntity target) {
            float damage = (float) player.getAttributeValue(ModCustomStats.MAGIC_DAMAGE);
            if (damage > 0.0F) {
                target.hurtServer(level, player.damageSources().indirectMagic(player, player), damage);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        UUID playerId = player.getUUID();
        BeamData beam = ACTIVE_BEAMS.get(playerId);
        if (beam == null) {
            return;
        }
        if (!player.isAlive() || player.level() != beam.level()
                || !(player.getMainHandItem().getItem() instanceof TestStaff)) {
            ACTIVE_BEAMS.remove(playerId);
            return;
        }

        spawnBeam(beam.level(), beam.start(), beam.end());
        if (beam.ticksLeft() <= 1) {
            ACTIVE_BEAMS.remove(playerId);
        } else {
            ACTIVE_BEAMS.put(playerId,
                    new BeamData(beam.level(), beam.start(), beam.end(), beam.ticksLeft() - 1));
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ACTIVE_BEAMS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        ACTIVE_BEAMS.clear();
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return false;
    }

    private static void spawnBeam(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        Vec3 direction = delta.normalize();
        for (double distance = 0.0D; distance <= length; distance += 0.5D) {
            Vec3 pos = start.add(direction.scale(distance));
            level.sendParticles(ParticleTypes.BUBBLE_POP, pos.x, pos.y, pos.z, 1,
                    0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private record BeamData(ServerLevel level, Vec3 start, Vec3 end, int ticksLeft) {
    }
}
