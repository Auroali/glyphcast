package com.auroali.glyphcast.client;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.model.DynamicStaffModel;
import com.auroali.glyphcast.client.model.DynamicWandModel;
import com.auroali.glyphcast.client.model.FloatingLightModel;
import com.auroali.glyphcast.client.model.entity.StaffCatModel;
import com.auroali.glyphcast.client.particles.MagicAmbienceProvider;
import com.auroali.glyphcast.client.particles.MagicPulseProvider;
import com.auroali.glyphcast.client.render.GlyphClientTooltipComponent;
import com.auroali.glyphcast.client.render.entity.EmptyEntityRenderer;
import com.auroali.glyphcast.client.render.entity.LightEntityRenderer;
import com.auroali.glyphcast.client.render.entity.StaffEntityRenderer;
import com.auroali.glyphcast.client.render.overlays.EnergyGaugeOverlay;
import com.auroali.glyphcast.client.screen.CarvingTableScreen;
import com.auroali.glyphcast.common.items.StaffItem;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import com.auroali.glyphcast.common.registry.GCEntities;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCMenus;
import com.auroali.glyphcast.common.registry.GCParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GlyphCast.MODID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    public static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(GCItems.VIAL.get(), new ResourceLocation(GlyphCast.MODID, "vial_level"), (stack, level, entity, j) -> (float) (stack.getOrCreateTag().getDouble("Amount") / 250.0));
            MenuScreens.register(GCMenus.CARVING_TABLE.get(), CarvingTableScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GCEntities.FIRE.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(GCEntities.FLARE.get(), EmptyEntityRenderer::new);
        event.registerEntityRenderer(GCEntities.FLOATING_LIGHT.get(), LightEntityRenderer::new);
        event.registerEntityRenderer(GCEntities.STAFF_ENTITY.get(), StaffEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerModels(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FloatingLightModel.LAYER_LOCATION, FloatingLightModel::createBodyLayer);
        event.registerLayerDefinition(StaffCatModel.LAYER_LOCATION, StaffCatModel::createBodyLayer);
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
        event.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), "energy_gauge", new EnergyGaugeOverlay());
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.register(GCParticles.MAGIC_AMBIENCE.get(), MagicAmbienceProvider::new);
        event.register(GCParticles.MAGIC_PULSE.get(), MagicPulseProvider::new);
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("wand", new DynamicWandModel.Loader());
        event.register("staff", new DynamicStaffModel.Loader());
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onPreStitch(TextureStitchEvent.Pre event) {
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
        event.addSprite(new ResourceLocation(GlyphCast.MODID, "item/staff/empty"));
        for (StaffItem.Variant variant : StaffItem.VARIANTS) {
            event.addSprite(new ResourceLocation(GlyphCast.MODID, "item/staff/" + variant.name()));
            event.addSprite(new ResourceLocation(GlyphCast.MODID, "item/staff/" + variant.name() + "_active"));
            event.addSprite(new ResourceLocation(GlyphCast.MODID, "item/staff/" + variant.name() + "_empty"));
        }
    }


}
