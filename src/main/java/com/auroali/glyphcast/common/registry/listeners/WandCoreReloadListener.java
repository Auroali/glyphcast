package com.auroali.glyphcast.common.registry.listeners;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.network.client.SyncWandCoresMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.registry.GCWandCores;
import com.auroali.glyphcast.common.wands.WandCore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = GlyphCast.MODID)
public class WandCoreReloadListener extends SimpleJsonResourceReloadListener {
    public WandCoreReloadListener(Gson p_10768_, String p_10769_) {
        super(p_10768_, p_10769_);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        pProfiler.push("wand cores");
        Map<ResourceLocation, WandCore> map = new HashMap<>();
        for(var entry : pObject.entrySet()) {
            WandCore.CODEC.decode(JsonOps.INSTANCE, entry.getValue()).get()
                    .ifLeft(result -> map.put(entry.getKey(), result.getFirst()))
                    .ifRight(result -> GlyphCast.LOGGER.error("Failed to parse wand core {}: {}", entry.getKey(), result.message()));

        }
        GCWandCores.KEY_MAP.clear();
        GCWandCores.VALUE_MAP.clear();

        for(Map.Entry<ResourceLocation, WandCore> entry : map.entrySet()) {
            GCWandCores.KEY_MAP.put(entry.getKey(), entry.getValue());
            GCWandCores.VALUE_MAP.put(entry.getValue(), entry.getKey());
        }
        if(ServerLifecycleHooks.getCurrentServer() != null)
            GCWandCores.syncToClients();
        pProfiler.pop();
    }

    @SubscribeEvent
    public static void serverStartedEvent(ServerStartedEvent event) {
        GCWandCores.syncToClients();
    }

    @SubscribeEvent
    public static void playerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            GCNetwork.sendToClient(player, new SyncWandCoresMessage());
        }
    }
}
