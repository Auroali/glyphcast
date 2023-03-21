package com.auroali.glyphcast.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class MagicAmbienceProvider implements ParticleProvider<SimpleParticleType> {
    final SpriteSet sprite;

    public MagicAmbienceProvider(SpriteSet sprite) {
        this.sprite = sprite;
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        GlowParticle glowparticle = new GlowParticle(pLevel, pX, pY, pZ, 0.0D, 0.0D, 0.0D, this.sprite);
        glowparticle.setColor(1.0F, 1.0F, 1.0F);
        glowparticle.setParticleSpeed(pXSpeed * 0.01D / 2.0D, pYSpeed * 0.01D, pZSpeed * 0.01D / 2.0D);
        glowparticle.setLifetime(pLevel.random.nextInt(30) + 10);
        return glowparticle;
    }
}
