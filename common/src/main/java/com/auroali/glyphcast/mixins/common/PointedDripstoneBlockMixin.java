package com.auroali.glyphcast.mixins.common;

import com.auroali.glyphcast.common.entities.FractureEntity;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import com.auroali.glyphcast.common.registry.GCFluids;
import com.auroali.glyphcast.common.registry.GCParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static net.minecraft.world.level.block.PointedDripstoneBlock.FluidInfo;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

    // Invokers
    @Invoker("findTip")
    public static BlockPos findTip(BlockState pState, LevelAccessor pLevel, BlockPos pPos, int pMaxIterations, boolean pIsTipMerge) {
        throw new AssertionError();
    }

    @Invoker("findFillableCauldronBelowStalactiteTip")
    static BlockPos findFillableCauldronBelowStalactiteTip(Level pLevel, BlockPos pPos, Fluid pFluid) {
        throw new AssertionError();
    }

    @Invoker("findBlockVertical")
    static Optional<BlockPos> findBlockVertical(LevelAccessor level, BlockPos pos, Direction.AxisDirection axis, BiPredicate<BlockPos, BlockState> positionalStatePredicate, Predicate<BlockState> statePredicate, int maxIterations) {
        throw new AssertionError();
    }

    @Invoker("canDripThrough")
    static boolean canDripThrough(BlockGetter level, BlockPos pos, BlockState state) {
        throw new AssertionError();
    }

    @Inject(method = "canFillCauldron", at = @At("HEAD"), cancellable = true)
    private static void glyphcast$canFillCauldron(Fluid p_154159_, CallbackInfoReturnable<Boolean> cir) {
        if (p_154159_.isSame(GCFluids.CONDENSED_ENERGY.get()))
            cir.setReturnValue(true);
    }

    @Inject(method = "spawnDripParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void glyphcast$spawnDripParticle(Level pLevel, BlockPos pPos, BlockState pState, Fluid pFluid, CallbackInfo ci) {
        if (!pFluid.isSame(GCFluids.CONDENSED_ENERGY.get()))
            return;

        Vec3 vec3 = pState.getOffset(pLevel, pPos);
        double d1 = (double) pPos.getX() + 0.5D + vec3.x;
        double d2 = (double) ((float) (pPos.getY() + 1) - 0.6875F) - 0.0625D;
        double d3 = (double) pPos.getZ() + 0.5D + vec3.z;
        ParticleOptions particleoptions = GCParticles.MAGIC_DRIP.get();
        pLevel.addParticle(particleoptions, d1, d2, d3, 0.0D, 0.0D, 0.0D);
        ci.cancel();
    }

    @Inject(
            method = "maybeTransferFluid",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock$FluidInfo;fluid:Lnet/minecraft/world/level/material/Fluid;",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true
    )
    private static void glyphcast$maybeTransferFluid(BlockState pState, ServerLevel pLevel, BlockPos pPos, float pRandChance, CallbackInfo ci, Optional<FluidInfo> optional) {
        if (!((FluidInfoAccessor) (Object) optional.get()).getFluid().isSame(GCFluids.CONDENSED_ENERGY.get()))
            return;

        if (!(pRandChance >= 0.17578125F)) {
            BlockPos blockpos = findTip(pState, pLevel, pPos, 11, false);
            if (blockpos != null) {
                BlockPos blockpos1 = findFillableCauldronBelowStalactiteTip(pLevel, blockpos, ((FluidInfoAccessor) (Object) optional.get()).getFluid());
                if (blockpos1 != null) {
                    pLevel.levelEvent(1504, blockpos, 0);
                    int i = blockpos.getY() - blockpos1.getY();
                    int j = 50 + i;
                    BlockState blockstate = pLevel.getBlockState(blockpos1);
                    pLevel.scheduleTick(blockpos1, blockstate.getBlock(), j);
                }
                BlockPos infusePos = findInfusableBlock(pLevel, blockpos);
                if(infusePos != null) {
                    ItemStack inputStack = pLevel.getBlockState(infusePos).getBlock().getCloneItemStack(pLevel, pPos, pLevel.getBlockState(pPos));
                    InfuseRecipe recipe = InfuseRecipe.getFor(
                            pLevel.getRecipeManager(),
                            inputStack,
                            ItemStack.EMPTY
                    );
                    FractureEntity.getAt(pLevel, optional.get().pos()).ifPresent(f -> {
                        if(f.drain(recipe.cost(), true) != recipe.cost())
                            return;

                        f.drain(recipe.cost());

                        ItemStack result = recipe.assemble(inputStack, ItemStack.EMPTY);
                        BlockState state = Block.byItem(result.getItem()).defaultBlockState();

                        if(state.isAir())
                            spawnItemEntity(pLevel, result, infusePos);

                        pLevel.setBlockAndUpdate(infusePos, state);
                        for (int i = 0; i < 8; i++) {
                            for (Direction direction : Direction.values()) {
                                BlockPos particlePos = infusePos.relative(direction);
                                if (pLevel.getBlockState(particlePos).isSolidRender(pLevel, particlePos))
                                    continue;
                                Direction.Axis direction$axis = direction.getAxis();
                                double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) pLevel.random.nextFloat();
                                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) pLevel.random.nextFloat();
                                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) pLevel.random.nextFloat();
                                SpawnParticlesMessage msg = new SpawnParticlesMessage(GCParticles.MAGIC_AMBIENCE.get(), 0, 1, new Vec3((double) infusePos.getX() + d1, (double) infusePos.getY() + d2, (double) infusePos.getZ() + d3), Vec3.ZERO, 0);
                                GCNetwork.CHANNEL.sendToTracking(pLevel, infusePos, msg);
                            }
                        }
                    });
                }
            }
        }
        ci.cancel();
    }

    private static void spawnItemEntity(Level level, ItemStack result, BlockPos pos) {
        float f = EntityType.ITEM.getHeight() / 2.0F;
        double d0 = (double) ((float) pos.getX() + 0.5F) + Mth.nextDouble(level.random, -0.25D, 0.25D);
        double d1 = (double) ((float) pos.getY() + 0.5F) + Mth.nextDouble(level.random, -0.25D, 0.25D) - (double) f;
        double d2 = (double) ((float) pos.getZ() + 0.5F) + Mth.nextDouble(level.random, -0.25D, 0.25D);
        ItemEntity item = new ItemEntity(level, d0, d1, d2, result);
        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
    }

    @Nullable
    private static BlockPos findInfusableBlock(Level level, BlockPos pos) {
        Predicate<BlockState> predicate = blockState -> InfuseRecipe.anyMatch(level.getRecipeManager(), blockState.getBlock().getCloneItemStack(level, pos, blockState), ItemStack.EMPTY);
        BiPredicate<BlockPos, BlockState> biPredicate = (blockPos, blockState) -> canDripThrough(level, blockPos, blockState);
        return findBlockVertical(level, pos, Direction.DOWN.getAxisDirection(), biPredicate, predicate, 11).orElse(null);
    }

}
