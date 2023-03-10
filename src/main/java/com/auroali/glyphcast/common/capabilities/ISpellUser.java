package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellSlot;
import com.auroali.glyphcast.common.spells.TickingSpell;
import com.auroali.glyphcast.common.spells.TickingSpellData;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public interface ISpellUser extends INBTSerializable<CompoundTag> {
    boolean hasDiscoveredSpell(Spell spell);
    boolean hasDiscoveredGlyph(Glyph glyph);
    void markSpellDiscovered(Spell spell);
    void markGlyphDiscovered(Glyph glyph);
    List<Spell> getDiscoveredSpells();

    Spell getSelectedSpell();
    void selectSpellSlot(int slot);
    void setSpellForSlot(int slot, Spell spell);

    List<SpellSlot> getSlots();

    void addTickingSpell(TickingSpell spell, CompoundTag tag);
    List<TickingSpellData> getTickingSpells();
    default void cloneTo(LazyOptional<ISpellUser> other) {
        other.ifPresent(otherCap -> otherCap.deserializeNBT(this.serializeNBT()));
    }
}
