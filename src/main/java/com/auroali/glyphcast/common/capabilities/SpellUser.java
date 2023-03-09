package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.registry.GCCapabilities;
import com.auroali.glyphcast.common.registry.GCSpells;
import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpellUser implements ISpellUser {
    List<Spell> discoveredSpells;
    Spell selected = GCSpells.FIRE_SPELL.get();

    public static LazyOptional<ISpellUser> get(@Nullable Player player) {
        if(player == null)
            return LazyOptional.empty();
        return player.getCapability(GCCapabilities.SPELL_USER);
    }

    public SpellUser() {
        this.discoveredSpells = new ArrayList<>();
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
    public void selectSpell(Spell spell) {
        this.selected = spell;
    }

    @Override
    public Spell getSelectedSpell() {
        return selected;
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
        tag.put("DiscoveredSpells", discoveredSpellsTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag discoveredSpellsList = nbt.getList("DiscoveredSpells", Tag.TAG_STRING);
        for(int i = 0; i < discoveredSpellsList.size(); i++) {
            ResourceLocation id = new ResourceLocation(discoveredSpellsList.getString(i));
            Spell spell = GlyphCast.SPELL_REGISTRY.get().getValue(id);
            if(spell != null)
                discoveredSpells.add(spell);
        }
    }
}
