package com.auroali.glyphcast.common.network;

import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.network.client.*;
import com.auroali.glyphcast.common.network.server.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class GCNetwork {
    public static final String PROTOCOL_VERSION = "1.0.0";
    public static final NetworkChannel CHANNEL = NetworkChannel.create(PROTOCOL_VERSION);

    public static void registerPackets() {
        CHANNEL.registerS2C(SpawnParticlesMessage.class, SpawnParticlesMessage::new);
        CHANNEL.registerS2C(SyncSpellUserDataMessage.class, SyncSpellUserDataMessage::new);
        CHANNEL.registerS2C(SyncSpellUserEnergyMessage.class, SyncSpellUserEnergyMessage::new);
        CHANNEL.registerS2C(SyncWandCoresMessage.class, SyncWandCoresMessage::new);
        CHANNEL.registerS2C(SyncWandMaterialsMessage.class, SyncWandMaterialsMessage::new);
        CHANNEL.registerS2C(SyncWandCapsMessage.class, SyncWandCapsMessage::new);
        CHANNEL.registerS2C(SpellEventMessage.class, SpellEventMessage::new);
        CHANNEL.registerS2C(SyncChunkEnergyMessage.class, SyncChunkEnergyMessage::new);
        CHANNEL.registerC2S(WriteParchmentMessage.class, WriteParchmentMessage::new);
        CHANNEL.registerC2S(SetSlotSpellMessage.class, SetSlotSpellMessage::new);
        CHANNEL.registerC2S(SelectSpellSlotMessage.class, SelectSpellSlotMessage::new);
        CHANNEL.registerC2S(ClearSpellSlotMessage.class, ClearSpellSlotMessage::new);
    }

//    private static <T extends NetworkMessage> void registerWithDirection(int id, Class<T> packet, Function<FriendlyByteBuf, T> decoder, Optional<NetworkDirection> direction) {
//        CHANNEL.registerMessage(id, packet, T::encode, decoder, T::handle, direction);
//    }
//
//    private static <T extends NetworkMessage> void registerPlayToServer(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
//        registerWithDirection(id++, packet, decoder, Optional.of(NetworkDirection.PLAY_TO_SERVER));
//    }
//
//    private static <T extends NetworkMessage> void registerPlayToClient(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
//        registerWithDirection(id++, packet, decoder, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
//    }
//
//    private static <T extends NetworkMessage> void register(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
//        registerWithDirection(id++, packet, decoder, Optional.empty());
//    }
//
//    public static void sendToClient(ServerPlayer player, Object message) {
//        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
//    }
//
//    public static void sendToAll(Object message) {
//        CHANNEL.send(PacketDistributor.ALL.noArg(), message);
//    }
//
//    public static void sendToNear(Level level, Vec3 pos, double radius, Object message) {
//        CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.x, pos.y, pos.z, radius, level.dimension())), message);
//    }
//
//    public static void sendNearPlayer(ServerPlayer player, double radius, SpawnParticlesMessage msg) {
//        sendToNear(player.level, player.position(), radius, msg);
//    }
//
//    public static void sendToServer(Object message) {
//        CHANNEL.sendToServer(message);
//    }

    @ExpectPlatform
    public static Packet<?> getEntitySpawnPacket(Entity entity) {
        throw new AssertionError();
    }
}
