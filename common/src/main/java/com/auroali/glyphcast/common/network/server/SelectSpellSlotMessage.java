package com.auroali.glyphcast.common.network.server;

import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SelectSpellSlotMessage extends NetworkMessage {

    final int slot;

    public SelectSpellSlotMessage(int slot) {
        this.slot = slot;
    }

    public SelectSpellSlotMessage(FriendlyByteBuf buf) {
        this.slot = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }

    @Override
    public void handleC2S(ServerPlayer player) {
        SpellUser.get(player).ifPresent(user -> user.selectSpellSlot(slot));
    }
}
