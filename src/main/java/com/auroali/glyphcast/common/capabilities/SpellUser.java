package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.registry.GCCapabilities;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpellUser implements ISpellUser {
    List<Spell> discoveredSpells;
    List<SpellSlot> slots;
    int selectedSlot;

    public static LazyOptional<ISpellUser> get(@Nullable Player player) {
        if(player == null)
            return LazyOptional.empty();
        return player.getCapability(GCCapabilities.SPELL_USER);
    }

    public SpellUser() {
        this.discoveredSpells = new ArrayList<>();
        this.slots = SpellSlot.makeSlots(4);
    }

    @Override
    public boolean hasDiscoveredSpell(Spell spell) {
        return discoveredSpells.contains(spell);
    }

    @Override
    public void markSpellDiscovered(Spell spell) {
        if(!discoveredSpells.contains(spell))
            discoveredSpells.add(spell);
    }

    @Override
    public Spell getSelectedSpell() {
        return slots.get(selectedSlot).getSpell();
    }

    @Override
    public void selectSpellSlot(int slot) {
        if(slot >= slots.size()) {
            GlyphCast.LOGGER.error("Attempted to select spell slot index {} when max is {}", slot, slots.size() - 1);
            return;
        }
        selectedSlot = slot;
    }

    @Override
    public void setSpellForSlot(int slot, Spell spell) {
        if(slot >= slots.size()) {
            GlyphCast.LOGGER.error("Attempted to select spell slot index {} when max is {}", slot, slots.size() - 1);
            return;
        }

        slots.set(slot, new SpellSlot(slot, spell));
    }

    @Override
    public List<SpellSlot> getSlots() {
        return Collections.unmodifiableList(slots);
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
    }
}
