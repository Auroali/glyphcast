package com.auroali.glyphcast.common.network;

import com.mojang.logging.LogUtils;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Function;

public class NetworkChannel {
    protected static Logger LOGGER = LogUtils.getLogger();
    protected List<MessageInfo<?>> messages;

    @ExpectPlatform
    public static NetworkChannel create(String version) {
        throw new AssertionError();
    }

    public <T extends NetworkMessage> void registerS2C(Class<T> type, Function<FriendlyByteBuf, T> decoder) {
        throw new AssertionError();
    }

    public <T extends NetworkMessage> void registerC2S(Class<T> type, Function<FriendlyByteBuf, T> decoder) {
        throw new AssertionError();
    }

    protected boolean isInvalid(Object message) {
        return messages.stream().noneMatch(messageInfo -> messageInfo.getMsgClass().equals(message.getClass()));
    }

    public void sendToPlayer(ServerPlayer player, Object msg) {
        throw new AssertionError();
    }

    public void sendToServer(Object msg) {
        throw new AssertionError();
    }

    public void sendToNear(Level level, Vec3 position, double range, Object msg) {
        throw new AssertionError();
    }

    public void sendToAll(Object msg) {
        throw new AssertionError();
    }
}
