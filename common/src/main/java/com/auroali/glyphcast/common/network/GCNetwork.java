package com.auroali.glyphcast.common.network;

import com.auroali.glyphcast.common.network.both.SetQuickSelectSlotMessage;
import com.auroali.glyphcast.common.network.client.*;
import com.auroali.glyphcast.common.network.client.menu.SendScribingGlyphListMessage;
import com.auroali.glyphcast.common.network.server.ClearSpellSlotMessage;
import com.auroali.glyphcast.common.network.server.QuickSelectSlotMessage;
import com.auroali.glyphcast.common.network.server.SelectSpellSlotMessage;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.network.server.menu.ScribingEraseButtonPress;
import com.auroali.glyphcast.common.network.server.menu.ScribingGlyphButtonPress;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("unused")
public class GCNetwork {
    public static final String PROTOCOL_VERSION = "1.0.0";
    public static final NetworkChannel CHANNEL = NetworkChannel.create(PROTOCOL_VERSION);

    public static void registerPackets() {
        // Server --> Client
        CHANNEL.registerS2C(SpawnParticlesMessage.class, SpawnParticlesMessage::new);
        CHANNEL.registerS2C(SyncSpellUserDataMessage.class, SyncSpellUserDataMessage::new);
        CHANNEL.registerS2C(SyncSpellUserEnergyMessage.class, SyncSpellUserEnergyMessage::new);
        CHANNEL.registerS2C(SyncWandCoresMessage.class, SyncWandCoresMessage::new);
        CHANNEL.registerS2C(SyncCooldownManagerMessage.class, SyncCooldownManagerMessage::new);
        CHANNEL.registerS2C(SpellEventMessage.class, SpellEventMessage::new);
        CHANNEL.registerS2C(SendScribingGlyphListMessage.class, SendScribingGlyphListMessage::new);
        // Client --> Server
        CHANNEL.registerC2S(SetSlotSpellMessage.class, SetSlotSpellMessage::new);
        CHANNEL.registerC2S(SelectSpellSlotMessage.class, SelectSpellSlotMessage::new);
        CHANNEL.registerC2S(QuickSelectSlotMessage.class, QuickSelectSlotMessage::new);
        CHANNEL.registerC2S(ClearSpellSlotMessage.class, ClearSpellSlotMessage::new);
        CHANNEL.registerC2S(ScribingGlyphButtonPress.class, ScribingGlyphButtonPress::new);
        CHANNEL.registerC2S(ScribingEraseButtonPress.class, ScribingEraseButtonPress::new);
        // Client <--> Server
        CHANNEL.registerC2S(SetQuickSelectSlotMessage.C2S.class, SetQuickSelectSlotMessage.C2S::new);
        CHANNEL.registerS2C(SetQuickSelectSlotMessage.S2C.class, SetQuickSelectSlotMessage.S2C::new);
    }

    @ExpectPlatform
    public static Packet<?> getEntitySpawnPacket(Entity entity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Packet<?> getEntitySpawnPacket(Entity entity, int data) {
        throw new AssertionError();
    }
}
