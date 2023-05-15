package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.menu.CarvingMenu;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;

public class GCMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Glyphcast.MODID, Registry.MENU_REGISTRY);
    public static final RegistrySupplier<MenuType<CarvingMenu>> CARVING_TABLE = MENUS.register("carving_table", () -> new MenuType<>(CarvingMenu::new));
}
