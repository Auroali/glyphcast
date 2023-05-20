package com.auroali.glyphcast.common.registry.listeners;

import com.auroali.glyphcast.GlyphcastExpectPlatform;
import com.auroali.glyphcast.common.registry.GCWandCores;
import com.auroali.glyphcast.common.wands.WandCore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class WandCoreReloadListener extends SimpleJsonResourceReloadListener {
    public static final Logger LOGGER = LogUtils.getLogger();

    public WandCoreReloadListener(Gson p_10768_, String p_10769_) {
        super(p_10768_, p_10769_);
    }

//    @SubscribeEvent
//    public static void serverStartedEvent(ServerStartedEvent event) {
//        GCWandCores.syncToClients();
//    }
//
//    @SubscribeEvent
//    public static void playerJoined(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getEntity() instanceof ServerPlayer player) {
//            GCNetwork.sendToClient(player, new SyncWandCoresMessage());
//        }
//    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        pProfiler.push("wand cores");
        Map<ResourceLocation, WandCore> map = new HashMap<>();
        for (var entry : pObject.entrySet()) {
            WandCore.CODEC.decode(JsonOps.INSTANCE, entry.getValue()).get()
                    .ifLeft(result -> map.put(entry.getKey(), result.getFirst()))
                    .ifRight(result -> LOGGER.error("Failed to parse wand core {}: {}", entry.getKey(), result.message()));

        }
        GCWandCores.KEY_MAP.clear();
        GCWandCores.VALUE_MAP.clear();

        for (Map.Entry<ResourceLocation, WandCore> entry : map.entrySet()) {
            GCWandCores.KEY_MAP.put(entry.getKey(), entry.getValue());
            GCWandCores.VALUE_MAP.put(entry.getValue(), entry.getKey());
        }

        LOGGER.info("Successfully loaded {} wand cores", GCWandCores.KEY_MAP.size());

        if (GlyphcastExpectPlatform.getServer() != null)
            GCWandCores.syncToClients();
        pProfiler.pop();
    }
}
