package com.auroali.glyphcast.client;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.model.FloatingLightModel;
import com.auroali.glyphcast.client.render.entity.FireEntityRenderer;
import com.auroali.glyphcast.client.render.entity.LightEntityRenderer;
import com.auroali.glyphcast.common.registry.GCEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GlyphCast.MODID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {
    @SubscribeEvent
    public static void registerRenderes(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GCEntities.FIRE.get(), FireEntityRenderer::new);
        event.registerEntityRenderer(GCEntities.FLOATING_LIGHT.get(), LightEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerModels(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FloatingLightModel.LAYER_LOCATION, FloatingLightModel::createBodyLayer);
    }

    public static void registerTooltipComponents() {

    }
}
