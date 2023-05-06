package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.common.network.client.SyncWandMaterialsMessage;
import com.auroali.glyphcast.common.wands.WandMaterial;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GCWandMaterials {
    public static final Map<ResourceLocation, WandMaterial> KEY_MAP = new HashMap<>();
    public static final Map<WandMaterial, ResourceLocation> VALUE_MAP = new HashMap<>();

    public static WandMaterial getValue(ResourceLocation location) {
        return KEY_MAP.get(location);
    }

    public static ResourceLocation getKey(WandMaterial core) {
        return VALUE_MAP.get(core);
    }

    public static void syncToClients() {
        GCNetwork.sendToAll(new SyncWandMaterialsMessage());
    }
}
