package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

@SuppressWarnings("unused")
public class GCParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Glyphcast.MODID, Registry.PARTICLE_TYPE_REGISTRY);
    public static final RegistrySupplier<SimpleParticleType> MAGIC_AMBIENCE = PARTICLES.register("magic_ambience", () -> createSimpleParticleType(false));
    public static final RegistrySupplier<SimpleParticleType> MAGIC_PULSE = PARTICLES.register("magic_pulse", () -> createSimpleParticleType(false));
    public static final RegistrySupplier<SimpleParticleType> FRACTURE = PARTICLES.register("fracture", () -> createSimpleParticleType(false));
    public static final RegistrySupplier<SimpleParticleType> MAGIC_DRIP = PARTICLES.register("magic_drip", () -> createSimpleParticleType(false));

    @ExpectPlatform
    public static SimpleParticleType createSimpleParticleType(boolean bl) {
        throw new AssertionError();
    }
}
