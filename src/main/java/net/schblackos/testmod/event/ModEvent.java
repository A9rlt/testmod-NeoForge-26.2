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

@EventBusSubscriber(modid = Testmod.MODID)
public class ModEvent {

    @EventBusSubscriber(modid = Testmod.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onLeftClick(InputEvent.InteractionKeyMappingTriggered event) {
            if (!event.isAttack()) {
                return;
            }

            var player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            if (player.getMainHandItem().getItem() instanceof TestStaff) {
                ClientPacketDistributor.sendToServer(new StaffShootPayload());
            }
        }
    }


}
