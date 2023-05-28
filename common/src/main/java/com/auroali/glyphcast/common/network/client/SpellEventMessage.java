package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
        this.spell = Glyphcast.SPELLS.get(buf.readResourceLocation());
        this.ctx = readCtx(buf);
    }

    public Spell.IContext readCtx(FriendlyByteBuf buf) {
        buf.readInt();
        InteractionHand hand = InteractionHand.values()[buf.readInt()];
        Player player = (Player) ClientPacketHandler.fromId(buf.readInt());
        if (player == null)
            return null;

        // A spell event is only ever triggered with a positioned context
        return new Spell.PositionedContext(player.level, player, hand,
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));

    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.id);
        buf.writeResourceLocation(Glyphcast.SPELLS.getId(spell));
        ctx.toNetwork(buf);
    }

    @Override
    public void handleS2C() {
        ClientPacketHandler.triggerSpellEvent(this.id, this.spell, this.ctx);
    }
}
