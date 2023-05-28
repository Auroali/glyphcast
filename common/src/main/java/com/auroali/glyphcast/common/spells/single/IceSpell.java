package com.auroali.glyphcast.common.spells.single;

import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class IceSpell extends Spell {
    public IceSpell() {
        super(new GlyphSequence(Ring.of(Glyph.ICE)));
    }

    @Override
    public void activate(IContext ctx) {
        EntityHitResult entityResult = ctx.clipEntity(PlayerHelper.getReachDistance(ctx.player()), e -> e instanceof LivingEntity);
        BlockHitResult blockResult = ctx.clipBlock(PlayerHelper.getReachDistance(ctx.player()));
        if(entityResult != null && blockResult.distanceTo(ctx.player()) > entityResult.distanceTo(ctx.player()))
            handleEntityHit(ctx, entityResult);
        else if(blockResult.getType() != HitResult.Type.MISS)
            handleBlockHit(ctx, blockResult);
    }

    void handleEntityHit(IContext ctx, EntityHitResult result) {
        ctx.playSound(SoundEvents.PLAYER_HURT_FREEZE, 1.0f);
        result.getEntity().hurt(DamageSource.FREEZE, 5.0f);
        result.getEntity().setTicksFrozen(Math.min(result.getEntity().getTicksFrozen() + 40, result.getEntity().getTicksRequiredToFreeze() + 20));
        Vec3 min = new Vec3(result.getEntity().getBoundingBox().minX, result.getEntity().getBoundingBox().minY, result.getEntity().getBoundingBox().minZ);
        Vec3 max = new Vec3(result.getEntity().getBoundingBox().maxX, result.getEntity().getBoundingBox().maxY, result.getEntity().getBoundingBox().maxZ);
        triggerEvent((byte)1, PositionedContext.withRange(ctx, min, max));
    }
    void handleBlockHit(IContext ctx, BlockHitResult result) {
        BlockPos hitPos = result.getBlockPos();
        BlockPos adjacentPos = result.getBlockPos().relative(result.getDirection());
        BlockState state = ctx.level().getBlockState(hitPos);
        BlockState adjacent = ctx.level().getBlockState(adjacentPos);
        if(state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
            ctx.level().setBlockAndUpdate(hitPos, state.setValue(BlockStateProperties.LIT, false));
            triggerEvent((byte)0, PositionedContext.with(ctx, hitPos));
            ctx.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0f);
            return;
        }
        if((!state.hasProperty(BlockStateProperties.WATERLOGGED) || !state.getValue(BlockStateProperties.WATERLOGGED)) && state.getFluidState().is(Fluids.WATER)) {
            ctx.level().setBlockAndUpdate(hitPos, Blocks.FROSTED_ICE.defaultBlockState());
            triggerEvent((byte)0, PositionedContext.with(ctx, hitPos));
            ctx.playSound(SoundEvents.GLASS_PLACE, 1.0f);
            return;
        }
        if(state.getFluidState().is(Fluids.LAVA)) {
            ctx.level().setBlockAndUpdate(hitPos, Blocks.OBSIDIAN.defaultBlockState());
            triggerEvent((byte)0, PositionedContext.with(ctx, hitPos));
            ctx.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0f);
            return;
        }
        if(adjacent.canBeReplaced(new BlockPlaceContext(ctx.player(), ctx.hand(), new ItemStack(Blocks.SNOW), result)) && Blocks.SNOW.canSurvive(Blocks.SNOW.defaultBlockState(), ctx.level(), adjacentPos)) {
            ctx.level().setBlockAndUpdate(adjacentPos, Blocks.SNOW.defaultBlockState());
            triggerEvent((byte)0, PositionedContext.with(ctx, adjacentPos));
            ctx.playSound(SoundEvents.SNOW_PLACE, 1.0f);
            return;
        }
    }

    @Override
    public void handleEvent(Byte id, PositionedContext ctx) {
        if(id == 0) {
            Vec3 start = ctx.start().add(0.5, 0.5, 0.5);
            for (int i = 0; i < 15; i++) {
                ctx.level().addParticle(ParticleTypes.SNOWFLAKE, start.x, start.y, start.z, 0, 0.15, 0);
            }
        }
        if(id == 1) {
            Vec3 min = ctx.start();
            Vec3 max = ctx.end().subtract(min);
            double dist = min.distanceToSqr(ctx.end());
            for(int i = 0; i < 15 * dist; i++) {
                double posX = ctx.level().random.nextDouble() * max.x + min.x;
                double posY = ctx.level().random.nextDouble() * max.y + min.y;
                double posZ = ctx.level().random.nextDouble() * max.z + min.z;
                ctx.level().addParticle(ParticleTypes.SNOWFLAKE, posX, posY, posZ, 0, 0, 0);
            }
        }
    }

    @Override
    public double getCost() {
        return 2;
    }
}
