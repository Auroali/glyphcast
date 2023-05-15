package com.auroali.glyphcast.common.network.server;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;


public class SetSlotSpellMessage extends NetworkMessage {

    final Spell spell;
    final int slot;

    public SetSlotSpellMessage(int slot, Spell spell) {
        this.spell = spell;
        this.slot = slot;
    }

    public SetSlotSpellMessage(FriendlyByteBuf buf) {
        spell = Glyphcast.SPELLS.get(buf.readResourceLocation());
        slot = buf.readInt();

    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(Glyphcast.SPELLS.getKey(spell).get().location());
        buf.writeInt(slot);
    }

    @Override
    public void handleC2S(ServerPlayer player) {
        SpellUser.get(player).ifPresent(user -> user.setSpellForSlot(slot, spell));
    }
}
