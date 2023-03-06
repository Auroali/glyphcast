package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.screen.GlyphEditorScreen;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BlankParchmentItem extends Item implements IGlyphWriteable {
    public BlankParchmentItem() {
        super(new Properties().stacksTo(16).tab(GlyphCast.GLYPHCAST_TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide) {
            // Open the glyph editor screen
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            GlyphEditorScreen editor = new GlyphEditorScreen(pPlayer.getInventory().findSlotMatchingItem(stack));
            Minecraft.getInstance().setScreen(editor);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public ItemStack writeGlyphs(ItemStack stack, GlyphSequence sequence) {
        ItemStack stack1 = GCItems.PARCHMENT.get().withGlyphSequence(sequence);
        stack1.setCount(stack.getCount());
        return stack1;
    }
}
