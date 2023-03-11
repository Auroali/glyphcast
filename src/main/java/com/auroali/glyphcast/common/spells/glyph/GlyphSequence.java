package com.auroali.glyphcast.common.spells.glyph;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A sequence of glyphs split into rings that represents a spell
 * @see com.auroali.glyphcast.common.spells.glyph.Ring
 * @see com.auroali.glyphcast.common.spells.glyph.Glyph
 * @author Auroali
 */
public class GlyphSequence {

    public static final GlyphSequence EMPTY = new GlyphSequence();
    private final List<Ring> glyphList;
    private Spell cachedSpell;

    private GlyphSequence() {
        this.glyphList = List.of();
        this.findSpell().ifPresent(s -> this.cachedSpell = s);
    }

    public GlyphSequence(List<Ring> ringList) {
        this.glyphList = ringList;
    }

    public GlyphSequence(Ring... rings) {
        this(Arrays.stream(rings).toList());
    }

    /**
     * Finds a spell in the registry matching this sequence
     * @return an optional containing the spell, if it exists
     */
    public Optional<Spell> findSpell() {
        if(cachedSpell != null)
            return Optional.of(cachedSpell);

        Optional<Spell> spellOpt = GlyphCast.SPELL_REGISTRY.get().getValues().stream()
                .filter(spell -> spell.isSequence(this))
                .findAny();
        spellOpt.ifPresent(s -> this.cachedSpell = s);
        return spellOpt;
    }

    public List<Ring> getRings() {
        return glyphList;
    }
    /**
     * Returns a list containing all the glyphs in the sequence
     * @return an immutable list holding the glyphs
     */
    public List<Glyph> asList() {
        return Ring.mergeRings(glyphList);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(glyphList.size());
        glyphList.forEach(ring -> ring.encode(buf));
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ListTag rings = new ListTag();
        glyphList.forEach(ring -> {
            ListTag glyphs = new ListTag();
            ring.glyphs.forEach(glyph -> glyphs.add(IntTag.valueOf(glyph.ordinal())));
            rings.add(glyphs);
        });
        tag.put("Rings", rings);
        return tag;
    }

    /**
     * Creates a new GlyphSequence from the provided buffer
     * @param buf the buffer to create the sequence from
     * @return the resulting sequence
     */
    public static GlyphSequence fromNetwork(FriendlyByteBuf buf) {
        int numRings = buf.readInt();
        List<Ring> rings = new ArrayList<>();
        for(int i = 0; i < numRings; i++) {
            rings.add(Ring.decode(buf));
        }
        return new GlyphSequence(rings);
    }

    /**
     * Creates a new GlyphSequence from a CompoundTag
     * @param tag the tag to create the sequence from
     * @return the resulting sequence
     */
    public static GlyphSequence fromTag(CompoundTag tag) {
        ListTag rings = tag.getList("Rings", Tag.TAG_LIST);
        List<Ring> ringsList = new ArrayList<>();
        // Read each ring
        for(int i = 0; i < rings.size(); i++) {
            ListTag glyphs = rings.getList(i);
            List<Glyph> ringSequence = new ArrayList<>();
            // Read all the glyphs in this ring
            for(int j = 0; j < glyphs.size(); j++) {
                ringSequence.add(Glyph.values()[glyphs.getInt(j)]);
            }
            ringsList.add(Ring.of(ringSequence));
        }
        return new GlyphSequence(ringsList);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GlyphSequence sequence)
            return sequence.glyphList.equals(glyphList);
        return super.equals(obj);
    }
}
