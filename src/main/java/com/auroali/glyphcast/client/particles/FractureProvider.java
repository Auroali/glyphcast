package com.auroali.glyphcast.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class FractureProvider implements ParticleProvider<SimpleParticleType> {
    final SpriteSet sprite;

    public FractureProvider(SpriteSet sprite) {
        this.sprite = sprite;
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        FractureParticle glowparticle = new FractureParticle(pLevel, pX, pY, pZ, 0.0D, 0.0D, 0.0D, this.sprite);
        glowparticle.setColor(1.0F, 1.0F, 1.0F);
        glowparticle.setParticleSpeed(0, 0, 0);
        glowparticle.setLifetime(1);
        return glowparticle;
    }

    public static class FractureParticle extends TextureSheetParticle {
        private final SpriteSet sprites;

        public FractureParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
            super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            this.hasPhysics = false;
            this.sprites = pSprites;
            this.setSpriteFromAge(pSprites);
        }

        @Override
        public int getLightColor(float pPartialTick) {
            return 255;
        }

        @Override
        public Particle scale(float pScale) {
            return this;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        public void tick() {
            super.tick();
            this.setSpriteFromAge(this.sprites);
        }

        @Override
        public float getQuadSize(float pScaleFactor) {
            return 1.0f;
        }
    }
}
