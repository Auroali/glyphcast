package com.auroali.glyphcast.common.blocks;

import com.auroali.glyphcast.common.registry.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class TrimmedGlyphFlowerBlock extends FlowerBlock {
    public TrimmedGlyphFlowerBlock() {
        super(MobEffects.BAD_OMEN, 20, Properties.of(Material.PLANT).randomTicks().noCollission().instabreak().sound(SoundType.GRASS).offsetType(OffsetType.XZ));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);
        if (pRandom.nextInt(28) == 0) {
            pLevel.setBlockAndUpdate(pPos, GCBlocks.BLUE_GLYPH_FLOWER.get().defaultBlockState());
        }
    }
}
