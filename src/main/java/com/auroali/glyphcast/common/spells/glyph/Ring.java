package com.auroali.glyphcast.common.spells.glyph;

import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Ring {
    final List<Glyph> glyphs;
    private Ring(List<Glyph> glyphs) {
        this.glyphs = glyphs;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(glyphs.size());
        glyphs.forEach(glyph -> buf.writeInt(glyph.ordinal()));
    }

    public List<Glyph> asList() {
        return glyphs;
    }

    public static Ring of(Glyph... glyphs) {
        return new Ring(Arrays.stream(glyphs).sorted().toList());
    }

    public static Ring of(List<Glyph> glyphs) {
        return new Ring(glyphs.stream().sorted().toList());
    }

    public static Ring decode(FriendlyByteBuf buf) {
        List<Glyph> glyphs = new ArrayList<>();
        int len = buf.readInt();
        for(int i = 0; i < len; i++) {
            glyphs.add(Glyph.values()[buf.readInt()]);
        }
        return Ring.of(glyphs);
    }

    public static List<Glyph> mergeRings(Ring... rings) {
        ArrayList<Glyph> list = new ArrayList<>();
        Arrays.stream(rings).forEach(ring -> list.addAll(ring.glyphs));
        return Collections.unmodifiableList(list);
    }

    public static List<Glyph> mergeRings(List<Ring> rings) {
        ArrayList<Glyph> list = new ArrayList<>();
        rings.forEach(ring -> list.addAll(ring.glyphs));
        return Collections.unmodifiableList(list);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Ring ring && ring.glyphs.equals(this.glyphs));
    }
}
