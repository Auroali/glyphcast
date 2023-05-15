package com.auroali.glyphcast.common.registry.forge;

import net.minecraft.core.particles.SimpleParticleType;

public class GCParticlesImpl {
    public static SimpleParticleType createSimpleParticleType(boolean bl) {
        return new SimpleParticleType(bl);
    }
}
