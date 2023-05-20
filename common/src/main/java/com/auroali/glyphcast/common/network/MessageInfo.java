package com.auroali.glyphcast.common.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class MessageInfo<T extends NetworkMessage> {
    Class<T> msg;
    BiConsumer<T, FriendlyByteBuf> encoder;
    Function<FriendlyByteBuf, T> decoder;

    public MessageInfo(Class<T> msg, Function<FriendlyByteBuf, T> decoder) {
        this.msg = msg;
        this.encoder = T::encode;
        this.decoder = decoder;
    }

    public Class<T> getMsgClass() {
        return msg;
    }

    public void encode(T obj, FriendlyByteBuf buf) {
        encoder.accept(obj, buf);
    }

    public T decode(FriendlyByteBuf buf) {
        return decoder.apply(buf);
    }
}
