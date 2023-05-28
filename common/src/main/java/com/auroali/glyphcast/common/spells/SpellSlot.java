package com.auroali.glyphcast.common.spells;

import java.util.ArrayList;

public class SpellSlot {
    private final Spell spell;
    private final int index;

    private int quickSelectId = -1;

    public SpellSlot(int index, Spell spell) {
        this.spell = spell;
        this.index = index;
    }

    public SpellSlot(int index) {
        this.spell = null;
        this.index = index;
    }

    public void setQuickSelect(int id) {
        this.quickSelectId = Math.min(id, 2);
    }

    public int getQuickSelectId() {
        return quickSelectId;
    }

    public static ArrayList<SpellSlot> makeSlots(int size) {
        ArrayList<SpellSlot> slots = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            slots.add(new SpellSlot(i));
        }
        return slots;
    }

    public boolean isEmpty() {
        return spell == null;
    }

    public Spell getSpell() {
        return spell;
    }

    public int getIndex() {
        return index;
    }
}
