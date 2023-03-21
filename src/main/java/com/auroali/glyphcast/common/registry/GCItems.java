package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.items.BlankParchmentItem;
import com.auroali.glyphcast.common.items.GlyphParchmentItem;
import com.auroali.glyphcast.common.items.WandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class GCItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GlyphCast.MODID);
    public static final RegistryObject<WandItem> WAND = ITEMS.register("wandering_wand", WandItem::new);

    public static final RegistryObject<BlankParchmentItem> BLANK_PARCHMENT = ITEMS.register("blank_parchment", BlankParchmentItem::new);
    public static final RegistryObject<GlyphParchmentItem> PARCHMENT = ITEMS.register("parchment", GlyphParchmentItem::new);
    public static final RegistryObject<Item> BLUE_GLYPH_PETAL = ITEMS.register("blue_glyph_petal", () -> new Item(new Item.Properties().tab(GlyphCast.GLYPHCAST_TAB)));
    public static final RegistryObject<Item> BLUE_GLYPH_FLOWER = ITEMS.register("blue_glyph_flower", () -> new BlockItem(GCBlocks.BLUE_GLYPH_FLOWER.get(), new Item.Properties().tab(GlyphCast.GLYPHCAST_TAB)));
    public static final RegistryObject<Item> RAW_CRYSTAL = ITEMS.register("raw_crystal", () -> new Item(new Item.Properties().tab(GlyphCast.GLYPHCAST_TAB)));
    public static final RegistryObject<Item> CUT_CRYSTAL = ITEMS.register("cut_crystal", () -> new Item(new Item.Properties().tab(GlyphCast.GLYPHCAST_TAB)));
    public static final RegistryObject<Item> CRYSTAL_ORE = ITEMS.register("crystal_ore", () -> new BlockItem(GCBlocks.CRYSTAL_ORE.get(), new Item.Properties().tab(GlyphCast.GLYPHCAST_TAB)));
    public static final RegistryObject<Item> DEEPSLATE_CRYSTAL_ORE = ITEMS.register("deepslate_crystal_ore", () -> new BlockItem(GCBlocks.DEEPSLATE_CRYSTAL_ORE.get(), new Item.Properties().tab(GlyphCast.GLYPHCAST_TAB)));
}
