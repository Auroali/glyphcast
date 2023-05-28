package com.auroali.glyphcast.common.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class MessageInfo<T extends NetworkMessage> {
    private Class<T> msg;
    private BiConsumer<T, FriendlyByteBuf> encoder;
    private Function<FriendlyByteBuf, T> decoder;

    private final boolean isC2S;

    public MessageInfo(Class<T> msg, Function<FriendlyByteBuf, T> decoder, boolean isC2S) {
        this.msg = msg;
        this.encoder = T::encode;
        this.decoder = decoder;
        this.isC2S = isC2S;
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

    public boolean isC2S() {
        return isC2S;
    }
}
