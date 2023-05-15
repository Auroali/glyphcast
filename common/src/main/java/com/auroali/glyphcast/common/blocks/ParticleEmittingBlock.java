package com.auroali.glyphcast.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class ParticleEmittingBlock extends Block {
    ParticleOptions particle;
    Supplier<ParticleOptions> particleSupplier;

    public ParticleEmittingBlock(Properties properties, Supplier<ParticleOptions> particle) {
        super(properties);
        this.particleSupplier = particle;
    }

    /**
     * @see net.minecraft.world.level.block.RedStoneOreBlock#animateTick(BlockState, Level, BlockPos, RandomSource)
     */
    private static void spawnParticles(Level pLevel, BlockPos pPos, ParticleOptions particle) {
        RandomSource randomsource = pLevel.random;

        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pPos.relative(direction);
            if (!pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) randomsource.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) randomsource.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) randomsource.nextFloat();
                pLevel.addParticle(particle, (double) pPos.getX() + d1, (double) pPos.getY() + d2, (double) pPos.getZ() + d3, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    public ParticleOptions getParticle() {
        if (particle == null)
            particle = particleSupplier.get();
        return particle;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        spawnParticles(pLevel, pPos, getParticle());
    }
}
