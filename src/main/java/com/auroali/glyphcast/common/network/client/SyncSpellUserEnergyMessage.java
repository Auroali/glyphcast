package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPacketHandler.syncSpellUserEnergy(energy));
        ctx.get().setPacketHandled(true);
    }
}
