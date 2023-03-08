package com.auroali.glyphcast.mixins.client;

import com.auroali.glyphcast.client.LightTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Light Engine Mixin to allow dynamic lighting
 * @author Auroali
 */
@Mixin(BlockLightEngine.class)
public class BlockLightEngineMixin {

    /**
     * Inject into the lighting engine,
     * this allows us to manually set the light value of blocks.
     */
    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    public void glyphcast$getLightEmission(long pLevelPos, CallbackInfoReturnable<Integer> cir) {
        int i = BlockPos.getX(pLevelPos);
        int j = BlockPos.getY(pLevelPos);
        int k = BlockPos.getZ(pLevelPos);
        BlockPos pos = new BlockPos(i, j, k);
        // We can use the Minecraft class because this is a client-only mixin,
        // it won't be applied to dedicated servers
        // Check to make sure the level is client-side so that the lighting is visual only
        if(Minecraft.getInstance().level != null && Minecraft.getInstance().level.isClientSide && LightTracker.hasAnyLight(pos))
            cir.setReturnValue(LightTracker.getBrightnessAtPosition(pos));
    }
}
