package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.common.network.client.ClientPacketHandler;
import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.registry.GCBlocks;
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
    public double getCost() {
        return 10;
    }

    @Override
    public void activate(IContext ctx) {
        var result = getTargetBlock(ctx.level(), ctx.player());
        if (result.getType() != HitResult.Type.BLOCK)
            return;

        BlockState state = ctx.level().getBlockState(result.getBlockPos());
        BlockState otherState = ctx.level().getBlockState(result.getBlockPos().relative(result.getDirection()));
        if (state.getBlock() instanceof BonemealableBlock bonemealableblock) {
            if (bonemealableblock.isValidBonemealTarget(ctx.level(), result.getBlockPos(), state, ctx.level().isClientSide)) {
                if (bonemealableblock.isBonemealSuccess(ctx.level(), ctx.level().random, result.getBlockPos(), state)) {
                    boneMealBlock(ctx, ctx.level(), result, state, otherState, bonemealableblock);
                }
            }
        }
    }

    private void boneMealBlock(IContext ctx, Level level, BlockHitResult result, BlockState state, BlockState otherState, BonemealableBlock bonemealableblock) {
        BlockPos pos = result.getBlockPos();
        if (otherState.isAir() && GCBlocks.BLUE_GLYPH_FLOWER.get().canSurvive(otherState, level, result.getBlockPos().relative(result.getDirection()))) {
            level.setBlockAndUpdate(result.getBlockPos().relative(result.getDirection()), GCBlocks.BLUE_GLYPH_FLOWER.get().defaultBlockState());
            pos = result.getBlockPos().relative(result.getDirection());
        }
        triggerEvent((byte) 0, PositionedContext.with(ctx, pos));
        bonemealableblock.performBonemeal((ServerLevel) level, level.random, result.getBlockPos(), state);
    }

    @Override
    public void handleEvent(Byte id, PositionedContext ctx) {
        spawnParticles(new BlockPos(ctx.start()));
    }

    public void spawnParticles(BlockPos pos) {
        Vec3 basePos = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        final Vec3 UP = new Vec3(0, 1, 0);
        for (int i = 0; i < 50; i++) {
            double dist = i / 25.0;
            Vec3 particlePosition = basePos.add(dist * Math.sin(i), 0, dist * Math.cos(i));
            SpawnParticlesMessage msg = new SpawnParticlesMessage(ParticleTypes.HAPPY_VILLAGER, 0, 3, particlePosition, UP, i / 50.0);
            ClientPacketHandler.spawnParticles(msg);
        }
    }

    private BlockHitResult getTargetBlock(Level level, Player player) {
        return level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(4.0f)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null));
    }
}
