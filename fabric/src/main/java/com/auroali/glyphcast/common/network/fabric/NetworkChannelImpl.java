package com.auroali.glyphcast.common.network.fabric;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.GlyphcastExpectPlatform;
import com.auroali.glyphcast.common.network.MessageInfo;
import com.auroali.glyphcast.common.network.NetworkChannel;
import com.auroali.glyphcast.common.network.NetworkMessage;
import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.function.Function;

public class NetworkChannelImpl extends NetworkChannel {
    private static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Glyphcast.MODID, "channel");

    protected NetworkChannelImpl(String protocol) {
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_NAME, (server, player, handler, buf, sender) -> {
            int id = buf.readInt();
            if (messages.isEmpty() || id < 0 || id > messages.size()) {
                LOGGER.error("Unable to handle packet <id: {}> on the server!", id);
            }
            MessageInfo<?> info = messages.get(id);
            NetworkMessage msg = info.decode(buf);
            server.execute(() -> msg.handleC2S(player));
        });
        this.messages = new ArrayList<>();
        if (Platform.getEnv() != EnvType.CLIENT)
            return;

        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(Glyphcast.MODID, "channel"), (client, handler, buf, sender) -> {
            int id = buf.readInt();
            if (messages.isEmpty() || id < 0 || id > messages.size()) {
                LOGGER.error("Unable to handle packet <id: {}> on the client!", id);
            }
            MessageInfo<? extends NetworkMessage> info = messages.get(id);
            NetworkMessage msg = info.decode(buf);
            client.execute(msg::handleS2C);
        });
    }

    public static NetworkChannel create(String version) {
        return new NetworkChannelImpl(version);
    }

    @SuppressWarnings("rawtypes")
    Pair<FriendlyByteBuf, MessageInfo> createBufferFor(Object msg) {
        for (int i = 0; i < messages.size(); i++) {
            if (!messages.get(i).getMsgClass().equals(msg.getClass()))
                continue;

            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeInt(i);
            return Pair.of(buf, messages.get(i));
        }

        LOGGER.error("Packet of type {} is not registered!", msg.getClass());
        throw new IllegalArgumentException("Packet of type %s is not registered!".formatted(msg.getClass()));
    }

    @Override
    public <T extends NetworkMessage> void registerS2C(Class<T> type, Function<FriendlyByteBuf, T> decoder) {
        MessageInfo<T> info = new MessageInfo<>(type, decoder);
        messages.add(info);
    }

    @Override
    public <T extends NetworkMessage> void registerC2S(Class<T> type, Function<FriendlyByteBuf, T> decoder) {
        MessageInfo<T> info = new MessageInfo<>(type, decoder);
        messages.add(info);
    }


    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void sendToPlayer(ServerPlayer player, Object msg) {
        Pair<FriendlyByteBuf, MessageInfo> bufferPair = createBufferFor(msg);
        bufferPair.second().encode((NetworkMessage) msg, bufferPair.first());
        ServerPlayNetworking.send(player, CHANNEL_NAME, bufferPair.first());
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void sendToServer(Object msg) {
        Pair<FriendlyByteBuf, MessageInfo> bufferPair = createBufferFor(msg);
        bufferPair.second().encode((NetworkMessage) msg, bufferPair.first());
        ClientPlayNetworking.send(CHANNEL_NAME, bufferPair.first());
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void sendToNear(Level level, Vec3 position, double range, Object msg) {
        Pair<FriendlyByteBuf, MessageInfo> bufferPair = createBufferFor(msg);
        bufferPair.second().encode((NetworkMessage) msg, bufferPair.first());
        PlayerLookup.around((ServerLevel) level, position, range)
                .forEach(p -> ServerPlayNetworking.send(p, CHANNEL_NAME, bufferPair.first()));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void sendToAll(Object msg) {
        Pair<FriendlyByteBuf, MessageInfo> bufferPair = createBufferFor(msg);
        bufferPair.second().encode((NetworkMessage) msg, bufferPair.first());
        PlayerLookup.all(GlyphcastExpectPlatform.getServer()).forEach(p -> ServerPlayNetworking.send(p, CHANNEL_NAME, bufferPair.first()));
    }
}