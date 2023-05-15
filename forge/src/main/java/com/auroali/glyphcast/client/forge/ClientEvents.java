package com.auroali.glyphcast.client.forge;

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
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Glyphcast.MODID)
public class ClientEvents {
    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterGeometryLoaders event) {
        event.register("wand", new DynamicWandModel.Loader());
        event.register("staff", new DynamicStaffModel.Loader());
    }

    @SubscribeEvent
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        event.register(GCParticles.FRACTURE.get(), FractureProvider::new);
        event.register(GCParticles.MAGIC_AMBIENCE.get(), MagicAmbienceProvider::new);
        event.register(GCParticles.MAGIC_PULSE.get(), MagicPulseProvider::new);
        event.register(GCParticles.MAGIC_DRIP.get(), MagicDripProvider::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GCEntities.FIRE.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(GCEntities.FLARE.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(GCEntities.FLOATING_LIGHT.get(), LightEntityRenderer::new);
        event.registerEntityRenderer(GCEntities.STAFF_ENTITY.get(), StaffEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FloatingLightModel.LAYER_LOCATION, FloatingLightModel::createBodyLayer);
        event.registerLayerDefinition(StaffCatModel.LAYER_LOCATION, StaffCatModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(GlyphTooltipComponent.class, (c) -> new GlyphClientTooltipComponent(c.sequence()));
    }

    @SubscribeEvent
    public static void textureStitchEven(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS))
            return;
        Minecraft.getInstance().getResourceManager().listResources("textures/item/wand", p -> p.getPath().endsWith(".png")).forEach((l, r) -> {
            ResourceLocation sprite = new ResourceLocation(l.getNamespace(), l.getPath().substring(9, l.getPath().length() - 4));
            event.addSprite(sprite);
        });
        Minecraft.getInstance().getResourceManager().listResources("textures/item/wand_cap", p -> p.getPath().endsWith(".png")).forEach((l, r) -> {
            ResourceLocation sprite = new ResourceLocation(l.getNamespace(), l.getPath().substring(9, l.getPath().length() - 4));
            event.addSprite(sprite);
        });
        event.addSprite(new ResourceLocation(Glyphcast.MODID, "item/staff/empty"));
        for (StaffItem.Variant variant : StaffItem.VARIANTS) {
            event.addSprite(new ResourceLocation(Glyphcast.MODID, "item/staff/" + variant.name()));
            event.addSprite(new ResourceLocation(Glyphcast.MODID, "item/staff/" + variant.name() + "_active"));
            event.addSprite(new ResourceLocation(Glyphcast.MODID, "item/staff/" + variant.name() + "_empty"));
        }
    }
}
