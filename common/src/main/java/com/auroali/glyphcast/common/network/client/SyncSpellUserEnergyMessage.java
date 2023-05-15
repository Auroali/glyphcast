package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.network.FriendlyByteBuf;

public class SyncSpellUserEnergyMessage extends NetworkMessage {
    final double energy;

    public SyncSpellUserEnergyMessage(ISpellUser user) {
        this.energy = user.getEnergy();
    }

    public SyncSpellUserEnergyMessage(FriendlyByteBuf buf) {
        this.energy = buf.readDouble();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(energy);
    }

    @Override
    public void handleS2C() {
        ClientPacketHandler.syncSpellUserEnergy(energy);
    }
}
