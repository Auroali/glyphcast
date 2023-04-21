package com.auroali.glyphcast.common.network.server;

import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null)
                return;
            SpellUser.get(ctx.get().getSender()).ifPresent(user -> user.selectSpellSlot(slot));
        });
        ctx.get().setPacketHandled(true);
    }
}
