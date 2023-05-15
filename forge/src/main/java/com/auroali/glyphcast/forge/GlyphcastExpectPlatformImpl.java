package com.auroali.glyphcast.forge;

import com.auroali.glyphcast.GlyphcastExpectPlatform;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.nio.file.Path;

public class GlyphcastExpectPlatformImpl {
    /**
     * This is our actual method to {@link GlyphcastExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
}
