package com.auroali.glyphcast.mixins.common;

import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SimpleParticleType.class)
public class SimpleParticleTypeAccessor {
    @Invoker("<init>")
    public static SimpleParticleType createNew(boolean bl) {
        throw new AssertionError();
    }
}
