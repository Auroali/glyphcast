package com.auroali.glyphcast.common.blocks;

import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.registry.GCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GlyphFlowerBlock extends FlowerBlock {
    public GlyphFlowerBlock() {
        super(() -> MobEffects.HEALTH_BOOST, 300, BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ));
    }

    public void spawnShearedItems(Level level, ItemStack heldStack, BlockPos pos, RandomSource rand) {
        int numDrops = rand.nextInt(1, 2 + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, heldStack));
        for(int i = 0; i < numDrops; i++) {
            ItemStack stack = new ItemStack(GCItems.BLUE_GLYPH_PETAL.get());
            ItemEntity ent = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            ent.setDeltaMovement(ent.getDeltaMovement().add(((rand.nextFloat() - rand.nextFloat()) * 0.1F),(rand.nextFloat() * 0.05F), ((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
            level.addFreshEntity(ent);
        }
    }
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if(stack.is(Items.SHEARS)) {
            pLevel.playSound(pPlayer, pPos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
            spawnShearedItems(pLevel, stack, pPos, pLevel.random);
            pLevel.setBlockAndUpdate(pPos, GCBlocks.TRIMMED_GLYPH_FLOWER.get().defaultBlockState());
            stack.hurtAndBreak(1, pPlayer, (p_186374_) -> {
                p_186374_.broadcastBreakEvent(pHand);
            });
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
