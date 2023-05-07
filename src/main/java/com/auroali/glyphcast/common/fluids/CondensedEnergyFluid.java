package com.auroali.glyphcast.common.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidType;

public class CondensedEnergyFluid extends Fluid {
    @Override
    public Item getBucket() {
        return null;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState pState, BlockGetter pLevel, BlockPos pPos, Fluid pFluid, Direction pDirection) {
        return false;
    }

    @Override
    protected Vec3 getFlow(BlockGetter pBlockReader, BlockPos pPos, FluidState pFluidState) {
        return Vec3.ZERO;
    }

    @Override
    public int getTickDelay(LevelReader pLevel) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getHeight(FluidState pState, BlockGetter pLevel, BlockPos pPos) {
        return 0;
    }

    @Override
    public float getOwnHeight(FluidState pState) {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState pState) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState pState) {
        return false;
    }

    @Override
    public int getAmount(FluidState pState) {
        return 1000;
    }

    @Override
    public VoxelShape getShape(FluidState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.empty();
    }

    @Override
    public FluidType getFluidType() {
        return new FluidType(FluidType.Properties.create()
                .canHydrate(false)
                .canSwim(true)
                .temperature(500)
        );
    }
}
