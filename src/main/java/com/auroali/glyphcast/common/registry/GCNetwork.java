package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.network.client.*;
import com.auroali.glyphcast.common.network.server.RequestChunkEnergyMessage;
import com.auroali.glyphcast.common.network.server.SelectSpellSlotMessage;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.network.server.WriteParchmentMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class GCNetwork {
    public static final String PROTOCOL_VERSION = "1.0.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(GlyphCast.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private static int id = 0;

    public static void registerPackets() {
        registerPlayToClient(SpawnParticlesMessage.class, SpawnParticlesMessage::new);
        registerPlayToClient(SyncSpellUserDataMessage.class, SyncSpellUserDataMessage::new);
        registerPlayToClient(SyncSpellUserEnergyMessage.class, SyncSpellUserEnergyMessage::new);
        registerPlayToClient(SyncWandCoresMessage.class, SyncWandCoresMessage::new);
        registerPlayToClient(SyncWandMaterialsMessage.class, SyncWandMaterialsMessage::new);
        registerPlayToClient(SyncWandCapsMessage.class, SyncWandCapsMessage::new);
        registerPlayToClient(SpellEventMessage.class, SpellEventMessage::new);
        registerPlayToClient(SyncChunkEnergyMessage.class, SyncChunkEnergyMessage::new);
        registerPlayToServer(WriteParchmentMessage.class, WriteParchmentMessage::new);
        registerPlayToServer(SetSlotSpellMessage.class, SetSlotSpellMessage::new);
        registerPlayToServer(SelectSpellSlotMessage.class, SelectSpellSlotMessage::new);
        registerPlayToServer(RequestChunkEnergyMessage.class, RequestChunkEnergyMessage::new);
        //registerPlayToServer(ClearSpellSlotMessage.class, ClearSpellSlotMessage::new);
    }

    private static <T extends NetworkMessage> void registerWithDirection(int id, Class<T> packet, Function<FriendlyByteBuf, T> decoder, Optional<NetworkDirection> direction) {
        CHANNEL.registerMessage(id, packet, T::encode, decoder, T::handle, direction);
    }

    private static <T extends NetworkMessage> void registerPlayToServer(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        registerWithDirection(id++, packet, decoder, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private static <T extends NetworkMessage> void registerPlayToClient(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        registerWithDirection(id++, packet, decoder, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private static <T extends NetworkMessage> void register(Class<T> packet, Function<FriendlyByteBuf, T> decoder) {
        registerWithDirection(id++, packet, decoder, Optional.empty());
    }

    public static void sendToClient(ServerPlayer player, Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAll(Object message) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

    public static void sendToNear(Level level, Vec3 pos, double radius, Object message) {
        CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.x, pos.y, pos.z, radius, level.dimension())), message);
    }

    public static void sendNearPlayer(ServerPlayer player, double radius, SpawnParticlesMessage msg) {
        sendToNear(player.level, player.position(), radius, msg);
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }
}
