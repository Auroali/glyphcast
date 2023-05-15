package com.auroali.glyphcast.fabric;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.model.FloatingLightModel;
import com.auroali.glyphcast.client.model.entity.StaffCatModel;
import com.auroali.glyphcast.client.particles.FractureProvider;
import com.auroali.glyphcast.client.particles.MagicAmbienceProvider;
import com.auroali.glyphcast.client.particles.MagicDripProvider;
import com.auroali.glyphcast.client.particles.MagicPulseProvider;
import com.auroali.glyphcast.client.render.GlyphClientTooltipComponent;
import com.auroali.glyphcast.client.render.entity.EmptyEntityRenderer;
import com.auroali.glyphcast.client.render.entity.LightEntityRenderer;
import com.auroali.glyphcast.client.render.entity.StaffEntityRenderer;
import com.auroali.glyphcast.common.items.StaffItem;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import com.auroali.glyphcast.common.registry.GCEntities;
import com.auroali.glyphcast.common.registry.GCParticles;
import com.auroali.glyphcast.common.registry.listeners.WandCapReloadListener;
import com.auroali.glyphcast.common.registry.listeners.WandCoreReloadListener;
import com.auroali.glyphcast.common.registry.listeners.WandMaterialReloadListener;
import com.google.gson.Gson;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Set;

public class GlyphcastFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Glyphcast.initClient();

        ParticleFactoryRegistry.getInstance().register(GCParticles.FRACTURE.get(), FractureProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticles.MAGIC_DRIP.get(), MagicDripProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticles.MAGIC_PULSE.get(), MagicPulseProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticles.MAGIC_AMBIENCE.get(), MagicAmbienceProvider::new);

        EntityModelLayerRegistry.register(StaffCatModel.LAYER_LOCATION, StaffCatModel::createBodyLayer);
        EntityModelLayerRegistry.register(FloatingLightModel.LAYER_LOCATION, FloatingLightModel::createBodyLayer);

        EntityRendererRegistry.register(GCEntities.STAFF_ENTITY.get(), StaffEntityRenderer::new);
        EntityRendererRegistry.register(GCEntities.FIRE.get(), EmptyEntityRenderer::new);
        EntityRendererRegistry.register(GCEntities.FLARE.get(), EmptyEntityRenderer::new);
        EntityRendererRegistry.register(GCEntities.FLOATING_LIGHT.get(), LightEntityRenderer::new);

        TooltipComponentCallback.EVENT.register(c -> {
            if(c instanceof GlyphTooltipComponent)
                return new GlyphClientTooltipComponent(((GlyphTooltipComponent) c).sequence());
            return null;
        });

        // TODO: Custom item models for wands and stuff

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new WandCapReloadListener(new Gson(), "wands/cap"));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new WandCoreReloadListener(new Gson(), "wands/core"));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new WandMaterialReloadListener(new Gson(), "wands/material"));
    }
}
