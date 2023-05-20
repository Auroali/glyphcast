package com.auroali.glyphcast.fabric;

import com.auroali.glyphcast.GlyphcastExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public class GlyphcastExpectPlatformImpl {
    public static MinecraftServer server;

    /**
     * This is our actual method to {@link GlyphcastExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static MinecraftServer getServer() {
        return server;
    }
}
