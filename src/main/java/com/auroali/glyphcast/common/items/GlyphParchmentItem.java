package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellStats;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class GlyphParchmentItem extends Item implements ISpellHolder {

    public GlyphParchmentItem() {
        super(new Properties().stacksTo(16).tab(GlyphCast.GLYPHCAST_TAB));
    }

    @Override
    public Component getName(ItemStack pStack) {
        var spell = getSpell(pStack);

        return spell.isPresent() ? Component.translatable("item.glyphcast.parchment_of_spell", spell.map(Spell::getName).orElse(Component.literal("error"))) : super.getName(pStack);
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pCategory)) {
            pItems.add(withGlyphSequence(new GlyphSequence(Ring.of(Glyph.FIRE))));
            pItems.add(withGlyphSequence(new GlyphSequence(Ring.of(Glyph.LIGHT))));
            pItems.add(withGlyphSequence(new GlyphSequence(Ring.of(Glyph.ICE))));
            pItems.add(withGlyphSequence(new GlyphSequence(Ring.of(Glyph.EARTH))));
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        GlyphSequence sequence = getSequence(pStack);
        if (sequence == null)
            return Optional.empty();
        return Optional.of(new GlyphTooltipComponent(getSequence(pStack)));
    }

    public ItemStack withGlyphSequence(GlyphSequence sequence) {
        ItemStack stack = new ItemStack(this);
        this.writeSequence(stack, sequence);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);

        if (!pLevel.isClientSide) {
            Optional<Spell> spell = getSpell(stack);
            spell.ifPresent(_spell -> {
                _spell.tryActivate(pLevel, pPlayer, pUsedHand, SpellStats.PARCHMENT);
                pPlayer.getCooldowns().addCooldown(this, SpellStats.PARCHMENT.cooldown());
                if (pPlayer instanceof ServerPlayer) {
                    CriteriaTriggers.USING_ITEM.trigger((ServerPlayer) pPlayer, stack);
                }
            });

            if (spell.isPresent() && !pPlayer.isCreative())
                stack.shrink(1);

            // Mark the spell as discovered
            SpellUser.get(pPlayer).ifPresent(user -> {
                GlyphSequence sequence = spell.map(Spell::getSequence).orElse(GlyphSequence.EMPTY);
                sequence.asList().forEach(user::markGlyphDiscovered);
            });

            return spell.isPresent() ? InteractionResultHolder.consume(stack) : InteractionResultHolder.pass(stack);
        }
        return InteractionResultHolder.success(stack);
    }
}
