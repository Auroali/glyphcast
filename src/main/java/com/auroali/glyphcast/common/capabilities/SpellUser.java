package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.network.client.SyncSpellUserDataMessage;
import com.auroali.glyphcast.common.registry.GCCapabilities;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.registry.GCSpells;
import com.auroali.glyphcast.common.spells.*;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpellUser implements ISpellUser {
    int glyphMask;
    List<SpellSlot> slots;
    List<TickingSpellData> tickingSpells;
    int selectedSlot;

    // The player this capability is attached to
    final Player player;

    public static LazyOptional<ISpellUser> get(@Nullable Player player) {
        if(player == null)
            return LazyOptional.empty();
        return player.getCapability(GCCapabilities.SPELL_USER);
    }

    public SpellUser(Player player) {
        this.slots = SpellSlot.makeSlots(18);
        this.tickingSpells = new ArrayList<>();
        this.player = player;
        this.populateDefaultSpells();
    }

    void populateDefaultSpells() {
        slots.set(9, new SpellSlot(9, GCSpells.WAND_ATTACK.get()));
    }


    @Override
    public boolean hasDiscoveredGlyph(Glyph glyph) {
        return (glyphMask >> glyph.ordinal() & 1) != 0;
    }

    @Override
    public void markGlyphDiscovered(Glyph glyph) {
        glyphMask |= 0x01 << glyph.ordinal();
        sync();
    }

    @Override
    public Spell getSelectedSpell() {
        return slots.get(selectedSlot).getSpell();
    }

    @Override
    public void selectSpellSlot(int slot) {
        if(slot >= slots.size()) {
            GlyphCast.LOGGER.error("Attempted to select spell slot index {} when size is {}", slot, slots.size());
            return;
        }
        selectedSlot = slot;
    }

    @Override
    public void setSpellForSlot(int slot, Spell spell) {
        if(slot >= slots.size()) {
            GlyphCast.LOGGER.error("Attempted to select spell slot index {} when size is {}", slot, slots.size());
            return;
        }

        slots.set(slot, new SpellSlot(slot, spell));
        sync();
    }

    @Override
    public List<SpellSlot> getManuallyAssignedSlots() {
        return Collections.unmodifiableList(slots.subList(0,9));
    }

    @Override
    public List<SpellSlot> getDefaultSlots() {
        return Collections.unmodifiableList(slots.subList(9, 18));
    }

    @Override
    public void addTickingSpell(TickingSpell spell, SpellStats stats, CompoundTag tag) {
        tickingSpells.add(new TickingSpellData(spell, stats, tag));
    }

    @Override
    public List<TickingSpellData> getTickingSpells() {
        return tickingSpells;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag spellSlotsTag = new ListTag();
        for(int i = 0; i < 9; i++) {
            SpellSlot slot = slots.get(i);
            if(slot.isEmpty()) {
                spellSlotsTag.add(StringTag.valueOf("empty"));
                continue;
            }

            ResourceLocation id = GlyphCast.SPELL_REGISTRY.get().getKey(slot.getSpell());
            if(id == null) {
                spellSlotsTag.add(StringTag.valueOf("empty"));
                continue;
            }

            spellSlotsTag.add(StringTag.valueOf(id.toString()));
        }
        tag.putInt("SelectedSlot", selectedSlot);
        tag.put("SpellSlots", spellSlotsTag);
        tag.putInt("DiscoveredGlyphs", glyphMask);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag spellSlotsList = nbt.getList("SpellSlots", Tag.TAG_STRING);

        for(int i = 0; i < spellSlotsList.size(); i++) {
            if(spellSlotsList.getString(i).equals("empty")) {
                slots.set(i, new SpellSlot(i));
                continue;
            }

            ResourceLocation id = new ResourceLocation(spellSlotsList.getString(i));
            Spell spell = GlyphCast.SPELL_REGISTRY.get().getValue(id);
            if(spell == null) {
                GlyphCast.LOGGER.error("Invalid spell id {} encountered while loading spell slots", id);
                slots.set(i, new SpellSlot(i));
                continue;
            }
            slots.set(i, new SpellSlot(i, spell));
        }

        selectedSlot = nbt.getInt("SelectedSlot");
        glyphMask = nbt.getInt("DiscoveredGlyphs");
    }

    // Syncs current spell user data to the client
    void sync() {
        // If we aren't on the client, don't do anything
        if(player instanceof ServerPlayer serverPlayer)
            GCNetwork.sendToClient(serverPlayer, new SyncSpellUserDataMessage(this));
    }
}
