package com.auroali.glyphcast.client;

import net.minecraft.core.BlockPos;

/**
 * A record containing dynamic lighting information
 * @param position the block position for the dynamic light source
 * @param brightness the brightness of the dynamic light source
 */
public record DynamicLightSource(BlockPos position, int brightness) {}
