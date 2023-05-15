package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

/**
 * Interface for items that can hold spells
 *
 * @author Auroali
 * @see GlyphParchmentItem
 */
public interface ISpellHolder {

    /**
     * Write a sequence of glyphs to an item stack
     *
     * @param stack    the stack to write to
     * @param sequence the sequence of glyphs
     */
    default void writeSequence(ItemStack stack, GlyphSequence sequence) {
        CompoundTag tag = sequence.serialize();
        stack.getOrCreateTag().put("glyphcast:glyphs", sequence.serialize());
        // If the sequence has an associated spell, write the spell ID for faster lookups
        sequence.findSpell().ifPresent(spell ->
                tag.putString("cachedSpellId", Glyphcast.SPELLS.getKey(spell).get().location().toString()));
        stack.getOrCreateTag().put("glyphcast:glyphs", tag);
    }

    /**
     * Gets a spell from an item stack
     *
     * @param stack the item stack to get the spell from
     * @return an optional holding the spell if it could be read
     */
    default Optional<Spell> getSpell(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag().getCompound("glyphcast:glyphs");
        if (tag.isEmpty())
            return Optional.empty();
        if (tag.contains("cachedSpellId")) {
            return Optional.ofNullable(Glyphcast.SPELLS.get(new ResourceLocation(tag.getString("cachedSpellId"))));
        }
        // The item doesn't have a cached spell id, so we fall back to checking the glyph sequence instead.
        GlyphSequence sequence = GlyphSequence.fromTag(tag);
        Optional<Spell> spell = sequence.findSpell();
        // Clearly something has changed and the sequence is now valid,
        // so we cache the spell id
        spell.ifPresent(sequenceSpell -> {
            tag.putString("cachedSpellId", Glyphcast.SPELLS.getKey(sequenceSpell).get().toString());
            stack.getOrCreateTag().put("glyphcast:glyphs", tag);
        });
        return spell;
    }

    default GlyphSequence getSequence(ItemStack stack) {
        if (!stack.getOrCreateTag().contains("glyphcast:glyphs"))
            return null;
        return GlyphSequence.fromTag(stack.getOrCreateTag().getCompound("glyphcast:glyphs"));
    }
}
