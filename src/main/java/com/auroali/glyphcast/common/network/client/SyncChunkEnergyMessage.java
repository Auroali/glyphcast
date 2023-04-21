package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.energy.Fracture;
import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncChunkEnergyMessage extends NetworkMessage {

    public final double[] values;
    public final double[] maxValues;
    public final List<Fracture> fractures;
    public final ChunkPos pos;

    public SyncChunkEnergyMessage(ChunkPos pos, double[] values, double[] maxValues, List<Fracture> fractures) {
        this.pos = pos;
        this.values = values;
        this.maxValues = maxValues;
        this.fractures = fractures;
    }

    public SyncChunkEnergyMessage(FriendlyByteBuf buf) {
        pos = new ChunkPos(buf.readInt(), buf.readInt());
        values = new double[256];
        maxValues = new double[256];
        fractures = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            values[i] = buf.readDouble();
            maxValues[i] = buf.readDouble();
        }
        while (buf.isReadable()) {
            fractures.add(new Fracture(buf.readInt(), buf.readInt(), buf.readInt()));
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(pos.x);
        buf.writeInt(pos.z);
        for (int i = 0; i < 256; i++) {
            buf.writeDouble(values[i]);
            buf.writeDouble(maxValues[i]);
        }
        fractures.forEach(f -> {
            buf.writeInt(f.position().getX());
            buf.writeInt(f.position().getY());
            buf.writeInt(f.position().getZ());
        });
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPacketHandler.handleChunkEnergy(this));
        ctx.get().setPacketHandled(true);
    }
}
