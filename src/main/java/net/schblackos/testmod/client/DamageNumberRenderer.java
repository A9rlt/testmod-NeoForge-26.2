package net.schblackos.testmod.client;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.network.DamageNumberPayload;
import net.schblackos.testmod.stats.DamageNumberStyle;
import org.jspecify.annotations.Nullable;

/** Temporary client visuals; damage numbers never create or save world entities. */
@EventBusSubscriber(modid = Testmod.MODID, value = Dist.CLIENT)
public final class DamageNumberRenderer {
    private static final int LIFETIME_TICKS = 25;
    private static final int FADE_TICKS = 7;
    private static final int MAX_NUMBERS = 256;
    private static final double RISE_DISTANCE = 0.7D;
    private static final RandomSource VISUAL_RANDOM = RandomSource.create();
    private static final ContextKey<List<DamageRenderState>> DAMAGE_NUMBERS = new ContextKey<>(
            Identifier.fromNamespaceAndPath(Testmod.MODID, "damage_numbers"));
    private static final ArrayDeque<DamageNumber> ACTIVE_NUMBERS = new ArrayDeque<>();
    private static @Nullable ClientLevel activeLevel;

    private DamageNumberRenderer() {
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(DamageNumberPayload.TYPE, (payload, context) -> add(payload));
    }

    private static void add(DamageNumberPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        updateLevel(level);
        if (level == null || !Float.isFinite(payload.damage()) || payload.damage() <= 0.0F
                || !Double.isFinite(payload.x()) || !Double.isFinite(payload.y()) || !Double.isFinite(payload.z())) {
            return;
        }

        while (ACTIVE_NUMBERS.size() >= MAX_NUMBERS) {
            ACTIVE_NUMBERS.removeFirst();
        }

        FormattedCharSequence text = Component.literal(DamageNumberStyle.format(payload.damage())).getVisualOrderText();
        ACTIVE_NUMBERS.addLast(new DamageNumber(
                payload.x() + (VISUAL_RANDOM.nextDouble() - 0.5D) * 0.4D,
                payload.y(),
                payload.z() + (VISUAL_RANDOM.nextDouble() - 0.5D) * 0.4D,
                text,
                minecraft.font.width(text),
                DamageNumberStyle.color(payload.critical())));
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        updateLevel(minecraft.level);
        if (activeLevel == null || minecraft.isPaused()) {
            return;
        }
        ACTIVE_NUMBERS.removeIf(number -> ++number.age >= LIFETIME_TICKS);
    }

    @SubscribeEvent
    public static void loggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ACTIVE_NUMBERS.clear();
        activeLevel = null;
    }

    @SubscribeEvent
    public static void extract(ExtractLevelRenderStateEvent event) {
        updateLevel(event.getLevel());
        float partialTick = event.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        List<DamageRenderState> states = new ArrayList<>(ACTIVE_NUMBERS.size());
        for (DamageNumber number : ACTIVE_NUMBERS) {
            float age = Math.min(LIFETIME_TICKS, number.age + partialTick);
            int alpha = Math.round(255.0F * Math.min(1.0F, (LIFETIME_TICKS - age) / FADE_TICKS));
            if (alpha < 4) {
                continue;
            }
            int color = (number.color & 0x00FFFFFF) | (alpha << 24);
            states.add(new DamageRenderState(number.x, number.y + RISE_DISTANCE * age / LIFETIME_TICKS,
                    number.z, number.text, number.width, color));
        }
        // The submission stage only reads this immutable snapshot, never mutable game state.
        event.getRenderState().setRenderData(DAMAGE_NUMBERS, List.copyOf(states));
    }

    @SubscribeEvent
    public static void submit(SubmitCustomGeometryEvent event) {
        List<DamageRenderState> states = event.getLevelRenderState().getRenderData(DAMAGE_NUMBERS);
        if (states == null || states.isEmpty()) {
            return;
        }
        var camera = event.getLevelRenderState().cameraRenderState;
        var poses = event.getPoseStack();
        for (DamageRenderState state : states) {
            poses.pushPose();
            poses.translate(state.x - camera.pos.x, state.y - camera.pos.y, state.z - camera.pos.z);
            poses.mulPose(camera.orientation);
            poses.scale(0.025F, -0.025F, 0.025F);
            event.getSubmitNodeCollector().submitText(poses, -state.width / 2.0F, 0.0F, state.text,
                    true, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, state.color, 0, 0);
            poses.popPose();
        }
    }

    private static void updateLevel(@Nullable ClientLevel level) {
        if (activeLevel != level) {
            ACTIVE_NUMBERS.clear();
            activeLevel = level;
        }
    }

    private static final class DamageNumber {
        private final double x;
        private final double y;
        private final double z;
        private final FormattedCharSequence text;
        private final int width;
        private final int color;
        private int age;

        private DamageNumber(double x, double y, double z, FormattedCharSequence text, int width, int color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.text = text;
            this.width = width;
            this.color = color;
        }
    }

    private record DamageRenderState(double x, double y, double z, FormattedCharSequence text, int width, int color) {
    }
}
