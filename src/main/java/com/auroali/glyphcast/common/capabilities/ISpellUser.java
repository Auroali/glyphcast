package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public interface ISpellUser extends INBTSerializable<CompoundTag> {
    boolean hasDiscoveredSpell(Spell spell);
    void markSpellDiscovered(Spell spell);
    Spell getSelectedSpell();

    void selectSpellSlot(int slot);
    void setSpellForSlot(int slot, Spell spell);

    List<SpellSlot> getSlots();
    default void cloneTo(LazyOptional<ISpellUser> other) {
        other.ifPresent(otherCap -> otherCap.deserializeNBT(this.serializeNBT()));
    }
}
