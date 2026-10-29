package net.schblackos.testmod.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.item.custom.TestStaff;

public record StaffShootPayload() implements CustomPacketPayload {
    public static final Type<StaffShootPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Testmod.MODID, "staff_shoot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StaffShootPayload> STREAM_CODEC =
            StreamCodec.unit(new StaffShootPayload());

    @Override
    public Type<StaffShootPayload> type() {
        return TYPE;
    }

    public static void handle(StaffShootPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            TestStaff.shoot(player);
        }
    }
}
