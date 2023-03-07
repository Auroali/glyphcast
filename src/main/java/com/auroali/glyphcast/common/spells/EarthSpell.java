package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EarthSpell extends Spell {
    public EarthSpell() {
        super(new GlyphSequence(Glyph.EARTH));
    }

    @Override
    public void activate(Level level, Player player) {
        var result = getTargetBlock(level, player);
        if(result.getType() != HitResult.Type.BLOCK)
            return;

        BlockState state = level.getBlockState(result.getBlockPos());
        BlockState otherState = level.getBlockState(result.getBlockPos().relative(result.getDirection()));
        if(otherState.isAir() && state.canSustainPlant(level, result.getBlockPos(), result.getDirection(), GCBlocks.BLUE_GLYPH_FLOWER.get())) {
            level.setBlockAndUpdate(result.getBlockPos().relative(result.getDirection()), GCBlocks.BLUE_GLYPH_FLOWER.get().defaultBlockState());
        }
    }

    private BlockHitResult getTargetBlock(Level level, Player player) {
        return level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(player.getReachDistance())), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
    }
}
