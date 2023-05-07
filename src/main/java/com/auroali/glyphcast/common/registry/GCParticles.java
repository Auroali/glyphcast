package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class GCParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, GlyphCast.MODID);
    public static final RegistryObject<SimpleParticleType> MAGIC_AMBIENCE = PARTICLES.register("magic_ambience", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MAGIC_PULSE = PARTICLES.register("magic_pulse", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FRACTURE = PARTICLES.register("fracture", () -> new SimpleParticleType(false));
}
