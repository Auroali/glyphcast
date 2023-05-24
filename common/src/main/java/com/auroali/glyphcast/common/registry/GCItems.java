package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.items.*;
import com.auroali.glyphcast.common.items.equipment.CrystalAmuletItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.Collections;

@SuppressWarnings("unused")
public class GCItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Glyphcast.MODID, Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<BlankParchmentItem> BLANK_PARCHMENT = ITEMS.register("blank_parchment", BlankParchmentItem::new);
    public static final RegistrySupplier<GlyphParchmentItem> PARCHMENT = ITEMS.register("parchment", GlyphParchmentItem::new);

    public static final RegistrySupplier<WandItem> WANDERING_WAND = ITEMS.register("wandering_wand", () -> new WandItem(Collections.emptyList()));
    public static final RegistrySupplier<WandItem> OAK_WAND = ITEMS.register("oak_wand", () -> new WandItem(Collections.emptyList()));
    public static final RegistrySupplier<WandItem> BIRCH_WAND = ITEMS.register("birch_wand", () -> new WandItem(Collections.emptyList()));
    public static final RegistrySupplier<WandItem> SPRUCE_WAND = ITEMS.register("spruce_wand", () -> new WandItem(Collections.emptyList()));
    public static final RegistrySupplier<WandItem> JUNGLE_WAND = ITEMS.register("jungle_wand", () -> new WandItem(Collections.emptyList()));
    public static final RegistrySupplier<WandItem> ACACIA_WAND = ITEMS.register("acacia_wand", () -> new WandItem(Collections.emptyList()));
    public static final RegistrySupplier<WandItem> DARK_OAK_WAND = ITEMS.register("dark_oak_wand", () -> new WandItem(Collections.emptyList()));
    public static final RegistrySupplier<WandItem> MANGROVE_WAND = ITEMS.register("mangrove_wand", () -> new WandItem(Collections.emptyList()));

    public static final RegistrySupplier<Item> ENERGY_GAUGE = ITEMS.register("energy_gauge", () -> new EnergyGaugeItem(new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB).stacksTo(1)));
    public static final RegistrySupplier<VialItem> VIAL = ITEMS.register("vial", VialItem::new);
    public static final RegistrySupplier<Item> CRYSTAL_NECKLACE = ITEMS.register("crystal_amulet", CrystalAmuletItem::new);
    public static final RegistrySupplier<Item> BLUE_GLYPH_PETAL = ITEMS.register("blue_glyph_petal", () -> new Item(new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
    public static final RegistrySupplier<Item> BLUE_GLYPH_FLOWER = ITEMS.register("blue_glyph_flower", () -> new BlockItem(GCBlocks.BLUE_GLYPH_FLOWER.get(), new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
    public static final RegistrySupplier<Item> CRYSTAL_ORE = ITEMS.register("crystal_ore", () -> new BlockItem(GCBlocks.CRYSTAL_ORE.get(), new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
    public static final RegistrySupplier<Item> DEEPSLATE_CRYSTAL_ORE = ITEMS.register("deepslate_crystal_ore", () -> new BlockItem(GCBlocks.DEEPSLATE_CRYSTAL_ORE.get(), new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
    public static final RegistrySupplier<Item> RAW_CRYSTAL = ITEMS.register("raw_crystal", () -> new Item(new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
    public static final RegistrySupplier<Item> CUT_CRYSTAL = ITEMS.register("cut_crystal", () -> new Item(new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
    public static final RegistrySupplier<Item> CUT_CRYSTAL_BLOCK = ITEMS.register("cut_crystal_block", () -> new BlockItem(GCBlocks.CUT_CRYSTAL_BLOCK.get(), new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
    public static final RegistrySupplier<Item> SCRIBING_TABLE = ITEMS.register("scribing_table", () -> new BlockItem(GCBlocks.SCRIBING_TABLE.get(), new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
    public static final RegistrySupplier<Item> CARVING_TABLE = ITEMS.register("carving_table", () -> new BlockItem(GCBlocks.CARVING_TABLE.get(), new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
    public static final RegistrySupplier<Item> FRACTURE_SIPHON = ITEMS.register("fracture_siphon", () -> new BlockItem(GCBlocks.FRACTURE_SIPHON.get(), new Item.Properties().tab(Glyphcast.GLYPHCAST_TAB)));
}
