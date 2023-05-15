package com.auroali.glyphcast.client.render;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlyphRenderer {
    public static final ResourceLocation GLYPHS = new ResourceLocation(Glyphcast.MODID, "textures/gui/glyphs/glyph_icons.png");
    public static final ResourceLocation OUTER_RING = new ResourceLocation(Glyphcast.MODID, "textures/gui/glyphs/glyph_outer_ring.png");

    private static final Map<Spell, ResourceLocation> TEXTURE_ID_CACHE = new HashMap<>();

    public static ResourceLocation getSpellPath(Spell spell) {
        if (spell == null)
            return null;

        ResourceLocation cachedTexPath = TEXTURE_ID_CACHE.get(spell);
        if (cachedTexPath != null)
            return cachedTexPath;

        ResourceLocation location = Glyphcast.SPELLS.getKey(spell).map(ResourceKey::location).orElse(null);
        if (location == null)
            return null;

        ResourceLocation tex = new ResourceLocation(location.getNamespace(), "textures/spell/" + location.getPath() + ".png");
        TEXTURE_ID_CACHE.put(spell, tex);
        return tex;
    }

    public static void drawAllGlyphs(PoseStack pPoseStack, int x, int y, List<List<Glyph>> glyphs) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GLYPHS);
        for (int i = 0; i < glyphs.size(); i++) {
            if (i == 0 && glyphs.get(i).size() > 0) {
                drawBaseGlyph(pPoseStack, x, y, glyphs.get(i).get(0));
            }
            if (i == 1) {
                GuiComponent.blit(pPoseStack, x - 20, y - 20, 256 - 40, 0, 40, 40, 256, 256);
                GuiComponent.blit(pPoseStack, x - 54, y - 54, 256 - 108, 40, 108, 108, 256, 256);
                for (int j = 0; j < glyphs.get(i).size(); j++) {
                    renderGlyphOnRing(pPoseStack, x, y, glyphs.get(i).get(j), j, glyphs.get(i).size(), 35);
                }
            }
            if (i == 2) {
                RenderSystem.setShaderTexture(0, OUTER_RING);
                GuiComponent.blit(pPoseStack, x - 88, y - 88, 0, 0, 176, 176, 256, 256);
                RenderSystem.setShaderTexture(0, GLYPHS);
                for (int j = 0; j < glyphs.get(i).size(); j++) {
                    renderGlyphOnRing(pPoseStack, x, y, glyphs.get(i).get(j), j, glyphs.get(i).size(), 70);
                }
            }
        }
    }

    public static void drawBaseGlyph(PoseStack pPoseStack, int x, int y, Glyph glyph) {
        RenderSystem.setShaderTexture(0, GLYPHS);
        int texOffsetX = 16;
        int texOffsetY = 16;
        drawGlyphIcon(pPoseStack, glyph, x - texOffsetX, y - texOffsetY);
        GuiComponent.blit(pPoseStack, x - texOffsetX, y - texOffsetY, 0, 0, 32, 32, 256, 256);
    }

    public static void drawGlyphIcon(PoseStack stack, Glyph glyph, int x, int y) {
        float f3 = (float) (glyph.color() >> 24 & 255) / 255.0F;
        float f = (float) (glyph.color() >> 16 & 255) / 255.0F;
        float f1 = (float) (glyph.color() >> 8 & 255) / 255.0F;
        float f2 = (float) (glyph.color() & 255) / 255.0F;
        RenderSystem.setShaderColor(f, f1, f2, f3);
        GuiComponent.blit(stack, x, y, 48, 32 * glyph.ordinal(), 32, 32, 256, 256);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void renderGlyphOnRing(PoseStack stack, int x, int y, Glyph glyph, int index, int maxPerRing, int size) {
        // Get the angle the glyph should be at
        double angle = (2 * Math.PI * ((double) (index + 1) / (double) maxPerRing)) - Math.PI / 3;

        // Convert the angle to screen coordinates, centered around the screen center
        int glyphX = x + (int) (size * Math.cos(angle));
        int glyphY = y + (int) (size * Math.sin(angle));

        drawGlyphIcon(stack, glyph, glyphX - 16, glyphY - 16);
        GuiComponent.blit(stack, glyphX - 16, glyphY - 16, 0, 0, 32, 32, 256, 256);
    }

    public static void drawSpell(PoseStack stack, int x, int y, Spell spell) {
        RenderSystem.setShaderTexture(0, getSpellPath(spell));
        GuiComponent.blit(stack, x, y, 0, 0, 32, 32, 32, 32);
        RenderSystem.setShaderTexture(0, GLYPHS);
        GuiComponent.blit(stack, x, y, 0, 98, 32, 32, 256, 256);
    }
}
