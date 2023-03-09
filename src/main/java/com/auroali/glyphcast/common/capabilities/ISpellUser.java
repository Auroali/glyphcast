package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public interface ISpellUser extends INBTSerializable<CompoundTag> {
    boolean hasDiscoveredSpell(Spell spell);
    void markSpellDiscovered(Spell spell);
    void selectSpell(Spell spell);
    Spell getSelectedSpell();

    default void cloneTo(LazyOptional<ISpellUser> other) {
        other.ifPresent(otherCap -> otherCap.deserializeNBT(this.serializeNBT()));
    }
}
