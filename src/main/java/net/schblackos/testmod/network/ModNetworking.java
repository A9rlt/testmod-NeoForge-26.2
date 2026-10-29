package net.schblackos.testmod.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.schblackos.testmod.network.payload.DamageNumberPayload;
import net.schblackos.testmod.network.payload.StaffShootPayload;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworking::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // The handler is registered by the client so dedicated servers never load rendering classes.
        var registrar = event.registrar("2");
        registrar.playToClient(DamageNumberPayload.TYPE, DamageNumberPayload.CODEC);
        registrar.playToServer(StaffShootPayload.TYPE, StaffShootPayload.STREAM_CODEC, StaffShootPayload::handle);
    }
}
