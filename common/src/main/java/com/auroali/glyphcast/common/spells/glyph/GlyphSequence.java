package com.auroali.glyphcast.common.spells.glyph;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;

import java.util.*;

/**
 * A sequence of glyphs split into rings that represents a spell
 *
 * @author Auroali
 * @see Ring
 * @see Glyph
 */
public class GlyphSequence {

    public static final GlyphSequence EMPTY = new GlyphSequence();
    private final List<Ring> glyphList;
    private final List<Ring> visual;
    private Spell cachedSpell;

    private GlyphSequence() {
        this.glyphList = Collections.emptyList();
        this.visual = Collections.emptyList();
    }

    public GlyphSequence(List<Ring> ringList, List<Ring> display) {
        this.glyphList = ringList.stream().map(Ring::sorted).toList();
        this.visual = display;
        this.findSpell().ifPresent(s -> this.cachedSpell = s);
    }

    public GlyphSequence(Ring... rings) {
        this(Arrays.stream(rings).toList(), Arrays.stream(rings).toList());
    }

    /**
     * Creates a new GlyphSequence from the provided buffer
     *
     * @param buf the buffer to create the sequence from
     * @return the resulting sequence
     */
    public static GlyphSequence fromNetwork(FriendlyByteBuf buf) {
        int numRings = buf.readInt();
        List<Ring> rings = new ArrayList<>();
        for (int i = 0; i < numRings; i++) {
            rings.add(Ring.decode(buf));
        }
        return new GlyphSequence(rings, rings);
    }

    /**
     * Creates a new GlyphSequence from a CompoundTag
     *
     * @param tag the tag to create the sequence from
     * @return the resulting sequence
     */
    public static GlyphSequence fromTag(CompoundTag tag) {
        ListTag rings = tag.getList("Rings", Tag.TAG_BYTE_ARRAY);
        List<Ring> ringsList = new ArrayList<>();
        // Read each ring
        for (Tag ring : rings) {
            ByteArrayTag glyphs = (ByteArrayTag) ring;
            List<Glyph> ringSequence = new ArrayList<>();
            // Read all the glyphs in this ring
            for (ByteTag glyph : glyphs) {
                ringSequence.add(Glyph.values()[glyph.getAsInt()]);
            }
            ringsList.add(Ring.of(ringSequence));
        }
        return new GlyphSequence(ringsList, ringsList);
    }

    /**
     * Finds a spell in the registry matching this sequence
     *
     * @return an optional containing the spell, if it exists
     */
    public Optional<Spell> findSpell() {
        if (isEmpty())
            return Optional.empty();

        if (cachedSpell != null)
            return Optional.of(cachedSpell);

        Optional<Spell> spellOpt = Glyphcast.SPELLS.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(spell -> spell.isSequence(this))
                .findAny();
        spellOpt.ifPresent(s -> this.cachedSpell = s);
        return spellOpt;
    }

    public List<Ring> getRings() {
        return glyphList;
    }
    public List<Ring> getVisualRings() {
        return visual;
    }

    /**
     * Returns a list containing all the glyphs in the sequence
     *
     * @return an immutable list holding the glyphs
     */
    public List<Glyph> asList() {
        return Ring.mergeRings(glyphList);
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(glyphList.size());
        visual.forEach(ring -> ring.encode(buf));
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ListTag rings = new ListTag();
        visual.forEach(ring -> {
            ByteArrayTag bytes = new ByteArrayTag(ring.glyphs.stream().map(g -> (byte) g.ordinal()).toList());
            rings.add(bytes);
        });
        tag.put("Rings", rings);
        return tag;
    }

    public boolean isEmpty() {
        return this == EMPTY || glyphList.isEmpty() || glyphList.stream().allMatch(r -> r.glyphs.isEmpty());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GlyphSequence sequence)
            return sequence.glyphList.equals(glyphList);
        return super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GlyphSequence[");
        int i = 0;
        for(Ring ring : visual) {
            if(i != 0)
                builder.append(',');
            builder.append("%d: [".formatted(i));
            ring.glyphs.forEach(g -> {
                builder.append(g.toString());
                builder.append(';');
            });
            builder.append("]");
            i++;
        }
        builder.append("]");
        return builder.toString();
    }
}
