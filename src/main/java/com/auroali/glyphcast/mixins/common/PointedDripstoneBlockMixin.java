package com.auroali.glyphcast.mixins.common;

import com.auroali.glyphcast.common.registry.GCFluids;
import com.auroali.glyphcast.common.registry.GCParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

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
        if (!optional.get().fluid().isSame(GCFluids.CONDENSED_ENERGY.get()))
            return;

        if (!(pRandChance >= 0.17578125F)) {
            BlockPos blockpos = findTip(pState, pLevel, pPos, 11, false);
            if (blockpos != null) {
                BlockPos blockpos1 = findFillableCauldronBelowStalactiteTip(pLevel, blockpos, optional.get().fluid());
                if (blockpos1 != null) {
                    pLevel.levelEvent(1504, blockpos, 0);
                    int i = blockpos.getY() - blockpos1.getY();
                    int j = 50 + i;
                    BlockState blockstate = pLevel.getBlockState(blockpos1);
                    pLevel.scheduleTick(blockpos1, blockstate.getBlock(), j);
                }
            }
        }
        ci.cancel();
    }
}
