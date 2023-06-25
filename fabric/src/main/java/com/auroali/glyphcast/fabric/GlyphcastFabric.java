package com.auroali.glyphcast.fabric;

import com.auroali.glyphcast.Glyphcast;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class GlyphcastFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Glyphcast.init();
        Glyphcast.post();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> GlyphcastExpectPlatformImpl.server = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> GlyphcastExpectPlatformImpl.server = null);
    }
}
