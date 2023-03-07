package com.auroali.glyphcast.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class GCRenderTypes {
    private static final Function<ResourceLocation, RenderType> GLOW = Util.memoize((p_173253_) -> {
        RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_173253_, false, false);
        return RenderType.create("glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTransparencyState(ADDITIVE_TRANSPARENCY).setTextureState(renderstateshard$texturestateshard).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
    });

    public static RenderType shadeless(ResourceLocation location) {
        return GLOW.apply(location);
    }
}
