package com.auroali.glyphcast.common.spells.single;

import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Random;

public class EarthSpell extends Spell {
    public EarthSpell() {
        super(new GlyphSequence(Ring.of(Glyph.EARTH)));
    }

    @Override
    public double getCost() {
        return 2;
    }

    @Override
    public void activate(IContext ctx) {
        BlockHitResult result = ctx.clipBlock(ClipContext.Block.OUTLINE, PlayerHelper.getReachDistance(ctx.player()));
        if(result.getType() == HitResult.Type.MISS)
            return;

        BlockPos pos = result.getBlockPos();
        BlockState state = ctx.level().getBlockState(pos);
        if(state.getBlock() instanceof BonemealableBlock bonemealableBlock) {
            if(bonemealableBlock.isValidBonemealTarget(ctx.level(), pos, state, false))
                handleBoneMeal(ctx, result, pos, state);
        }
    }

    void handleBoneMeal(IContext ctx, BlockHitResult result, BlockPos pos, BlockState state) {
        BlockPos adjacent = pos.relative(result.getDirection());
        BlockState adjacentState = ctx.level().getBlockState(adjacent);
        if(adjacentState.canBeReplaced(new BlockPlaceContext(ctx.player(), ctx.hand(), new ItemStack(GCBlocks.BLUE_GLYPH_FLOWER.get()), result)) && GCBlocks.BLUE_GLYPH_FLOWER.get().canSurvive(Blocks.SNOW.defaultBlockState(), ctx.level(), adjacent)) {
            ctx.level().setBlockAndUpdate(adjacent, GCBlocks.BLUE_GLYPH_FLOWER.get().defaultBlockState());
        }
        if(state.getBlock() instanceof BonemealableBlock block) {
            block.performBonemeal((ServerLevel) ctx.level(), ctx.level().random, pos, state);
            ctx.playSound(SoundEvents.BONE_MEAL_USE, 1.0f);
            triggerEvent((byte)0, PositionedContext.with(ctx, pos));
        }
    }

    @Override
    public void handleEvent(Byte id, PositionedContext ctx) {
        BlockPos pos = new BlockPos(ctx.start());
        BlockState state = ctx.level().getBlockState(pos);
        state.getShape(ctx.level(), pos)
                .forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> spawnParticlesForBox(ctx.level(), ctx.level().random, pos, minX, minY, minZ, maxX, maxY, maxZ));
    }

    void spawnParticlesForBox(Level level, RandomSource random, BlockPos pos, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        minX -= 0.1;
        minY -= 0.1;
        minZ -= 0.1;
        maxX += 0.1;
        maxY += 0.1;
        maxZ += 0.1;
        double size = Math.sqrt(maxX*maxX + maxY*maxY + maxZ*maxZ) - Math.sqrt(minX*minX + minY*minY + minZ*minZ);
        for(int i = 0; i < (int) Math.ceil(30 * size); i++) {
            double xPos = minX + random.nextDouble() * (maxX - minX);
            double yPos = minY + random.nextDouble() * (maxY - minY);
            double zPos = minZ + random.nextDouble() * (maxZ - minZ);
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + xPos, pos.getY() + yPos, pos.getZ() + zPos, 0, 0, 0);
        }
    }
}
