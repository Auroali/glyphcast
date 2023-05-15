package com.auroali.glyphcast.fabric;

import com.auroali.glyphcast.Glyphcast;
import net.fabricmc.api.ModInitializer;

public class GlyphcastFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Glyphcast.init();
    }
}
