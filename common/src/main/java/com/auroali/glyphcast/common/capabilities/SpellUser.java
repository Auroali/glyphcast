package com.auroali.glyphcast.common.capabilities;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.entities.FloatingLight;
import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.items.equipment.IMaxEnergyModifier;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.both.SetQuickSelectSlotMessage;
import com.auroali.glyphcast.common.network.client.SyncSpellUserDataMessage;
import com.auroali.glyphcast.common.network.client.SyncSpellUserEnergyMessage;
import com.auroali.glyphcast.common.network.server.QuickSelectSlotMessage;
import com.auroali.glyphcast.common.registry.GCSpells;
import com.auroali.glyphcast.common.spells.*;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.mojang.logging.LogUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.*;

public class SpellUser implements ISpellUser {
    public static final Logger LOGGER = LogUtils.getLogger();
    final List<SpellSlot> slots;
    final List<TickingSpellData> tickingSpells;
    final List<FloatingLight> floatingLights;
    // The player this capability is attached to
    final Player player;
    final SpellCooldownManager cooldownManager;
    int glyphMask;
    int selectedSlot;
    double energy;
    boolean canSync = true;

    public SpellUser(Player player) {
        this.slots = SpellSlot.makeSlots(18);
        this.tickingSpells = new ArrayList<>();
        this.player = player;
        this.cooldownManager = new SpellCooldownManager();
        this.floatingLights = new ArrayList<>();
        this.populateDefaultSpells();
    }

    @ExpectPlatform
    public static Optional<ISpellUser> get(Player player) {
        throw new AssertionError();
    }

    public static SpellCooldownManager getCooldownManager(Player player) {
        return get(player).map(ISpellUser::getCooldownManager).orElse(new SpellCooldownManager.Immutable(null));
    }

    void populateDefaultSpells() {
        slots.set(9, new SpellSlot(9, GCSpells.WAND_INFUSE.get()));
        slots.set(10, new SpellSlot(10, GCSpells.WAND_ATTACK.get()));
        slots.set(11, new SpellSlot(11, GCSpells.WAND_EXTRACT.get()));
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
        if (slot >= slots.size()) {
            LOGGER.error("Attempted to select spell slot index {} when size is {}", slot, slots.size());
            return;
        }
        selectedSlot = slot;
        sync();
    }

    @Override
    public void setSpellForSlot(int slot, Spell spell) {
        if (slot >= slots.size()) {
            LOGGER.error("Attempted to select spell slot index {} when size is {}", slot, slots.size());
            return;
        }

        slots.set(slot, new SpellSlot(slot, spell));
        sync();
    }

    @Override
    public List<SpellSlot> getManuallyAssignedSlots() {
        return Collections.unmodifiableList(slots.subList(0, 9));
    }

    @Override
    public List<SpellSlot> getDefaultSlots() {
        return Collections.unmodifiableList(slots.subList(9, 18));
    }

    @Override
    public void addTickingSpell(TickingSpell spell, InteractionHand hand, CompoundTag tag) {
        tickingSpells.add(new TickingSpellData(spell, hand, tag));
    }

    @Override
    public List<TickingSpellData> getTickingSpells() {
        return tickingSpells;
    }

    @Override
    public boolean canOpenSpellWheel() {
        return PlayerHelper.hasItemInHand(player, IWandLike.class);
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(double amount) {
        energy = Math.max(0, Math.min(amount, getMaxEnergy()));
        syncEnergy();
    }

    @Override
    public double getMaxEnergy() {
        List<ItemStack> energyMod = new ArrayList<>();
        PlayerHelper.getAllEquipment(player)
                .stream()
                .filter(i -> i.getItem() instanceof IMaxEnergyModifier)
                .forEach(energyMod::add);

        // this is so cursed
        final double[] finalMaxEnergy = {250};

        energyMod.stream()
                .sorted(Comparator.comparing(e -> ((IMaxEnergyModifier) e.getItem()).getEnergyModType()))
                .forEach(i -> {
                    switch (((IMaxEnergyModifier) i.getItem()).getEnergyModType()) {
                        case ADDITION -> finalMaxEnergy[0] += (((IMaxEnergyModifier) i.getItem()).getEnergyMod(i));
                        case MULTIPLIER -> finalMaxEnergy[0] *= (((IMaxEnergyModifier) i.getItem()).getEnergyMod(i));
                    }
                });

        return finalMaxEnergy[0];
    }

    @Override
    public double drainEnergy(double amount, boolean simulate) {
        if (energy - amount < 0)
            amount = energy;

        if (!simulate) {
            energy -= amount;
            syncEnergy();
        }
        return amount;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag spellSlotsTag = new ListTag();
        for (int i = 0; i < 9; i++) {
            SpellSlot slot = slots.get(i);
            if (slot.isEmpty()) {
                spellSlotsTag.add(StringTag.valueOf("empty"));
                continue;
            }

            ResourceLocation id = Glyphcast.SPELLS.getId(slot.getSpell());
            if (id == null) {
                spellSlotsTag.add(StringTag.valueOf("empty"));
                continue;
            }

            spellSlotsTag.add(StringTag.valueOf(id.toString()));
        }

        ListTag quickSelect = new ListTag();
        slots.forEach(slot -> {
            if(slot.getQuickSelectId() < 0)
                return;
            CompoundTag quickSelectSlot = new CompoundTag();
            quickSelectSlot.putInt("Id", slot.getQuickSelectId());
            quickSelectSlot.putInt("Slot", slot.getIndex());
            quickSelect.add(quickSelectSlot);
        });

        ListTag lights = new ListTag();
        for(FloatingLight light : floatingLights) {
            CompoundTag lightData = new CompoundTag();
            lightData.putDouble("PosX", light.getX());
            lightData.putDouble("PosY", light.getY());
            lightData.putDouble("PosZ", light.getZ());
            lights.add(lightData);
            light.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
        floatingLights.clear();

        tag.putInt("SelectedSlot", selectedSlot);
        tag.put("SpellSlots", spellSlotsTag);
        tag.put("QuickSelect", quickSelect);
        tag.putInt("DiscoveredGlyphs", glyphMask);
        tag.putDouble("Energy", energy);
        tag.put("CooldownManager", cooldownManager.serialize());
        tag.put("FloatingLights", lights);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        canSync = false;
        ListTag spellSlotsList = nbt.getList("SpellSlots", Tag.TAG_STRING);

        for (int i = 0; i < spellSlotsList.size(); i++) {
            if (spellSlotsList.getString(i).equals("empty")) {
                slots.set(i, new SpellSlot(i));
                continue;
            }

            ResourceLocation id = new ResourceLocation(spellSlotsList.getString(i));
            Spell spell = Glyphcast.SPELLS.get(id);
            if (spell == null) {
                LOGGER.error("Invalid spell id {} encountered while loading spell slots", id);
                slots.set(i, new SpellSlot(i));
                continue;
            }
            slots.set(i, new SpellSlot(i, spell));
        }

        ListTag quickSelect = nbt.getList("QuickSelect", Tag.TAG_COMPOUND);
        for(int i = 0; i < quickSelect.size(); i++) {
            CompoundTag quickSelectSlot = quickSelect.getCompound(i);
            int quickSelectId = quickSelectSlot.getInt("Id");
            int slotIndex = quickSelectSlot.getInt("Slot");
            setQuickSelectForSlot(slotIndex, quickSelectId);
        }

        ListTag lights = nbt.getList("FloatingLights", Tag.TAG_COMPOUND);
        for(int i = 0; i < lights.size(); i++) {
            FloatingLight light = new FloatingLight(player.level,
                    lights.getCompound(i).getDouble("PosX"),
                    lights.getCompound(i).getDouble("PosY"),
                    lights.getCompound(i).getDouble("PosZ")
            );
            light.setOwner(player);
            floatingLights.add(light);
        }
        selectedSlot = nbt.getInt("SelectedSlot");
        glyphMask = nbt.getInt("DiscoveredGlyphs");
        energy = nbt.getDouble("Energy");
        cooldownManager.deserialize(nbt.getCompound("CooldownManager"));
        canSync = true;
    }

    // Syncs current spell user data to the client
    @Override
    public void sync() {
        // If we aren't on the client, don't do anything
        if (canSync && player instanceof ServerPlayer serverPlayer)
            GCNetwork.CHANNEL.sendToPlayer(serverPlayer, new SyncSpellUserDataMessage(this));
    }

    @Override
    public SpellCooldownManager getCooldownManager() {
        return player.level.isClientSide ? new SpellCooldownManager.Immutable(cooldownManager) : cooldownManager;
    }

    @Override
    public void setQuickSelectForSlot(int slot, int quickSelect) {
        if(slot >= slots.size()) {
            LOGGER.warn("Tried to set quick select for slot {}! (Max is {})", slot, slots.size() - 1);
            return;
        }
        slots.forEach(s -> {
            if(quickSelect >= 0 && s.getQuickSelectId() == quickSelect)
                s.setQuickSelect(-1);
        });
        slots.get(slot).setQuickSelect(quickSelect);
        if(canSync && player instanceof ServerPlayer p)
            GCNetwork.CHANNEL.sendToPlayer(p, new SetQuickSelectSlotMessage.S2C(slot, quickSelect));
    }

    @Override
    public void quickSelectSlot(int quickSelect) {
        if(player.level.isClientSide) {
            GCNetwork.CHANNEL.sendToServer(new QuickSelectSlotMessage(quickSelect));
            return;
        }
        slots.forEach(slot -> {
            if(slot.getQuickSelectId() == quickSelect)
                selectSpellSlot(slot.getIndex());
        });
    }

    @Override
    public void loadCooldownManagerData(CompoundTag tag) {
        cooldownManager.deserialize(tag);
    }

    @Override
    public void loadFloatingLights() {
        floatingLights.forEach(player.level::addFreshEntity);
        floatingLights.clear();
    }

    @Override
    public void saveFloatingLights() {
        floatingLights.clear();
        floatingLights.addAll(FloatingLight.getAllFollowing(player));
    }

    void syncEnergy() {
        // If we aren't on the client, don't do anything
        if (canSync && player instanceof ServerPlayer serverPlayer)
            GCNetwork.CHANNEL.sendToPlayer(serverPlayer, new SyncSpellUserEnergyMessage(this));
    }
}
