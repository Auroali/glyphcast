package com.auroali.glyphcast.common.spells.wand;

import com.auroali.glyphcast.common.network.client.ClientPacketHandler;
import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import com.auroali.glyphcast.common.registry.GCParticles;
import com.auroali.glyphcast.common.registry.GCRecipeTypes;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagicInfuseSpell extends Spell {
    public MagicInfuseSpell() {
        super(new GlyphSequence(Ring.of(Glyph.WAND)));
    }

    @Override
    public double getCost() {
        return 0;
    }

    @Override
    public void activate(IContext ctx) {
        var blockStackPair = getTargetedBlock(ctx.level(), ctx.player());
        var entityPair = getTargetedEntityStack(ctx.level(), ctx.player());
        if (blockStackPair == null && entityPair == null)
            return;
        ItemStack stack = entityPair != null ? entityPair.first() : blockStackPair.first();
        ItemStack offhandStack = ctx.getOtherHandItem();
        List<InfuseRecipe> recipes = ctx.level().getServer().getRecipeManager().getAllRecipesFor(GCRecipeTypes.INFUSE_RECIPE.get());
        recipes.stream().filter(r -> r.itemsMatch(stack, offhandStack)).findFirst().ifPresent(r -> {
            if (!canDrainEnergy(ctx.stats(), ctx.player(), r.cost()))
                return;
            if (!ctx.player().isCreative() && r.consumesOther())
                offhandStack.shrink(1);
            ItemStack result = r.assemble(stack);
            result.setCount(stack.getCount());
            drainEnergy(ctx.stats(), ctx.player(), r.cost());
            if (entityPair != null)
                transformEntity(ctx, ctx.level(), entityPair, result);
            else
                transformBlock(ctx, ctx.level(), blockStackPair, result);
        });
    }

    void transformBlock(IContext ctx, Level level, Pair<ItemStack, BlockPos> pair, ItemStack stack) {
        BlockState state = Block.byItem(stack.getItem()).defaultBlockState();
        level.setBlockAndUpdate(pair.right(), state);
        float f = EntityType.ITEM.getHeight() / 2.0F;
        double d0 = (double) ((float) pair.second().getX() + 0.5F) + Mth.nextDouble(level.random, -0.25D, 0.25D);
        double d1 = (double) ((float) pair.second().getY() + 0.5F) + Mth.nextDouble(level.random, -0.25D, 0.25D) - (double) f;
        double d2 = (double) ((float) pair.second().getZ() + 0.5F) + Mth.nextDouble(level.random, -0.25D, 0.25D);
        triggerEvent((byte) 0, PositionedContext.with(ctx, new Vec3(pair.right().getX(), pair.right().getY(), pair.right().getZ())));
        if (state.isAir()) {
            ItemEntity entity = new ItemEntity(level, d0, d1, d2, stack);
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }

    }

    void transformEntity(IContext ctx, Level level, Pair<ItemStack, ItemEntity> pair, ItemStack stack) {
        float f = EntityType.ITEM.getHeight() / 2.0F;
        double d0 = (double) ((float) pair.second().getX()) + Mth.nextDouble(level.random, -0.25D, 0.25D);
        double d1 = (float) pair.second().getY() + 0.5 + Mth.nextDouble(level.random, -0.25D, 0.25D) - (double) f;
        double d2 = (double) ((float) pair.second().getZ()) + Mth.nextDouble(level.random, -0.25D, 0.25D);
        ItemEntity entity = new ItemEntity(level, d0, d1, d2, stack);
        entity.setDefaultPickUpDelay();
        pair.second().remove(Entity.RemovalReason.DISCARDED);
        level.addFreshEntity(entity);
        triggerEvent((byte) 1, PositionedContext.with(ctx, entity.position()));
    }

    @Override
    public void handleEvent(Byte id, PositionedContext ctx) {
        if (id == 0)
            spawnParticles(ctx.level(), new BlockPos(ctx.start()));
        if (id == 1)
            spawnParticles(ctx.level(), ctx.start());
    }

    void spawnParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 8; i++) {
            for (Direction direction : Direction.values()) {
                BlockPos particlePos = pos.relative(direction);
                if (level.getBlockState(particlePos).isSolidRender(level, particlePos))
                    continue;
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) level.random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) level.random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) level.random.nextFloat();
                SpawnParticlesMessage msg = new SpawnParticlesMessage(GCParticles.MAGIC_AMBIENCE.get(), 0, 1, new Vec3((double) pos.getX() + d1, (double) pos.getY() + d2, (double) pos.getZ() + d3), Vec3.ZERO, 0);
                ClientPacketHandler.spawnParticles(msg);
            }
        }
    }

    void spawnParticles(Level level, Vec3 pos) {
        for (int i = 0; i < 2; i++) {
            for (Direction direction : Direction.values()) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) level.random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) level.random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) level.random.nextFloat();
                d1 *= 0.35;
                d2 *= 0.35;
                d3 *= 0.35;
                SpawnParticlesMessage msg = new SpawnParticlesMessage(GCParticles.MAGIC_AMBIENCE.get(), 0, 1, new Vec3(pos.x + d1, pos.y + d2, pos.z + d3), Vec3.ZERO, 0);
                ClientPacketHandler.spawnParticles(msg);
            }
        }
    }

    public Pair<ItemStack, BlockPos> getTargetedBlock(Level level, Player player) {
        BlockHitResult bResult = level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(player.getReachDistance())), ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, null));
        if (bResult.getType() == HitResult.Type.MISS)
            return null;

        ItemStack stack = level.getBlockState(bResult.getBlockPos()).getCloneItemStack(bResult, level, bResult.getBlockPos(), player);
        return Pair.of(stack, bResult.getBlockPos());
    }

    public Pair<ItemStack, ItemEntity> getTargetedEntityStack(Level level, Player player) {
        EntityHitResult eResult = clipEntityFromPlayer(player, player.getReachDistance(), e -> e instanceof ItemEntity);
        if (eResult == null || eResult.getType() == HitResult.Type.MISS)
            return null;

        ItemEntity entity = (ItemEntity) eResult.getEntity();
        return Pair.of(entity.getItem(), entity);
    }
}
