package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.common.spells.*;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;

import java.util.List;

public interface ISpellUser {
    /**
     * Checks whether this spell user has discovered a glyph
     *
     * @param glyph the glyph to check
     * @return whether the glyph has been discovered
     */
    boolean hasDiscoveredGlyph(Glyph glyph);

    /**
     * Mark a glyph as discovered
     *
     * @param glyph the glyph to mark as discovered
     */
    void markGlyphDiscovered(Glyph glyph);

    /**
     * Gets the spell from the currently selected spell slot
     *
     * @return the spell, or null if the slot is empty
     */
    Spell getSelectedSpell();

    /**
     * Changes the active spell slot
     *
     * @param slot the slot to select
     */
    void selectSpellSlot(int slot);

    /**
     * Sets the spell for a modifiable slot
     *
     * @param slot  the slot index to set
     * @param spell the spell to set the slot to
     */
    void setSpellForSlot(int slot, Spell spell);

    /**
     * Gets spell slots that the player can modify
     *
     * @return an unmodifiable list containing all modifiable spell slots
     */
    List<SpellSlot> getManuallyAssignedSlots();

    /**
     * Gets default spell slots
     *
     * @return an unmodifiable list containing all default spell slots
     */
    List<SpellSlot> getDefaultSlots();

    /**
     * Adds a ticking spell
     *
     * @param spell the spell to add
     * @param tag   the spell's data
     */
    void addTickingSpell(TickingSpell spell, InteractionHand hand, CompoundTag tag);

    /**
     * Gets all active ticking spells data
     *
     * @return a list containing all the active spell data
     */
    List<TickingSpellData> getTickingSpells();

    boolean canOpenSpellWheel();

    double getEnergy();

    void setEnergy(double amount);

    double getMaxEnergy();

    double drainEnergy(double amount, boolean simulate);

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);

    void sync();

    SpellCooldownManager getCooldownManager();

    void loadCooldownManagerData(CompoundTag tag);

    void loadFloatingLights();
    void saveFloatingLights();
    /**
     * Clones this capability to another
     *
     * @param other a lazy optional containing the other capability
     */
    default void cloneTo(ISpellUser other) {
        other.deserializeNBT(this.serializeNBT());
    }
}

