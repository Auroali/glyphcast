package com.auroali.glyphcast.common.registry.tags;

import com.auroali.glyphcast.Glyphcast;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class GCItemTags {
    public static final TagKey<Item> ENERGY_GAUGE_OVERLAY = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Glyphcast.MODID, "shows_gauge_energy_overlay"));
    public static final TagKey<Item> WAND_ENERGY_GAUGE_OVERLAY = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Glyphcast.MODID, "shows_wand_energy_overlay"));
    public static final TagKey<Item> POINT_ON_USE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Glyphcast.MODID, "point_on_use"));
    public static final TagKey<Item> WANDS = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Glyphcast.MODID, "wands"));
}
