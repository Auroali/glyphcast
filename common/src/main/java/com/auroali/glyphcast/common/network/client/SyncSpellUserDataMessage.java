package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class SyncSpellUserDataMessage extends NetworkMessage {
    final CompoundTag tag;

    public SyncSpellUserDataMessage(ISpellUser user) {
        this.tag = user.serializeNBT();
    }

    public SyncSpellUserDataMessage(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    @Override
    public void handleS2C() {
        ClientPacketHandler.syncSpellUserData(tag);
    }
}
