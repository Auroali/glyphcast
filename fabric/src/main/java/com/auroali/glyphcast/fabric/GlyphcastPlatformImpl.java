package com.auroali.glyphcast.fabric;

import com.auroali.glyphcast.GlyphcastExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class GlyphcastPlatformImpl {
    /**
     * This is our actual method to {@link GlyphcastExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
