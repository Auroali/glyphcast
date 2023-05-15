package com.auroali.glyphcast.forge;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.registry.GCOres;
import com.auroali.glyphcast.common.registry.listeners.WandCapReloadListener;
import com.auroali.glyphcast.common.registry.listeners.WandCoreReloadListener;
import com.auroali.glyphcast.common.registry.listeners.WandMaterialReloadListener;
import com.google.gson.Gson;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Glyphcast.MODID)
public class GlyphcastForge {
    public GlyphcastForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Glyphcast.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::setup);

        MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);

        Glyphcast.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Glyphcast::initClient);
    }

    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GCOres.init();
        });
    }

    public void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new WandMaterialReloadListener(new Gson(), "wands/material"));
        event.addListener(new WandCoreReloadListener(new Gson(), "wands/core"));
        event.addListener(new WandCapReloadListener(new Gson(), "wands/cap"));
    }
}
