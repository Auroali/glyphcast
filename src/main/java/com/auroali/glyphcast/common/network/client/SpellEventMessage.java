package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellStats;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpellEventMessage extends NetworkMessage {
    final byte id;
    final Spell spell;
    final Spell.IContext ctx;
    public SpellEventMessage(Byte id, Spell spell, Spell.IContext ctx) {
        this.id = id;
        this.spell = spell;
        this.ctx = ctx;
    }

    public SpellEventMessage(FriendlyByteBuf buf) {
        this.id = buf.readByte();
        this.spell = buf.readRegistryIdSafe(Spell.class);
        this.ctx = readCtx(buf);
    }

    public Spell.IContext readCtx(FriendlyByteBuf buf) {
        int type = buf.readInt();
        Player player = (Player) ClientPacketHandler.fromId(buf.readInt());
        if(player == null)
            return null;
        double efficiency = buf.readDouble();
        int cooldown = buf.readInt();
        double fireAffinity = buf.readDouble();
        double lightAffinity = buf.readDouble();
        double iceAffinity = buf.readDouble();
        double earthAffinity = buf.readDouble();
        final SpellStats stats = new SpellStats(efficiency, cooldown, fireAffinity, lightAffinity, iceAffinity, earthAffinity);
        return switch (type) {
            default -> new Spell.BasicContext(player.level, player, stats);
            case 1 -> new Spell.PositionedContext(player.level, player, stats, new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        };
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.id);
        buf.writeRegistryId(GlyphCast.SPELL_REGISTRY.get(), this.spell);
        ctx.toNetwork(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPacketHandler.triggerSpellEvent(this.id, this.spell, this.ctx));
        ctx.get().setPacketHandled(true);
    }
}
