package com.auroali.glyphcast.client.forge;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.particles.FractureProvider;
import com.auroali.glyphcast.client.particles.MagicAmbienceProvider;
import com.auroali.glyphcast.client.particles.MagicDripProvider;
import com.auroali.glyphcast.client.particles.MagicPulseProvider;
import com.auroali.glyphcast.client.render.GlyphClientTooltipComponent;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import com.auroali.glyphcast.common.registry.GCParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Glyphcast.MODID)
public class ClientEvents {
    @SubscribeEvent
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        event.register(GCParticles.FRACTURE.get(), FractureProvider::new);
        event.register(GCParticles.MAGIC_AMBIENCE.get(), MagicAmbienceProvider::new);
        event.register(GCParticles.MAGIC_PULSE.get(), MagicPulseProvider::new);
        event.register(GCParticles.MAGIC_DRIP.get(), MagicDripProvider::new);
    }

    @SubscribeEvent
    public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(GlyphTooltipComponent.class, (c) -> new GlyphClientTooltipComponent(c.sequence()));
    }
}
