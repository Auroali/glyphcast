package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.registry.GCWandMaterials;
import com.auroali.glyphcast.common.wands.WandMaterial;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncWandMaterialsMessage extends NetworkMessage {
    Map<ResourceLocation, WandMaterial> map;
    public SyncWandMaterialsMessage() {
        map = GCWandMaterials.KEY_MAP;
    }
    @SuppressWarnings("deprecation")
    public SyncWandMaterialsMessage(FriendlyByteBuf buf) {
        map = new HashMap<>();
        while(buf.isReadable()) {
            map.put(buf.readResourceLocation(), buf.readWithCodec(WandMaterial.CODEC));
        }
    }
    @Override
    @SuppressWarnings("deprecation")
    public void encode(FriendlyByteBuf buf) {
        for(Map.Entry<ResourceLocation, WandMaterial> entry : map.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeWithCodec(WandMaterial.CODEC, entry.getValue());
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GCWandMaterials.KEY_MAP.clear();
            GCWandMaterials.VALUE_MAP.clear();
            for(Map.Entry<ResourceLocation, WandMaterial> entry : map.entrySet()) {
                GCWandMaterials.KEY_MAP.put(entry.getKey(), entry.getValue());
                GCWandMaterials.VALUE_MAP.put(entry.getValue(), entry.getKey());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
