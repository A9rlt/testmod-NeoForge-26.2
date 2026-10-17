package net.schblackos.testmod.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworking::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // The handler is registered by the client so dedicated servers never load rendering classes.
        event.registrar("1").playToClient(DamageNumberPayload.TYPE, DamageNumberPayload.CODEC);
    }
}
