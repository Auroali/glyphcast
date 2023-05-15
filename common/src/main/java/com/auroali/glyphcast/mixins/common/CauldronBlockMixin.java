package com.auroali.glyphcast.mixins.common;

import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.registry.GCFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronBlock.class)
public class CauldronBlockMixin {
    @Inject(method = "receiveStalactiteDrip", at = @At("HEAD"))
    public void glyphcast$receiveStalactiteDrip(BlockState pState, Level pLevel, BlockPos pPos, Fluid pFluid, CallbackInfo ci) {
        if (pFluid.isSame(GCFluids.CONDENSED_ENERGY.get())) {
            BlockState blockstate = GCBlocks.CONDENSED_ENERGY_CAULDRON.get()
                    .defaultBlockState()
                    .setValue(LayeredCauldronBlock.LEVEL, 1);
            pLevel.setBlockAndUpdate(pPos, blockstate);
            pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(blockstate));
            pLevel.levelEvent(1047, pPos, 0);
        }
    }
}
