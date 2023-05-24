package com.auroali.glyphcast.common.network;

import com.auroali.glyphcast.common.network.client.*;
import com.auroali.glyphcast.common.network.server.ClearSpellSlotMessage;
import com.auroali.glyphcast.common.network.server.SelectSpellSlotMessage;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.network.server.WriteParchmentMessage;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("unused")
public class GCNetwork {
    public static final String PROTOCOL_VERSION = "1.0.0";
    public static final NetworkChannel CHANNEL = NetworkChannel.create(PROTOCOL_VERSION);

    public static void registerPackets() {
        CHANNEL.registerS2C(SpawnParticlesMessage.class, SpawnParticlesMessage::new);
        CHANNEL.registerS2C(SyncSpellUserDataMessage.class, SyncSpellUserDataMessage::new);
        CHANNEL.registerS2C(SyncSpellUserEnergyMessage.class, SyncSpellUserEnergyMessage::new);
        CHANNEL.registerS2C(SyncWandCoresMessage.class, SyncWandCoresMessage::new);
        CHANNEL.registerS2C(SyncCooldownManagerMessage.class, SyncCooldownManagerMessage::new);
        CHANNEL.registerS2C(SpellEventMessage.class, SpellEventMessage::new);
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

    @ExpectPlatform
    public static Packet<?> getEntitySpawnPacket(Entity entity, int data) {
        throw new AssertionError();
    }
}
