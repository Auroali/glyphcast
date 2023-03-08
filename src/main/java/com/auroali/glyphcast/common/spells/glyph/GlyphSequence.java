package com.auroali.glyphcast.common.spells.glyph;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.Spell;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.*;

/**
 * A sequence of glyphs that may represent a spell.
 * This automatically sorts all glyphs other than the base glyph,
 * so (assuming the first is the base glyph) <code>[FIRE, ICE, ICE, EARTH, FIRE]</code>
 * will become <code>[FIRE, FIRE, ICE, ICE, EARTH]</code>. This is to make matching with
 * spells easier, as the player only has to get the amount of each glyph right, not
 * the order.
 *
 * @see com.auroali.glyphcast.common.spells.glyph.Glyph
 * @author Auroali
 */
public class GlyphSequence {

    private final List<Glyph> glyphList;

    /**
     * Creates a new GlyphSequence
     * @param base the base glyph of the sequence, will be at index 0
     * @param glyphs the extra glyphs in the sequence, will be sorted
     */
    public GlyphSequence(Glyph base, Glyph... glyphs) {
        glyphList = new ArrayList<>();
        glyphList.addAll(Arrays.stream(glyphs).sorted().toList());
        glyphList.add(0, base);
    }

    public GlyphSequence(Glyph base, List<Glyph> glyphs) {
        glyphList = glyphs;
        glyphList.add(0, base);
    }

    /**
     * Creates a glyph sequence from a list of glyphs, assumes index 0 is the base glyph
     * @param glyphs the list of glyphs
     */
    public GlyphSequence(List<Glyph> glyphs) {
        this(glyphs.remove(0), new ArrayList<>(glyphs.stream().sorted().toList()));
    }

    /**
     * Finds a spell in the registry matching this sequence
     * @return an optional containing the spell, if it exists
     */
    public Optional<Spell> findSpell() {
        return GlyphCast.SPELL_REGISTRY.get().getValues().stream()
                .filter(spell -> spell.isSequence(this))
                .findAny();
    }

    /**
     * Returns a list containing all the glyphs in the sequence
     * @return an immutable list holding the glyphs
     */
    public List<Glyph> asList() {
        return Collections.unmodifiableList(glyphList);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(glyphList.size());
        glyphList.stream().map(Enum::ordinal).forEach(buf::writeByte);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("base", (byte) glyphList.get(0).ordinal());
        ListTag list = new ListTag();
        glyphList.stream().skip(1).forEach(glyph -> list.add(ByteTag.valueOf((byte) glyph.ordinal())));
        tag.put("glyphs", list);
        return tag;
    }

    /**
     * Creates a new GlyphSequence from the provided buffer
     * @param buf the buffer to create the sequence from
     * @return the resulting sequence
     */
    public static GlyphSequence fromNetwork(FriendlyByteBuf buf) {
        int len = buf.readInt();
        List<Glyph> glyphs = new ArrayList<>();
        for(int i = 0; i < len; i++) {
            glyphs.add(Glyph.values()[buf.readByte()]);
        }
        return new GlyphSequence(glyphs);
    }

    /**
     * Creates a new GlyphSequence from a CompoundTag
     * @param tag the tag to create the sequence from
     * @return the resulting sequence
     */
    public static GlyphSequence fromTag(CompoundTag tag) {
        // We store the base and extra glyphs seperately,
        // so that ordering is preserved
        int baseGlyph = tag.getByte("base");
        ListTag list = tag.getList("glyphs", Tag.TAG_BYTE);

        Glyph base = Glyph.values()[baseGlyph];
        List<Glyph> glyphs = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {
            glyphs.add(Glyph.values()[list.getInt(i)]);
        }

        return new GlyphSequence(base, glyphs);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GlyphSequence sequence)
            return sequence.glyphList.equals(glyphList);
        return super.equals(obj);
    }
}
