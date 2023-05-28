package com.auroali.glyphcast.fabric;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.render.GlyphClientTooltipComponent;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class GlyphcastFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Glyphcast.initClient();
        Glyphcast.postClient();


        TooltipComponentCallback.EVENT.register(c -> {
            if (c instanceof GlyphTooltipComponent)
                return new GlyphClientTooltipComponent(((GlyphTooltipComponent) c).sequence());
            return null;
        });

        /*ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> (id, provider) -> {
            if (id.equals(new ModelResourceLocation(Glyphcast.MODID, "wand", "inventory")))
                return new WandUnbakedModel(null, null);
            return null;
        });*/
    }
}
