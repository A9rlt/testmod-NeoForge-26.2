package net.schblackos.testmod.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.schblackos.testmod.Testmod;

/** The final damage and position needed to display a combat hit on nearby clients. */
public record DamageNumberPayload(double x, double y, double z, float damage, boolean critical, boolean magic)
        implements CustomPacketPayload {
    public static final Type<DamageNumberPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Testmod.MODID, "damage_number"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DamageNumberPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, DamageNumberPayload::x,
            ByteBufCodecs.DOUBLE, DamageNumberPayload::y,
            ByteBufCodecs.DOUBLE, DamageNumberPayload::z,
            ByteBufCodecs.FLOAT, DamageNumberPayload::damage,
            ByteBufCodecs.BOOL, DamageNumberPayload::critical,
            ByteBufCodecs.BOOL, DamageNumberPayload::magic,
            DamageNumberPayload::new);

    @Override
    public Type<DamageNumberPayload> type() {
        return TYPE;
    }
}
