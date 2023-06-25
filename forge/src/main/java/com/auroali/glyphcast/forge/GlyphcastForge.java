package com.auroali.glyphcast.forge;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.forge.CommonEvents;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod(Glyphcast.MODID)
public class GlyphcastForge {
    public GlyphcastForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Glyphcast.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::setup);
        modBus.addListener(this::setupClient);
        modBus.addListener(this::enqueueIMC);

        MinecraftForge.EVENT_BUS.register(CommonEvents.class);

        Glyphcast.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Glyphcast::initClient);
    }

    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Glyphcast::postClient);
    }

    public void setupClient(final FMLClientSetupEvent event) {
        event.enqueueWork(Glyphcast::postClient);
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
    }
}
