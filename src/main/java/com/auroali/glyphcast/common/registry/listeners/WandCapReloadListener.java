package com.auroali.glyphcast.common.registry.listeners;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.network.client.SyncWandCapsMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.registry.GCWandCaps;
import com.auroali.glyphcast.common.wands.WandCap;
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
public class WandCapReloadListener extends SimpleJsonResourceReloadListener {
    public WandCapReloadListener(Gson p_10768_, String p_10769_) {
        super(p_10768_, p_10769_);
    }

    @SubscribeEvent
    public static void serverStartedEvent(ServerStartedEvent event) {
        GCWandCaps.syncToClients();
    }

    @SubscribeEvent
    public static void playerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            GCNetwork.sendToClient(player, new SyncWandCapsMessage());
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        pProfiler.push("wand caps");
        Map<ResourceLocation, WandCap> map = new HashMap<>();
        for (var entry : pObject.entrySet()) {
            WandCap.CODEC.decode(JsonOps.INSTANCE, entry.getValue()).get()
                    .ifLeft(result -> map.put(entry.getKey(), result.getFirst()))
                    .ifRight(result -> GlyphCast.LOGGER.error("Failed to parse wand cap {}: {}", entry.getKey(), result.message()));

        }
        GCWandCaps.KEY_MAP.clear();
        GCWandCaps.VALUE_MAP.clear();

        for (Map.Entry<ResourceLocation, WandCap> entry : map.entrySet()) {
            GCWandCaps.KEY_MAP.put(entry.getKey(), entry.getValue());
            GCWandCaps.VALUE_MAP.put(entry.getValue(), entry.getKey());
        }
        if (ServerLifecycleHooks.getCurrentServer() != null)
            GCWandCaps.syncToClients();
        pProfiler.pop();
    }
}
