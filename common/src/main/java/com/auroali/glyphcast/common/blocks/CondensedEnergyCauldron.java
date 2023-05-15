package com.auroali.glyphcast.common.blocks;

import com.auroali.glyphcast.common.registry.GCFluids;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class CondensedEnergyCauldron extends LayeredCauldronBlock {
    public static final Object2ObjectOpenHashMap<Item, CauldronInteraction> INTERACTIONS = CauldronInteraction.newInteractionMap();

    public CondensedEnergyCauldron() {
        super(Properties.copy(Blocks.CAULDRON), p -> false, INTERACTIONS);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return new ItemStack(Blocks.CAULDRON);
    }

    @Override
    protected boolean canReceiveStalactiteDrip(Fluid pFluid) {
        return pFluid.isSame(GCFluids.CONDENSED_ENERGY.get());
    }
}
