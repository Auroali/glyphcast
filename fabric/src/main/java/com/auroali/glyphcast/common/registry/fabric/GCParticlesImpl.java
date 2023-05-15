package com.auroali.glyphcast.common.registry.fabric;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class GCParticlesImpl {
    public static SimpleParticleType createSimpleParticleType(boolean bl) {
        return FabricParticleTypes.simple(bl);
    }
}
