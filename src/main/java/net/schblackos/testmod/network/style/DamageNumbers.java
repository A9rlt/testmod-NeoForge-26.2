package net.schblackos.testmod.network.style;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.network.payload.DamageNumberPayload;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

/** Server-only hit bookkeeping; clients render the resulting short-lived numbers. */
@EventBusSubscriber(modid = Testmod.MODID)
public final class DamageNumbers {
    private static final Map<UUID, PendingMelee> MELEE = new HashMap<>();
    private static final Map<DamageSource, Map<UUID, Hit>> HITS = new IdentityHashMap<>();

    private DamageNumbers() {
    }

    public static void trackMeleeCritical(CriticalHitEvent event) {
        MELEE.put(event.getEntity().getUUID(), new PendingMelee(
                event.getTarget().getUUID(), event.getEntity().tickCount, event));
    }

    public static boolean consumeMeleeCritical(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)
                || event.getSource().getDirectEntity() != player) {
            return false;
        }
        PendingMelee pending = MELEE.remove(player.getUUID());
        return pending != null && pending.tick() == player.tickCount
                && pending.target().equals(event.getEntity().getUUID())
                && pending.event().isCriticalHit();
    }

    public static void trackHit(LivingIncomingDamageEvent event, boolean critical) {
        removeHit(event.getSource(), event.getEntity().getUUID());
        if (event.isCanceled() || !(event.getSource().getEntity() instanceof ServerPlayer)) {
            return;
        }
        HITS.computeIfAbsent(event.getSource(), ignored -> new HashMap<>()).put(
                event.getEntity().getUUID(), new Hit(critical, event.getEntity().getHealth()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void beforeHealthDamage(LivingDamageEvent.Pre event) {
        Map<UUID, Hit> targets = HITS.get(event.getSource());
        if (targets == null) {
            return;
        }
        UUID target = event.getEntity().getUUID();
        Hit hit = targets.get(target);
        if (hit != null) {
            targets.put(target, new Hit(hit.critical(), event.getEntity().getHealth()));
        }
    }

    @SubscribeEvent
    public static void onDamageReported(LivingDamageEvent.Post event) {
        DamageNumberPayload number = createNumber(event);
        if (number != null) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(event.getEntity(), number);
        }
    }

    static @Nullable DamageNumberPayload createNumber(LivingDamageEvent.Post event) {
        Hit hit = removeHit(event.getSource(), event.getEntity().getUUID());
        if (hit == null) {
            return null;
        }
        // Include absorbed damage; cap overkill to the health the victim actually had.
        float damage = Math.min(Math.max(0.0F, event.getHealthDamage()), hit.healthBefore())
                + Math.max(0.0F, event.getReduction(DamageContainer.Reduction.ABSORPTION));
        if (!Float.isFinite(damage) || damage <= 0.0F) {
            return null;
        }
        var victim = event.getEntity();
        return new DamageNumberPayload(victim.getX(), victim.getY() + victim.getBbHeight() + 0.2D,
                victim.getZ(), damage, hit.critical());
    }

    private static @Nullable Hit removeHit(DamageSource source, UUID target) {
        Map<UUID, Hit> targets = HITS.get(source);
        if (targets == null) {
            return null;
        }
        Hit result = targets.remove(target);
        if (targets.isEmpty()) {
            HITS.remove(source);
        }
        return result;
    }

    @SubscribeEvent
    public static void afterServerTick(ServerTickEvent.Post event) {
        // Canceled attacks and immunity-frame rejections never produce a Post event.
        clear();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        clear();
    }

    private static void clear() {
        MELEE.clear();
        HITS.clear();
    }

    private record PendingMelee(UUID target, int tick, CriticalHitEvent event) {
    }

    private record Hit(boolean critical, float healthBefore) {
    }
}
