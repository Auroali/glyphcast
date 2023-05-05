package com.auroali.glyphcast.common.registry.tags;

import com.auroali.glyphcast.GlyphCast;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class GCItemTags {
    public static final TagKey<Item> ENERGY_GAUGE_OVERLAY = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation(GlyphCast.MODID, "shows_gauge_energy_overlay"));
    public static final TagKey<Item> WAND_ENERGY_GAUGE_OVERLAY = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation(GlyphCast.MODID, "shows_wand_energy_overlay"));
}
