package com.auroali.glyphcast.client.render.overlays;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.render.GlyphRenderer;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy;
import com.auroali.glyphcast.common.items.EnergyGaugeItem;
import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.network.server.RequestChunkEnergyMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class EnergyGaugeOverlay implements IGuiOverlay {
    public static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation(GlyphCast.MODID, "textures/gui/energy_gauge.png");

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null)
            return;

        if (!hasItemInHand(player, EnergyGaugeItem.class) && !hasItemInHand(player, IWandLike.class))
            return;

        if (hasItemInHand(player, EnergyGaugeItem.class))
            renderEnergyGaugeOverlay(gui, poseStack, player);

        if (hasItemInHand(player, IWandLike.class))
            renderWandOverlay(gui, poseStack, player);
    }

    void renderEnergyGaugeOverlay(ForgeGui gui, PoseStack poseStack, LocalPlayer player) {
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        gui.blit(poseStack, 0, 0, 0, 0, 62, 39);
//        double energy = IChunkEnergy.getEnergyAt(player.level, player.blockPosition());
//        double maxEnergy = IChunkEnergy.getMaxEnergyAt(player.level, player.blockPosition());
//        double energyPercent = Math.min(energy / 320.0, 1.0);
//        double maxEnergyPercent = Math.min(maxEnergy / 320.0, 1.0);
//        int currentEnergyNeedleY = 28 - (int) (energyPercent * 21);
//        int maxEnergyNeedleY = 28 - (int) (maxEnergyPercent * 21);
//
//        gui.blit(poseStack, 44, maxEnergyNeedleY, 62, 0, 3, 1);
//        gui.blit(poseStack, 44, currentEnergyNeedleY, 62, 1, 4, 1);
    }

    void renderWandOverlay(ForgeGui gui, PoseStack poseStack, LocalPlayer player) {
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        gui.blit(poseStack, 0, 0, 0, 39, 69, 39);
        double energy = SpellUser.get(player).map(ISpellUser::getEnergy).orElse(Double.NaN);
        double energyPercent = energy / 250.0;
        double fracturePercent = IChunkEnergy.getAverageFractureEnergy(player.level, player.blockPosition()) / 415.0;
        int currentEnergyNeedleY = 28 - (int) (energyPercent * 20);
        int fractureNeedleY = 28 - (int) (fracturePercent * 20);

        gui.blit(poseStack, 44, currentEnergyNeedleY, 62, 1, 4, 1);
        gui.blit(poseStack, 60, fractureNeedleY, 62, 2, 3, 2);

        SpellUser.get(player).ifPresent(user -> {
            if (user.getSelectedSpell() != null)
                GlyphRenderer.drawSpell(poseStack, 1, 2, user.getSelectedSpell());
        });
    }

    <T> boolean hasItemInHand(Player player, Class<T> tClass) {
        return tClass.isInstance(player.getMainHandItem().getItem()) || tClass.isInstance(player.getOffhandItem().getItem());
    }
}
