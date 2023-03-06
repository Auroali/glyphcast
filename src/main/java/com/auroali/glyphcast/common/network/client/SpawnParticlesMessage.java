package com.auroali.glyphcast.common.network.client;

import com.auroali.glyphcast.common.network.NetworkMessage;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnParticlesMessage extends NetworkMessage {

    ParticleOptions particle;
    int count;
    Vec3 direction;
    Vec3 pos;
    double maxSpeed;


    @SuppressWarnings("deprecation")
    public void encode(FriendlyByteBuf buf) {
        buf.writeId(Registry.PARTICLE_TYPE, this.particle.getType());
        buf.writeInt(count);
        buf.writeDouble(maxSpeed);
        buf.writeDouble(direction.x);
        buf.writeDouble(direction.y);
        buf.writeDouble(direction.z);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        particle.writeToNetwork(buf);
    }

    public SpawnParticlesMessage(ParticleOptions options, int count, Vec3 pos, Vec3 direction, double speed) {
        particle = options;
        this.count = count;
        this.direction = direction;
        this.maxSpeed = speed;
        this.pos = pos;
    }
    @SuppressWarnings("deprecation")
    public SpawnParticlesMessage(FriendlyByteBuf buf) {
        ParticleType<?> type = buf.readById(Registry.PARTICLE_TYPE);
        count = buf.readInt();
        maxSpeed = buf.readDouble();
        direction = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        particle = readParticle(buf, type);
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf pBuffer, ParticleType<T> pParticleType) {
        return pParticleType.getDeserializer().fromNetwork(pParticleType, pBuffer);
    }
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.spawnParticles(this)));
        ctx.get().setPacketHandled(true);
    }
}
