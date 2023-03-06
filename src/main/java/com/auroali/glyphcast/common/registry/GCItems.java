package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.items.GlyphParchmentItem;
import com.auroali.glyphcast.common.items.WandItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GCItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GlyphCast.MODID);
    public static final RegistryObject<Item> WAND = ITEMS.register("wandering_wand", WandItem::new);

    public static final RegistryObject<Item> BLANK_PARCHMENT = ITEMS.register("blank_parchment", () -> new Item(new Item.Properties().stacksTo(16).tab(GlyphCast.GLYPHCAST_TAB)));
    public static final RegistryObject<Item> PARCHMENT = ITEMS.register("parchment", GlyphParchmentItem::new);

}
