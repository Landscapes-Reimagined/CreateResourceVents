package com.landscapesreimagined.createresourcevents;

import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class Client {

    public static void onClientInit(final FMLClientSetupEvent event){
        PartialModels.init();
        SpriteShifts.init();
    }

    public static void onCtorClient(IEventBus modEventBus) {
        modEventBus.addListener(Client::onClientInit);

    }
}
