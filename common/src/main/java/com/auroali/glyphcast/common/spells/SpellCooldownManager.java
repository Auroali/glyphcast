package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.Glyphcast;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.IdentityHashMap;

public class SpellCooldownManager {
    private final IdentityHashMap<Spell, Long> cooldownMap = new IdentityHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean markedDirty = true;
    public SpellCooldownManager() {

    }

    public void addCooldown(Spell spell, long ticks) {
        cooldownMap.put(spell, ticks);
    }

    public boolean isOnCooldown(Spell spell) {
        return cooldownMap.getOrDefault(spell, 0L) != 0;
    }

    public long getRemainingCooldownTicks(Spell spell) {
        return cooldownMap.getOrDefault(spell, 0L);
    }

    public void tickCooldowns() {
        cooldownMap.entrySet().removeIf(spellLongEntry -> {
            this.markDirty();
            spellLongEntry.setValue(spellLongEntry.getValue() - 1);
            return spellLongEntry.getValue() <= 0;
        });
    }

    public boolean markedDirty() {
        return this.markedDirty;
    }

    public void markDirty() {
        this.markedDirty = true;
    }

    public void markClean() {
        this.markedDirty = false;
    }

    public CompoundTag serialize() {
        ListTag cooldowns = new ListTag();

        cooldownMap.forEach((spell, ticks) -> {
            CompoundTag serializedEntry = new CompoundTag();
            serializedEntry.putString("id", Glyphcast.SPELLS.getId(spell).toString());
            serializedEntry.putLong("ticks", ticks);
            cooldowns.add(serializedEntry);
        });

        CompoundTag tag = new CompoundTag();
        tag.put("Cooldowns", cooldowns);
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        cooldownMap.clear();
        ListTag cooldowns = tag.getList("Cooldowns", Tag.TAG_COMPOUND);
        for(int i = 0; i < cooldowns.size(); i++) {
            CompoundTag entry = cooldowns.getCompound(i);
            ResourceLocation location = ResourceLocation.tryParse(entry.getString("id"));
            if(!Glyphcast.SPELLS.contains(location)) {
                LOGGER.warn("Spell '{}' is not present in registry!", location);
                continue;
            }
            Spell spell = Glyphcast.SPELLS.get(location);
            long ticks = entry.getLong("ticks");
            cooldownMap.put(spell, ticks);
        }
    }

    public static class Immutable extends SpellCooldownManager {
        final SpellCooldownManager manager;
        public Immutable(SpellCooldownManager manager) {
            super();
            this.manager = manager;
        }

        @Override
        public void addCooldown(Spell spell, long ticks) {}

        @Override
        public boolean isOnCooldown(Spell spell) {
            if(manager == null)
                return false;
            return manager.isOnCooldown(spell);
        }

        @Override
        public long getRemainingCooldownTicks(Spell spell) {
            if(manager == null)
                return 0L;
            return manager.getRemainingCooldownTicks(spell);
        }

        @Override
        public void tickCooldowns() {}

        @Override
        public CompoundTag serialize() { return new CompoundTag(); }

        @Override
        public void deserialize(CompoundTag tag) {}
    }
}
