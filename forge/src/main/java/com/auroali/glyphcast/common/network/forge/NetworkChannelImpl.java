package com.auroali.glyphcast.common.network.forge;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.network.MessageInfo;
import com.auroali.glyphcast.common.network.NetworkChannel;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public class NetworkChannelImpl extends NetworkChannel {
    SimpleChannel channel;

    protected NetworkChannelImpl(String protocol) {
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Glyphcast.MODID, "channel"),
                () -> protocol,
                protocol::equals,
                protocol::equals
        );
        this.messages = new ArrayList<>();
    }

    public static NetworkChannel create(String version) {
        return new NetworkChannelImpl(version);
    }

    @Override
    public <T extends NetworkMessage> void registerS2C(Class<T> type, Function<FriendlyByteBuf, T> decoder) {
        MessageInfo<T> info = new MessageInfo<>(type, decoder, false);
        messages.add(info);
        channel.registerMessage(messages.size(), info.getMsgClass(), info::encode, info::decode, (msg, ctx) -> {
            ctx.get().enqueueWork(msg::handleS2C);
            ctx.get().setPacketHandled(true);
        }, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    @Override
    public <T extends NetworkMessage> void registerC2S(Class<T> type, Function<FriendlyByteBuf, T> decoder) {
        MessageInfo<T> info = new MessageInfo<>(type, decoder, true);
        messages.add(info);
        channel.registerMessage(messages.size(), info.getMsgClass(), info::encode, info::decode, (msg, ctx) -> {
            ctx.get().enqueueWork(() -> msg.handleC2S(ctx.get().getSender()));
            ctx.get().setPacketHandled(true);
        }, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, Object msg) {
        if (isInvalid(msg)) {
            LOGGER.error("Message {} is not registered!", msg.getClass());
            return;
        }
        channel.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    @Override
    public void sendToServer(Object msg) {
        if (isInvalid(msg)) {
            LOGGER.error("Message {} is not registered!", msg.getClass());
            return;
        }
        channel.sendToServer(msg);
    }

    @Override
    public void sendToNear(Level level, Vec3 position, double range, Object msg) {
        if (isInvalid(msg)) {
            LOGGER.error("Message {} is not registered!", msg.getClass());
            return;
        }
        channel.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(position.x, position.y, position.x, range, level.dimension())), msg);
    }

    @Override
    public void sendToAll(Object msg) {
        if (isInvalid(msg)) {
            LOGGER.error("Message {} is not registered!", msg.getClass());
            return;
        }

        channel.send(PacketDistributor.ALL.noArg(), msg);
    }

    @Override
    public void sendToTracking(ServerLevel level, BlockPos pos, Object msg) {
        if (isInvalid(msg)) {
            LOGGER.error("Message {} is not registered!", msg.getClass());
            return;
        }
        if(!level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
            return;

        channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), msg);
    }
}
