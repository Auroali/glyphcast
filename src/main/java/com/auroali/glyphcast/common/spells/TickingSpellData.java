package com.auroali.glyphcast.common.spells;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;

public class TickingSpellData {
    private final TickingSpell spell;
    private final CompoundTag tag;
    private final SpellStats stats;
    private final InteractionHand hand;
    private int ticks;

    public TickingSpellData(TickingSpell spell, InteractionHand hand, SpellStats stats, CompoundTag tag) {
        this.spell = spell;
        this.hand = hand;
        this.ticks = 0;
        this.stats = stats;
        this.tag = tag;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public TickingSpell getSpell() {
        return spell;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public SpellStats getStats() {
        return stats;
    }
}
