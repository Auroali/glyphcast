package com.auroali.glyphcast.common.network.server;

import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class ClearSpellSlotMessage extends NetworkMessage {

    final int slot;

    public ClearSpellSlotMessage(int slot) {
        this.slot = slot;
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }

    public ClearSpellSlotMessage(FriendlyByteBuf buf) {
        slot = buf.readInt();

    }
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getSender() == null)
                return;

            SpellUser.get(ctx.get().getSender()).ifPresent(user -> user.setSpellForSlot(slot, null));
        });
        ctx.get().setPacketHandled(true);
    }
}
