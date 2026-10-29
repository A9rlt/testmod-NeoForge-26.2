package net.schblackos.testmod.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.schblackos.testmod.network.payload.StaffShootPayload;

public class StaffNetwork {

    public static void register(RegisterPayloadHandlersEvent event) {

        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                StaffShootPayload.TYPE,
                StaffShootPayload.STREAM_CODEC,
                StaffShootPayload::handle
        );
    }
}