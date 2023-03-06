package com.auroali.glyphcast.common.spells.glyph;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GlyphSequence {

    private final List<Glyph> glyphList;
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

    public Optional<Spell> findSpell() {
        return GlyphCast.SPELL_REGISTRY.get().getValues().stream()
                .filter(spell -> spell.isSequence(this))
                .findAny();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(glyphList.size());
        glyphList.stream().map(Enum::ordinal).forEach(buf::writeByte);
    }

    public static GlyphSequence fromNetwork(FriendlyByteBuf buf) {
        int len = buf.readInt();
        List<Glyph> glyphs = new ArrayList<>();
        for(int i = 0; i < len; i++) {
            glyphs.add(Glyph.values()[buf.readByte()]);
        }
        return new GlyphSequence(glyphs);
    }
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("base", (byte) glyphList.get(0).ordinal());
        ListTag list = new ListTag();
        glyphList.stream().skip(1).forEach(glyph -> list.add(ByteTag.valueOf((byte) glyph.ordinal())));
        tag.put("glyphs", list);
        return tag;
    }
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
