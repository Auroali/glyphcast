package com.auroali.glyphcast.client;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.model.FloatingLightModel;
import com.auroali.glyphcast.client.render.GlyphClientTooltipComponent;
import com.auroali.glyphcast.client.render.entity.EmptyEntityRenderer;
import com.auroali.glyphcast.client.render.entity.LightEntityRenderer;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import com.auroali.glyphcast.common.registry.GCEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GlyphCast.MODID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GCEntities.FIRE.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(GCEntities.FLARE.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(GCEntities.FLOATING_LIGHT.get(), LightEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerModels(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FloatingLightModel.LAYER_LOCATION, FloatingLightModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(GlyphTooltipComponent.class, (c) -> new GlyphClientTooltipComponent(c.sequence()));
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(GCKeybinds.SPELL_SELECTION);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        //event.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), "wand_overlay", new WandOverlay());
    }
}
