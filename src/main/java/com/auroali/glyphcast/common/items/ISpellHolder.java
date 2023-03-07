package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

public interface ISpellHolder {
    default void writeSequence(ItemStack stack, GlyphSequence sequence) {
        CompoundTag tag = sequence.serialize();
        stack.getOrCreateTag().put("glyphcast:glyphs", sequence.serialize());
        // If the sequence has an associated spell, write the spell ID for faster lookups
        sequence.findSpell().ifPresent(spell -> tag.putString("cachedSpellId", Objects.requireNonNull(GlyphCast.SPELL_REGISTRY.get().getKey(spell)).toString()));
        stack.getOrCreateTag().put("glyphcast:glyphs", tag);
    }

    default Optional<Spell> getSpell(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag().getCompound("glyphcast:glyphs");
        if(tag.contains("cachedSpellId")) {
            return Optional.ofNullable(GlyphCast.SPELL_REGISTRY.get().getValue(new ResourceLocation(tag.getString("cachedSpellId"))));
        }
        GlyphSequence sequence = GlyphSequence.fromTag(tag);
        Optional<Spell> spell = sequence.findSpell();
        // Clearly something has changed and the sequence is now valid,
        // so we cache the spell id
        spell.ifPresent(sequenceSpell -> {
            tag.putString("cachedSpellId", Objects.requireNonNull(GlyphCast.SPELL_REGISTRY.get().getKey(sequenceSpell)).toString());
            stack.getOrCreateTag().put("glyphcast:glyphs", tag);
        });
        return spell;
    }
}
