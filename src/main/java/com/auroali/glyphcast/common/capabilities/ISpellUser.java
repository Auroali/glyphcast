package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellSlot;
import com.auroali.glyphcast.common.spells.TickingSpell;
import com.auroali.glyphcast.common.spells.TickingSpellData;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public interface ISpellUser extends INBTSerializable<CompoundTag> {
    /**
     * Checks whether this spell user has discovered a spell
     * @param spell the spell to check
     * @return whether the spell has been discovered
     */
    boolean hasDiscoveredSpell(Spell spell);

    /**
     * Checks whether this spell user has discovered a glyph
     * @param glyph the glyph to check
     * @return whether the glyph has been discovered
     */
    boolean hasDiscoveredGlyph(Glyph glyph);

    /**
     * Marks a spell as discovered
     * @param spell the spell to mark as discovered
     */
    void markSpellDiscovered(Spell spell);

    /**
     * Mark a glyph as discovered
     * @param glyph the glyph to mark as discovered
     */
    void markGlyphDiscovered(Glyph glyph);

    /**
     * Gets the list of spells this spell user has discovered
     * @return an unmodifiable list containing all discovered spells
     */
    List<Spell> getDiscoveredSpells();

    /**
     * Gets the spell from the currently selected spell slot
     * @return the spell, or null if the slot is empty
     */
    @Nullable Spell getSelectedSpell();

    /**
     * Changes the active spell slot
     * @param slot the slot to select
     */
    void selectSpellSlot(int slot);

    /**
     * Sets the spell for a slot
     * @param slot the slot index to set
     * @param spell the spell to set the slot to
     */
    void setSpellForSlot(int slot, @Nullable Spell spell);

    /**
     * Gets all spell slots
     * @return an unmodifiable list containing all spell slots
     */
    List<SpellSlot> getSlots();

    /**
     * Adds a ticking spell
     * @param spell the spell to add
     * @param tag the spell's data
     */
    void addTickingSpell(TickingSpell spell, CompoundTag tag);

    /**
     * Gets all active ticking spells data
     * @return a list containing all the active spell data
     */
    List<TickingSpellData> getTickingSpells();

    /**
     * Clones this capability to another
     * @param other a lazy optional containing the other capability
     */
    default void cloneTo(LazyOptional<ISpellUser> other) {
        other.ifPresent(otherCap -> otherCap.deserializeNBT(this.serializeNBT()));
    }
}
