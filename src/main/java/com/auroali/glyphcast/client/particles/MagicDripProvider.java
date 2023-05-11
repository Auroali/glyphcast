package com.auroali.glyphcast.client.particles;

import com.auroali.glyphcast.common.registry.GCFluids;
import com.auroali.glyphcast.common.registry.GCParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class MagicDripProvider implements ParticleProvider<SimpleParticleType> {
    final SpriteSet sprite;

    public MagicDripProvider(SpriteSet sprite) {
        this.sprite = sprite;
    }

    @Nullable
    @Override
    public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        DripParticle glowparticle = new MagicFallAndLandParticle(pLevel, pX, pY, pZ, GCFluids.CONDENSED_ENERGY.get(), GCParticles.MAGIC_AMBIENCE.get());
        glowparticle.pickSprite(sprite);
        return glowparticle;
    }

    static class MagicFallAndLandParticle extends DripParticle.FallAndLandParticle {
        MagicFallAndLandParticle(ClientLevel p_171930_, double p_171931_, double p_171932_, double p_171933_, Fluid p_171934_, ParticleOptions p_171935_) {
            super(p_171930_, p_171931_, p_171932_, p_171933_, p_171934_, p_171935_);
            this.isGlowing = true;
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
                SoundEvent soundevent = SoundEvents.AMETHYST_BLOCK_CHIME;
                float f = Mth.randomBetween(this.random, 0.3F, 1.0F);
                this.level.playLocalSound(this.x, this.y, this.z, soundevent, SoundSource.BLOCKS, f, 1.0F, false);
            }

        }
    }
}
