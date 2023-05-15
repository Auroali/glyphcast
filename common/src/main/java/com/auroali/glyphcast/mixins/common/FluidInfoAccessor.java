package com.auroali.glyphcast.mixins.common;

import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net/minecraft/world/level/block/PointedDripstoneBlock$FluidInfo")
public interface FluidInfoAccessor {
    @Accessor("fluid")
    Fluid getFluid();
}
