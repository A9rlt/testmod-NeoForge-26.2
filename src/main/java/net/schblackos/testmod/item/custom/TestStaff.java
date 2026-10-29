package net.schblackos.testmod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.schblackos.testmod.stats.ModCustomStats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "testmod")
public class TestStaff extends Item {

    private static final int BEAM_DURATION = 5;
    private static final double BEAM_RANGE = 20;
    private static final Map<UUID, Long> lastFireTime = new HashMap<>();
    private static final Map<UUID, BeamData> activeBeams = new HashMap<>();

    private record BeamData(
            Vec3 start,
            Vec3 direction,
            int ticksLeft
    ){

    }

    public TestStaff(Properties properties) {
        super(properties);
    }


    public static void shootParticles(ServerLevel level, Player player) {

        UUID playerId = player.getUUID();
        long currentTime = level.getGameTime();

        // Retrieve player's current fire rate
        double fireRate = player.getAttributeValue(
                ModCustomStats.FIRE_RATE
        );

        // Convert shots per second into ticks between shots
        int cooldownTicks = Math.max(
                1,
                (int) Math.ceil(25 / fireRate)
        );

        Long previousFire = lastFireTime.get(playerId);

        if (previousFire != null &&
                currentTime - previousFire < cooldownTicks) {
            return;
        }

        lastFireTime.put(playerId, currentTime);

        Vec3 direction = player.getLookAngle();

        Vec3 right = new Vec3(
                -Math.cos(Math.toRadians(player.getYRot())),
                0,
                -Math.sin(Math.toRadians(player.getYRot()))
        );

        Vec3 start = player.getEyePosition()
                .add(right.scale(0.4))
                .add(0, -0.3, 0);

        activeBeams.put(
                playerId,
                new BeamData(start, direction, BEAM_DURATION)
        );
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

        if (!(player.getMainHandItem().getItem() instanceof TestStaff)) {
            activeBeams.remove(playerId);
            return;
        }

        if (player.level() instanceof ServerLevel level) {
            spawnBeam(level, beam.start(), beam.direction());
        }

        if (beam.ticksLeft() <= 1) {
            activeBeams.remove(playerId);
        } else {
            activeBeams.put(
                    playerId,
                    new BeamData(
                            beam.start(),
                            beam.direction(),
                            beam.ticksLeft() - 1
                    )
            );
        }
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity
    ) {
        return false;
    }

    private static void spawnBeam(ServerLevel level, Vec3 start, Vec3 direction) {

        for (double distance = 0;
             distance < BEAM_RANGE;
             distance += 0.5) {

            Vec3 pos = start.add(direction.scale(distance));

            level.sendParticles(
                    ParticleTypes.BUBBLE_POP,
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