package com.auroali.glyphcast.common.spells;

import net.minecraft.nbt.CompoundTag;

public class TickingSpellData {
    private final TickingSpell spell;
    private int ticks;
    private final CompoundTag tag;
    public TickingSpellData(TickingSpell spell, CompoundTag tag) {
        this.spell = spell;
        this.ticks = 0;
        this.tag = tag;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public TickingSpell getSpell() {
        return spell;
    }
}
