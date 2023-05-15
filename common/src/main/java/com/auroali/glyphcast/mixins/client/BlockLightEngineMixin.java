package com.auroali.glyphcast.mixins.client;

import com.auroali.glyphcast.client.LightTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Light Engine Mixin to allow dynamic lighting
 * <br> Based on <a href="https://github.com/infernalstudios/Infernal-Expansion/blob/dde9a0b951bb2de303697e3d58914b3d03c24125/src/main/java/org/infernalstudios/infernalexp/mixin/client/MixinBlockGetter.java">MixinBlockGetter</a>
 *
 * @author Auroali
 */
@Mixin(BlockLightEngine.class)
public class BlockLightEngineMixin {
    /**
     * Inject into the lighting engine,
     * this allows us to manually set the light value of blocks.
     *
     * @param instance the blockgetter instance
     * @param pPos     the block position of the block to check
     * @return the brightness of the block at pPos
     */
    @Redirect(method = "getLightEmission", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;getLightEmission(Lnet/minecraft/core/BlockPos;)I"))
    public int glyphcast$getLightEmission(BlockGetter instance, BlockPos pPos) {
        // We can use the Minecraft class because this is a client-only mixin,
        // it won't be applied to dedicated servers
        // Check to make sure the level is client-side so that the lighting is visual only
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.isClientSide && LightTracker.hasAnyLight(pPos))
            return LightTracker.getBrightnessAtPosition(pPos);
        return instance.getLightEmission(pPos);
    }
}
