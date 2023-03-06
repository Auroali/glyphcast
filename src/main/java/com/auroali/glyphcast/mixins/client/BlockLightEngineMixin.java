package com.auroali.glyphcast.mixins.client;

import com.auroali.glyphcast.client.LightTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLightEngine.class)
public class BlockLightEngineMixin {
    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    public void getLightEmission(long pLevelPos, CallbackInfoReturnable<Integer> cir) {
        int i = BlockPos.getX(pLevelPos);
        int j = BlockPos.getY(pLevelPos);
        int k = BlockPos.getZ(pLevelPos);
        BlockPos pos = new BlockPos(i, j, k);
        if(Minecraft.getInstance().level.isClientSide && LightTracker.LIGHTS.containsValue(pos))
            cir.setReturnValue(15);
    }
}
