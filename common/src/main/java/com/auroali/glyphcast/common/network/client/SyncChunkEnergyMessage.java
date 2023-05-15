package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.energy.Fracture;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;


import java.util.ArrayList;
import java.util.List;

public class SyncChunkEnergyMessage extends NetworkMessage {
    public final List<Fracture> fractures;
    public final ChunkPos pos;

    public SyncChunkEnergyMessage(ChunkPos pos, List<Fracture> fractures) {
        this.pos = pos;
        this.fractures = fractures;
    }

    public SyncChunkEnergyMessage(FriendlyByteBuf buf) {
        pos = new ChunkPos(buf.readInt(), buf.readInt());
        fractures = new ArrayList<>();
        while (buf.isReadable()) {
            fractures.add(new Fracture(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readDouble(),
                    buf.readDouble())
            );
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(pos.x);
        buf.writeInt(pos.z);
        fractures.forEach(f -> {
            buf.writeInt(f.position().getX());
            buf.writeInt(f.position().getY());
            buf.writeInt(f.position().getZ());
            buf.writeDouble(f.energy());
            buf.writeDouble(f.maxEnergy());
        });
    }

    @Override
    public void handleS2C() {
        ClientPacketHandler.handleChunkEnergy(this);
    }
}
