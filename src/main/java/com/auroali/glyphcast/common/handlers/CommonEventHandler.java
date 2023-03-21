package com.auroali.glyphcast.common.handlers;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy;
import com.auroali.glyphcast.common.entities.FloatingLight;
import com.auroali.glyphcast.common.registry.GCCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;

@Mod.EventBusSubscriber(modid = GlyphCast.MODID)
public class CommonEventHandler {

    @SubscribeEvent
    public static void playerChangedDimensions(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.getServer() != null) {
            teleportLights(event, player);
        }
    }

    private static void teleportLights(PlayerEvent.PlayerChangedDimensionEvent event, ServerPlayer player) {
        if(player.getServer() == null)
            return;

        ServerLevel level = player.getServer().getLevel(event.getFrom());
        if(level == null)
            return;
        var lights = FloatingLight.getAllFollowing(event.getEntity(), level);
        lights.forEach(e -> {
            e.remove(Entity.RemovalReason.CHANGED_DIMENSION);
            FloatingLight newLight = new FloatingLight(player.level, player.getX(), player.getY(), player.getZ());
            newLight.setOwner(player);
            player.level.addFreshEntity(newLight);
        });
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase != TickEvent.Phase.END || event.side != LogicalSide.SERVER)
            return;

        SpellUser.get(event.player).ifPresent(user -> user.getTickingSpells().removeIf(data -> {
            boolean flag = !data.getSpell().tick(event.player.level, event.player, data.getTicks(), data.getTag());
            data.setTicks(data.getTicks() + 1);
            return flag;

        }));

        for(int x = -5; x <= 5; x++) {
            for(int z = -5; z <= 5; z++) {
                ChunkPos pos = event.player.chunkPosition();
                if(!event.player.level.hasChunk(pos.x + x, pos.z + z))
                    return;

                event.player.getLevel().getChunk(pos.x + x, pos.z + z).getCapability(GCCapabilities.CHUNK_ENERGY)
                        .ifPresent(IChunkEnergy::tick);
            }
        }


        DecimalFormat format = new DecimalFormat("####");
        double energy = IChunkEnergy.getEnergyAt(event.player.level, event.player.blockPosition());
        double maxEnergy = IChunkEnergy.getMaxEnergyAt(event.player.level, event.player.blockPosition());
        double energyPercent = (energy / maxEnergy) * 100;
        event.player.displayClientMessage(Component.literal("%s/%s (%s%%)".formatted(format.format(energy), format.format(maxEnergy), format.format(energyPercent))), true);

    }
}
