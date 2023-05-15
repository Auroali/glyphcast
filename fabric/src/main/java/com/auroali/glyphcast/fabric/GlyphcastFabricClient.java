package com.auroali.glyphcast.fabric;

import com.auroali.glyphcast.Glyphcast;
import net.fabricmc.api.ClientModInitializer;

public class GlyphcastFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Glyphcast.initClient();
    }
}
