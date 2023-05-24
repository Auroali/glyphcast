package com.auroali.glyphcast.fabric;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.particles.FractureProvider;
import com.auroali.glyphcast.client.particles.MagicAmbienceProvider;
import com.auroali.glyphcast.client.particles.MagicDripProvider;
import com.auroali.glyphcast.client.particles.MagicPulseProvider;
import com.auroali.glyphcast.client.render.GlyphClientTooltipComponent;
import com.auroali.glyphcast.client.render.overlays.EnergyGaugeOverlay;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import com.auroali.glyphcast.common.registry.GCParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class GlyphcastFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Glyphcast.initClient();

        ParticleFactoryRegistry.getInstance().register(GCParticles.FRACTURE.get(), FractureProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticles.MAGIC_DRIP.get(), MagicDripProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticles.MAGIC_PULSE.get(), MagicPulseProvider::new);
        ParticleFactoryRegistry.getInstance().register(GCParticles.MAGIC_AMBIENCE.get(), MagicAmbienceProvider::new);


        TooltipComponentCallback.EVENT.register(c -> {
            if (c instanceof GlyphTooltipComponent)
                return new GlyphClientTooltipComponent(((GlyphTooltipComponent) c).sequence());
            return null;
        });



        HudRenderCallback.EVENT.register(EnergyGaugeOverlay::renderOverlay);

        /*ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> (id, provider) -> {
            if (id.equals(new ModelResourceLocation(Glyphcast.MODID, "wand", "inventory")))
                return new WandUnbakedModel(null, null);
            return null;
        });*/
    }
}
