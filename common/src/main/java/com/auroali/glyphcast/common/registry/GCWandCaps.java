package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.client.SyncWandCapsMessage;
import com.auroali.glyphcast.common.wands.WandCap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GCWandCaps {
    public static final Map<ResourceLocation, WandCap> KEY_MAP = new HashMap<>();
    public static final Map<WandCap, ResourceLocation> VALUE_MAP = new HashMap<>();

    public static WandCap getValue(ResourceLocation location) {
        return KEY_MAP.get(location);
    }

    public static ResourceLocation getKey(WandCap core) {
        return VALUE_MAP.get(core);
    }

    public static void syncToClients() {
        GCNetwork.CHANNEL.sendToAll(new SyncWandCapsMessage());
    }

    public static Optional<WandCap> fromItem(ItemStack stack) {
        return KEY_MAP.values().stream().filter(m -> m.validMaterials().stream().anyMatch(s -> s.map(stack::is, stack::is))).findFirst();
    }
}
