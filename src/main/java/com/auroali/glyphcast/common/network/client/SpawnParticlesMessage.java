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

    final ParticleOptions particle;
    final int count;
    final double spread;
    final Vec3 direction;
    final Vec3 pos;
    final double maxSpeed;
    final double minSpeed;

    public SpawnParticlesMessage(ParticleOptions options, double spread, int count, Vec3 pos, Vec3 direction, double speed) {
        particle = options;
        this.count = count;
        this.spread = spread;
        this.direction = direction;
        this.maxSpeed = speed;
        this.minSpeed = 0;
        this.pos = pos;
    }

    public SpawnParticlesMessage(ParticleOptions options, double spread, int count, Vec3 pos, Vec3 direction, double minSpeed, double maxSpeed) {
        particle = options;
        this.count = count;
        this.spread = spread;
        this.direction = direction;
        this.maxSpeed = maxSpeed;
        this.minSpeed = minSpeed;
        this.pos = pos;
    }

    @SuppressWarnings("deprecation")
    public SpawnParticlesMessage(FriendlyByteBuf buf) {
        ParticleType<?> type = buf.readById(Registry.PARTICLE_TYPE);
        count = buf.readInt();
        spread = buf.readDouble();
        maxSpeed = buf.readDouble();
        minSpeed = buf.readDouble();
        direction = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        particle = readParticle(buf, type);
    }

    @SuppressWarnings("deprecation")
    public void encode(FriendlyByteBuf buf) {
        buf.writeId(Registry.PARTICLE_TYPE, this.particle.getType());
        buf.writeInt(count);
        buf.writeDouble(spread);
        buf.writeDouble(maxSpeed);
        buf.writeDouble(minSpeed);
        buf.writeDouble(direction.x);
        buf.writeDouble(direction.y);
        buf.writeDouble(direction.z);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        particle.writeToNetwork(buf);
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf pBuffer, ParticleType<T> pParticleType) {
        return pParticleType.getDeserializer().fromNetwork(pParticleType, pBuffer);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.spawnParticles(this)));
        ctx.get().setPacketHandled(true);
    }
}
