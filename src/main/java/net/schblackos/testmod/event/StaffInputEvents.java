package net.schblackos.testmod.event;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.schblackos.testmod.Testmod;
import net.schblackos.testmod.item.custom.TestStaff;
import net.schblackos.testmod.network.payload.StaffShootPayload;

/** Staff attacks use the server's beam target instead of a second vanilla melee attack. */
@EventBusSubscriber(modid = Testmod.MODID, value = Dist.CLIENT)
public final class StaffInputEvents {
    private StaffInputEvents() {
    }

    @SubscribeEvent
    public static void onLeftClick(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) {
            return;
        }
        var player = Minecraft.getInstance().player;
        if (player == null || !(player.getMainHandItem().getItem() instanceof TestStaff)) {
            return;
        }
        event.setCanceled(true);
        if (!player.getCooldowns().isOnCooldown(player.getMainHandItem())) {
            ClientPacketDistributor.sendToServer(new StaffShootPayload());
        }
    }
}
