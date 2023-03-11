package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.network.client.SyncSpellUserDataMessage;
import com.auroali.glyphcast.common.registry.GCCapabilities;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellSlot;
import com.auroali.glyphcast.common.spells.TickingSpell;
import com.auroali.glyphcast.common.spells.TickingSpellData;
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
import java.util.*;

public class SpellUser implements ISpellUser {
    int glyphMask;
    Set<Spell> discoveredSpells;
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
        this.discoveredSpells = new HashSet<>();
        this.slots = SpellSlot.makeSlots(9);
        this.tickingSpells = new ArrayList<>();
        this.player = player;
    }

    @Override
    public boolean hasDiscoveredSpell(Spell spell) {
        return discoveredSpells.contains(spell);
    }

    @Override
    public boolean hasDiscoveredGlyph(Glyph glyph) {
        return (glyphMask >> glyph.ordinal() & 1) != 0;
    }

    @Override
    public void markSpellDiscovered(Spell spell) {
        if(!discoveredSpells.contains(spell)) {
            discoveredSpells.add(spell);
            sync();
        }
    }

    @Override
    public void markGlyphDiscovered(Glyph glyph) {
        glyphMask |= 0x01 << glyph.ordinal();
        sync();
    }

    @Override
    public List<Spell> getDiscoveredSpells() {
        return discoveredSpells.stream().toList();
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
    public List<SpellSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    @Override
    public void addTickingSpell(TickingSpell spell, CompoundTag tag) {
        tickingSpells.add(new TickingSpellData(spell, tag));
    }

    @Override
    public List<TickingSpellData> getTickingSpells() {
        return tickingSpells;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag discoveredSpellsTag = new ListTag();
        this.discoveredSpells.forEach(spell -> {
            ResourceLocation id = GlyphCast.SPELL_REGISTRY.get().getKey(spell);
            if(id == null)
                return;

            discoveredSpellsTag.add(StringTag.valueOf(id.toString()));
        });

        ListTag spellSlotsTag = new ListTag();
        this.slots.forEach(slot -> {
            if(slot.isEmpty()) {
                spellSlotsTag.add(StringTag.valueOf("empty"));
                return;
            }

            ResourceLocation id = GlyphCast.SPELL_REGISTRY.get().getKey(slot.getSpell());
            if(id == null) {
                spellSlotsTag.add(StringTag.valueOf("empty"));
                return;
            }

            spellSlotsTag.add(StringTag.valueOf(id.toString()));
        });
        tag.putInt("SelectedSlot", selectedSlot);
        tag.put("SpellSlots", spellSlotsTag);
        tag.put("DiscoveredSpells", discoveredSpellsTag);
        tag.putInt("DiscoveredGlyphs", glyphMask);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag discoveredSpellsList = nbt.getList("DiscoveredSpells", Tag.TAG_STRING);
        ListTag spellSlotsList = nbt.getList("SpellSlots", Tag.TAG_STRING);
        for(int i = 0; i < discoveredSpellsList.size(); i++) {
            ResourceLocation id = new ResourceLocation(discoveredSpellsList.getString(i));
            Spell spell = GlyphCast.SPELL_REGISTRY.get().getValue(id);
            if(spell != null)
                discoveredSpells.add(spell);
        }

        for(int i = 0; i < spellSlotsList.size(); i++) {
            if(spellSlotsList.getString(i).equals("empty")) {
                slots.set(i, new SpellSlot(i));
                continue;
            }

            ResourceLocation id = new ResourceLocation(spellSlotsList.getString(i));
            Spell spell = GlyphCast.SPELL_REGISTRY.get().getValue(id);
            if(spell == null) {
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
