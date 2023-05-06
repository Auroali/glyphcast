package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.common.network.client.SyncWandCoresMessage;
import com.auroali.glyphcast.common.wands.WandCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GCWandCores {
    public static final Map<ResourceLocation, WandCore> KEY_MAP = new HashMap<>();
    public static final Map<WandCore, ResourceLocation> VALUE_MAP = new HashMap<>();

    public static WandCore getValue(ResourceLocation location) {
        return KEY_MAP.get(location);
    }

    public static ResourceLocation getKey(WandCore core) {
        return VALUE_MAP.get(core);
    }

    public static void syncToClients() {
        GCNetwork.sendToAll(new SyncWandCoresMessage());
    }

    public static Optional<WandCore> fromItem(ItemStack stack) {
        return KEY_MAP.values().stream().filter(m -> m.item().equals(stack.getItem())).findFirst();
    }
}
