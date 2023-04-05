package com.auroali.glyphcast.client.render.overlays;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class EnergyGaugeOverlay implements IGuiOverlay {
    public static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation(GlyphCast.MODID, "textures/gui/energy_gauge.png");

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null)
            return;

        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        gui.blit(poseStack, 0, 0, 0, 0, 61, 38);
        double energy = IChunkEnergy.getEnergyAt(player.level, player.blockPosition());
        double maxEnergy = IChunkEnergy.getMaxEnergyAt(player.level, player.blockPosition());
        double energyPercent = (energy / maxEnergy);
        int currentEnergyNeedleY = 29 - (int)(energyPercent * 23);

        gui.blit(poseStack, 44, currentEnergyNeedleY, 62, 0, 4, 2);
    }
}
