package com.auroali.glyphcast.mixins.fabric;

import com.auroali.glyphcast.fabric.GlyphcastExpectPlatformImpl;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// I couldn't find a fabric hook for this
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "runServer", at = @At("HEAD"))
    public void glyphcastfabric$runServer(CallbackInfo ci) {
        GlyphcastExpectPlatformImpl.server = (MinecraftServer) (Object) this;
    }

    @Inject(method = "stopServer", at = @At("TAIL"))
    public void glyphcastfabric$stopServer(CallbackInfo ci) {
        if (GlyphcastExpectPlatformImpl.server == (Object) this)
            GlyphcastExpectPlatformImpl.server = null;
    }
}
