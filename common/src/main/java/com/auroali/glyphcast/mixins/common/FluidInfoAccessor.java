package com.auroali.glyphcast.mixins.common;

import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PointedDripstoneBlock.FluidInfo.class)
public interface FluidInfoAccessor {
    @Accessor("fluid")
    Fluid getFluid();
}
