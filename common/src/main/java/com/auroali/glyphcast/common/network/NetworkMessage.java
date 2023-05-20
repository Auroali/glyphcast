package com.auroali.glyphcast.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public abstract class NetworkMessage {

    public abstract void encode(FriendlyByteBuf buf);

    public void handleC2S(ServerPlayer player) {
    }

    public void handleS2C() {
    }
}
