package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.client.screen.ScribingTableScreen;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.both.SetQuickSelectSlotMessage;
import com.auroali.glyphcast.common.network.client.menu.SendScribingGlyphListMessage;
import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ClientPacketHandler {
    public static void spawnParticles(SpawnParticlesMessage msg) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;
        RandomSource rand = level.getRandom();
        for (int i = 0; i < msg.count; i++) {
            double spreadX = rand.nextGaussian() * msg.spread;
            double spreadY = rand.nextGaussian() * msg.spread;
            double spreadZ = rand.nextGaussian() * msg.spread;
            double speed = Math.max(msg.minSpeed, msg.maxSpeed * rand.nextFloat());
            Vec3 newDir = msg.direction.normalize().add(spreadX, spreadY, spreadZ).normalize().scale(speed);
            level.addParticle(msg.particle, msg.pos.x, msg.pos.y, msg.pos.z, newDir.x, newDir.y, newDir.z);
        }
    }

    public static void syncSpellUserData(CompoundTag tag) {
        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> user.deserializeNBT(tag));
    }

    public static void syncSpellUserEnergy(double energy) {
        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> user.setEnergy(energy));
    }

    public static void syncCooldownManager(CompoundTag data) {
        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> user.loadCooldownManagerData(data));
    }

    public static void triggerSpellEvent(Byte id, Spell spell, Spell.IContext ctx) {
        if (ctx instanceof Spell.PositionedContext posCtx)
            spell.handleEvent(id, posCtx);
    }

    public static void setQuickSelectForSlot(SetQuickSelectSlotMessage msg) {
        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> user.setQuickSelectForSlot(msg.slot, msg.quickSelect));
    }

    public static Entity fromId(int id) {
        if (Minecraft.getInstance().level == null)
            return null;
        return Minecraft.getInstance().level.getEntity(id);
    }

    public static void sendScribingGlyphList(SendScribingGlyphListMessage msg) {
        Player player = Minecraft.getInstance().player;
        if(Minecraft.getInstance().screen instanceof ScribingTableScreen screen && screen.getMenu().containerId == msg.containerId) {
            screen.setGlyphs(msg.glyphs);
        }
    }
}
