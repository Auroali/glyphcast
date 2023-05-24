package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.menu.CarvingMenu;
import com.auroali.glyphcast.common.menu.ScribingMenu;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;

public class GCMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Glyphcast.MODID, Registry.MENU_REGISTRY);
    public static final RegistrySupplier<MenuType<CarvingMenu>> CARVING_TABLE = MENUS.register("carving_table", () -> new MenuType<>(CarvingMenu::new));
    public static final RegistrySupplier<MenuType<ScribingMenu>> SCRIBING_TABLE = MENUS.register("scribing_table", () -> new MenuType<>(ScribingMenu::new));
}
