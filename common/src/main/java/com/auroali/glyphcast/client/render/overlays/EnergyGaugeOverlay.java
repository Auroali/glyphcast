package com.auroali.glyphcast.client.render.overlays;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.render.GlyphRenderer;
import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.entities.FractureEntity;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.tags.GCItemTags;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

// TODO: Fix this
public class EnergyGaugeOverlay {
    public static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation(Glyphcast.MODID, "textures/gui/energy_gauge.png");

    public static void renderOverlay(PoseStack poseStack, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null)
            return;

        if (PlayerHelper.hasItemInHand(player, GCItemTags.ENERGY_GAUGE_OVERLAY))
            renderEnergyGaugeOverlay(poseStack, player);

        if (PlayerHelper.hasItemInHand(player, GCItemTags.WAND_ENERGY_GAUGE_OVERLAY))
            renderWandOverlay(poseStack, player);
    }

    static void renderEnergyGaugeOverlay(PoseStack poseStack, LocalPlayer player) {
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        GuiComponent.blit(poseStack, 0, 0, 0, 0, 62, 39, 256, 256);
        double energy = SpellUser.get(player).map(ISpellUser::getEnergy).orElse(Double.NaN);
        double maxEnergy = SpellUser.get(player).map(ISpellUser::getMaxEnergy).orElse(Double.NaN);
        double energyPercent = energy / maxEnergy;
        int currentEnergyNeedleY = 28 - (int) (energyPercent * 21);

        GuiComponent.blit(poseStack, 44, currentEnergyNeedleY, 62, 1, 4, 1, 256, 256);
    }

    static void renderWandOverlay(PoseStack poseStack, LocalPlayer player) {
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        GuiComponent.blit(poseStack, 0, 0, 0, 39, 34, 82, 256, 256);
        double energy = SpellUser.get(player).map(ISpellUser::getEnergy).orElse(Double.NaN);
        double maxEnergy = SpellUser.get(player).map(ISpellUser::getMaxEnergy).orElse(Double.NaN);
        double energyPercent = energy / maxEnergy;
        double fracturePercent = FractureEntity.getAverageEnergyAt(player.level, player.blockPosition(), 15.0) / FractureEntity.MAX_ENERGY;
        int currentEnergyNeedleY = 58 - (int) (energyPercent * 22);
        int fractureNeedleY = 58 - (int) (fracturePercent * 22);

        GuiComponent.blit(poseStack, 11, currentEnergyNeedleY, 62, 4, 3, 2, 256, 256);
        GuiComponent.blit(poseStack, 20, fractureNeedleY, 62, 2, 3, 2, 256, 256);

        ItemStack overlayItem = PlayerHelper.getHeldItem(player, GCItemTags.WAND_ENERGY_GAUGE_OVERLAY);
        if (overlayItem.is(GCItems.WAND.get())) {
            GCItems.WAND.get().getCore(overlayItem)
                    .ifPresent(c ->
                            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(new ItemStack(c.item()), 9, 64)
                    );
        }
        SpellUser.get(player).ifPresent(user -> {
            if (user.getSelectedSpell() != null)
                GlyphRenderer.drawSpell(poseStack, 1, 2, user.getSelectedSpell());
        });
    }
}
