package com.auroali.glyphcast.common.blocks;

import com.auroali.glyphcast.common.registry.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class TrimmedGlyphFlowerBlock extends FlowerBlock {
    public TrimmedGlyphFlowerBlock() {
        super(() -> MobEffects.BAD_OMEN, 20, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ));
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);
        if(pRandom.nextInt(32) == 0) {
            pLevel.setBlockAndUpdate(pPos, GCBlocks.BLUE_GLYPH_FLOWER.get().defaultBlockState());
        }
    }
}
