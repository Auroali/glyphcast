package com.auroali.glyphcast.common.fluids;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;

public class CondensedEnergyFluid {
    public static class Source extends ArchitecturyFlowingFluid.Source {
        public Source(ArchitecturyFluidAttributes attributes) {
            super(attributes);
        }

        @Override
        public float getHeight(FluidState state, BlockGetter level, BlockPos pos) {
            return 0;
        }

        @Override
        public float getOwnHeight(FluidState state) {
            return 0;
        }

//        @Override
//        public int getAmount(FluidState state) {
//            return 0;
//        }

//        @Override
//        public Vec3 getFlow(BlockGetter blockReader, BlockPos pos, FluidState fluidState) {
//            return super.getFlow(blockReader, pos, fluidState);
//        }
    }
}
