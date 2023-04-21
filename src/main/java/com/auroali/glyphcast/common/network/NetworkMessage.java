package com.auroali.glyphcast.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class NetworkMessage {
    public abstract void encode(FriendlyByteBuf buf);

    public abstract void handle(Supplier<NetworkEvent.Context> ctx);
}
