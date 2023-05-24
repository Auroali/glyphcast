package com.auroali.glyphcast.common.blocks;

import com.auroali.glyphcast.common.menu.CarvingMenu;
import com.auroali.glyphcast.common.menu.ScribingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ScribingTableBlock extends Block {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(3, 2, 3, 13, 11, 13),
            Block.box(1, 0, 1, 15, 2, 15),
            Block.box(0, 11, 0, 16, 16, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public ScribingTableBlock() {
        super(Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.5f));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @SuppressWarnings("deprecation")
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider((id, inv, player) -> new ScribingMenu(id, inv, ContainerLevelAccess.create(pLevel, pPos)), Component.translatable("menu.title.glyphcast.scribing"));
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide) {
            player.openMenu(state.getMenuProvider(level, pos));
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
