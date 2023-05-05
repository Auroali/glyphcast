package com.auroali.glyphcast.common.network.server;

import com.auroali.glyphcast.common.capabilities.chunk.ChunkEnergy;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.network.client.SyncChunkEnergyMessage;
import com.auroali.glyphcast.common.registry.GCCapabilities;
import com.auroali.glyphcast.common.registry.GCNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestChunkEnergyMessage extends NetworkMessage {

    final ChunkPos pos;

    public RequestChunkEnergyMessage(ChunkPos pos) {
        this.pos = pos;
    }

    public RequestChunkEnergyMessage(FriendlyByteBuf buf) {
        this.pos = new ChunkPos(buf.readLong());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null || !ctx.get().getSender().level.hasChunk(pos.x, pos.z)) return;

            ctx.get().getSender().level.getChunk(pos.x, pos.z).getCapability(GCCapabilities.CHUNK_ENERGY).ifPresent(cap -> {
                if (cap instanceof ChunkEnergy energy)
                    GCNetwork.sendToClient(ctx.get().getSender(), new SyncChunkEnergyMessage(pos, energy.fractures));
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
