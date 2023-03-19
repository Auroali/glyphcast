package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EarthSpell extends Spell {
    public EarthSpell() {
        super(new GlyphSequence(Ring.of(Glyph.EARTH)));
    }

    @Override
    public void activate(Level level, Player player) {
        if(!canDrainEnergy(player, 10))
            return;

        drainEnergy(player, 10);
        var result = getTargetBlock(level, player);
        if(result.getType() != HitResult.Type.BLOCK)
            return;

        BlockState state = level.getBlockState(result.getBlockPos());
        BlockState otherState = level.getBlockState(result.getBlockPos().relative(result.getDirection()));
        if (state.getBlock() instanceof BonemealableBlock bonemealableblock) {
            if (bonemealableblock.isValidBonemealTarget(level, result.getBlockPos(), state, level.isClientSide)) {
                if (bonemealableblock.isBonemealSuccess(level, level.random, result.getBlockPos(), state)) {
                    boneMealBlock(level, result, state, otherState, bonemealableblock);
                }
            }
        }
    }

    private void boneMealBlock(Level level, BlockHitResult result, BlockState state, BlockState otherState, BonemealableBlock bonemealableblock) {
        BlockPos pos = result.getBlockPos();
        if(otherState.isAir() && state.canSustainPlant(level, result.getBlockPos(), result.getDirection(), GCBlocks.BLUE_GLYPH_FLOWER.get())) {
            level.setBlockAndUpdate(result.getBlockPos().relative(result.getDirection()), GCBlocks.BLUE_GLYPH_FLOWER.get().defaultBlockState());
            pos = result.getBlockPos().relative(result.getDirection());
        }
        spawnParticles(level, pos);
        bonemealableblock.performBonemeal((ServerLevel) level, level.random, result.getBlockPos(), state);
    }

    public void spawnParticles(Level level, BlockPos pos) {
        Vec3 basePos = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        final Vec3 UP = new Vec3(0, 1, 0);
        for(int i = 0; i < 50; i++) {
            double dist = i / 25.0;
            Vec3 particlePosition = basePos.add(dist * Math.sin(i),0,dist * Math.cos(i));
            SpawnParticlesMessage msg = new SpawnParticlesMessage(ParticleTypes.HAPPY_VILLAGER, 0, 3, particlePosition, UP, i / 50.0);
            GCNetwork.sendToNear(level, basePos, 16, msg);
        }
    }
    private BlockHitResult getTargetBlock(Level level, Player player) {
        return level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(player.getReachDistance())), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null));
    }
}
