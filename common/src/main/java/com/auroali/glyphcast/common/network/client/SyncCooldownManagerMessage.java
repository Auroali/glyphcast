package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.spells.SpellCooldownManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class SyncCooldownManagerMessage extends NetworkMessage {
    final CompoundTag managerNbt;

    public SyncCooldownManagerMessage(SpellCooldownManager manager) {
        this.managerNbt = manager.serialize();
    }

    public SyncCooldownManagerMessage(FriendlyByteBuf buf) {
        this.managerNbt = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(managerNbt);
    }

    @Override
    public void handleS2C() {
        ClientPacketHandler.syncCooldownManager(managerNbt);
    }
}
