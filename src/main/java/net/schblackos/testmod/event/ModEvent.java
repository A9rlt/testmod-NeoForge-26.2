package net.schblackos.testmod.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.schblackos.testmod.Testmod;

@EventBusSubscriber(modid = Testmod.MODID)
public class ModEvent {

    //@SubscribeEvent above every event
    //Can make any custom event e.g. hitting an enitity with a specific block or item gives an effect


}
