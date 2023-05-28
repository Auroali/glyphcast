package com.auroali.glyphcast.common.network.both;

import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.network.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SetQuickSelectSlotMessage extends NetworkMessage {

    public final int slot;
    public final int quickSelect;

    public SetQuickSelectSlotMessage(int slot, int quickSelect) {
        this.slot = slot;
        this.quickSelect = quickSelect;
    }

    public SetQuickSelectSlotMessage(FriendlyByteBuf buf) {
        this.slot = buf.readInt();
        this.quickSelect = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeInt(quickSelect);
    }

    @Override
    public void handleC2S(ServerPlayer player) {
        SpellUser.get(player).ifPresent(user -> user.setQuickSelectForSlot(slot, quickSelect));
    }

    @Override
    public void handleS2C() {
        ClientPacketHandler.setQuickSelectForSlot(this);
    }

    // forge is complaining so these have to be separate classes
    public static class C2S extends SetQuickSelectSlotMessage {
        public C2S(int slot, int quickSelect) {
            super(slot, quickSelect);
        }

        public C2S(FriendlyByteBuf buf) {
            super(buf);
        }
    }

    public static class S2C extends SetQuickSelectSlotMessage {
        public S2C(int slot, int quickSelect) {
            super(slot, quickSelect);
        }

        public S2C(FriendlyByteBuf buf) {
            super(buf);
        }
    }
}
