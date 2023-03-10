package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class IceSpell extends Spell{
    public IceSpell() {
        super(new GlyphSequence(Ring.of(Glyph.ICE)));
    }

    @Override
    public void activate(Level level, Player player) {
        if(level instanceof ServerLevel serverLevel) {
            Vec3 eyePos = player.getEyePosition();
            GCNetwork.sendToClient((ServerPlayer) player, new SpawnParticlesMessage(ParticleTypes.SNOWFLAKE, 0.16d, 40, eyePos.add(player.getLookAngle().scale(0.25f)), player.getLookAngle(), 0.5f));
            rayTraceBlocks(eyePos.add(player.getLookAngle().cross(new Vec3(0, 1, 0))), player.getLookAngle().scale(6), 12, serverLevel);
            rayTraceBlocks(eyePos, player.getLookAngle().scale(6), 12, serverLevel);
            rayTraceBlocks(eyePos.subtract(player.getLookAngle().cross(new Vec3(0, 1, 0))), player.getLookAngle().scale(6), 12, serverLevel);
        }
    }

    void rayTraceBlocks(Vec3 start, Vec3 end, float numMarches, ServerLevel serverLevel) {
        var endCheck = serverLevel.clip(new ClipContext(start, start.add(end), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        if(endCheck.getType() != HitResult.Type.MISS)
            end = endCheck.getLocation().subtract(start);
        for(int i = 0; i < numMarches; i++) {
            Vec3 start1 = start.add(end.scale((double) i / (double)numMarches));
            Vec3 end1 = start1.add(new Vec3(0, -8, 0));
            var result = serverLevel.clip(new ClipContext(start1, end1, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
            if(result.getType() == HitResult.Type.BLOCK) {
                BlockState state = serverLevel.getBlockState(result.getBlockPos());
                BlockState aboveState = serverLevel.getBlockState(result.getBlockPos().above());
                handleLitBlocks(serverLevel, result, state);
                setSnowLayer(serverLevel, result, state, aboveState);
                freezeWater(serverLevel, result, state);
            }
        }
    }

    private void freezeWater(ServerLevel serverLevel, BlockHitResult result, BlockState state) {
        if(state.is(Blocks.WATER)) {
            serverLevel.setBlockAndUpdate(result.getBlockPos(), Blocks.FROSTED_ICE.defaultBlockState());
        }
    }

    private void setSnowLayer(ServerLevel serverLevel, BlockHitResult result, BlockState state, BlockState aboveState) {
        if(state.isFaceSturdy(serverLevel, result.getBlockPos(), Direction.UP, SupportType.FULL) && canReplace(aboveState))
            serverLevel.setBlockAndUpdate(result.getBlockPos().above(), Blocks.SNOW.defaultBlockState());
    }

    private void handleLitBlocks(ServerLevel serverLevel, BlockHitResult result, BlockState state) {
        if(state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
            serverLevel.playSound(null, result.getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
            serverLevel.setBlockAndUpdate(result.getBlockPos(), state.setValue(BlockStateProperties.LIT, false));
        }
    }

    boolean canReplace(BlockState state) {
        return state.isAir() || state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE);
    }
}
