package com.auroali.glyphcast.common.registry.listeners;

import com.auroali.glyphcast.GlyphcastExpectPlatform;
import com.auroali.glyphcast.common.registry.GCWandMaterials;
import com.auroali.glyphcast.common.wands.WandMaterial;
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

public class WandMaterialReloadListener extends SimpleJsonResourceReloadListener {
    public static final Logger LOGGER = LogUtils.getLogger();

    public WandMaterialReloadListener(Gson p_10768_, String p_10769_) {
        super(p_10768_, p_10769_);
    }

//    @SubscribeEvent
//    public static void serverStartedEvent(ServerStartedEvent event) {
//        GCWandMaterials.syncToClients();
//    }
//
//    @SubscribeEvent
//    public static void playerJoined(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getEntity() instanceof ServerPlayer player) {
//            GCNetwork.sendToClient(player, new SyncWandMaterialsMessage());
//        }
//    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        pProfiler.push("wand materials");
        Map<ResourceLocation, WandMaterial> map = new HashMap<>();
        for (var entry : pObject.entrySet()) {
            WandMaterial.CODEC.decode(JsonOps.INSTANCE, entry.getValue()).get()
                    .ifLeft(result -> map.put(entry.getKey(), result.getFirst()))
                    .ifRight(result -> LOGGER.error("Failed to parse wand material {}: {}", entry.getKey(), result.message()));

        }
        GCWandMaterials.KEY_MAP.clear();
        GCWandMaterials.VALUE_MAP.clear();

        for (Map.Entry<ResourceLocation, WandMaterial> entry : map.entrySet()) {
            GCWandMaterials.KEY_MAP.put(entry.getKey(), entry.getValue());
            GCWandMaterials.VALUE_MAP.put(entry.getValue(), entry.getKey());
        }

        LOGGER.info("Successfully loaded {} wand materials", GCWandMaterials.KEY_MAP.size());

        if (GlyphcastExpectPlatform.getServer() != null)
            GCWandMaterials.syncToClients();
        pProfiler.pop();
    }
}
