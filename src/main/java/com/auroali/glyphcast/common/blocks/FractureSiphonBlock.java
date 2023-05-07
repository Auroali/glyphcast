package com.auroali.glyphcast.common.blocks;

import com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy;
import com.auroali.glyphcast.common.registry.GCFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FractureSiphonBlock extends Block {
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");
    public static final VoxelShape SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 6, 15), Block.box(2, 6, 2, 14, 16, 14));

    public FractureSiphonBlock() {
        super(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(2.0f, 6));
        this.registerDefaultState(getStateDefinition().any().setValue(CHARGED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(CHARGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return state.setValue(CHARGED, IChunkEnergy.getFractureAt(pContext.getLevel(), pContext.getClickedPos()).isPresent());
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(CHARGED) ? GCFluids.CONDENSED_ENERGY.get().defaultFluidState() : super.getFluidState(pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
}
