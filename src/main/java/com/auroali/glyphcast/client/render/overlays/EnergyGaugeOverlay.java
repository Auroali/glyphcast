package com.auroali.glyphcast.client.render.overlays;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.render.GlyphRenderer;
import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy;
import com.auroali.glyphcast.common.items.EnergyGaugeItem;
import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.items.WandItem;
import com.auroali.glyphcast.common.network.server.RequestChunkEnergyMessage;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.registry.tags.GCItemTags;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

        if (PlayerHelper.hasItemInHand(player, GCItemTags.ENERGY_GAUGE_OVERLAY))
            renderEnergyGaugeOverlay(gui, poseStack, player);

        if (PlayerHelper.hasItemInHand(player, GCItemTags.WAND_ENERGY_GAUGE_OVERLAY))
            renderWandOverlay(gui, poseStack, player);
    }

    void renderEnergyGaugeOverlay(ForgeGui gui, PoseStack poseStack, LocalPlayer player) {
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        gui.blit(poseStack, 0, 0, 0, 0, 62, 39);
        double energy = SpellUser.get(player).map(ISpellUser::getEnergy).orElse(Double.NaN);
        double maxEnergy = SpellUser.get(player).map(ISpellUser::getMaxEnergy).orElse(Double.NaN);
        double energyPercent = energy / maxEnergy;
        int currentEnergyNeedleY = 28 - (int) (energyPercent * 21);

        gui.blit(poseStack, 44, currentEnergyNeedleY, 62, 1, 4, 1);
    }

    void renderWandOverlay(ForgeGui gui, PoseStack poseStack, LocalPlayer player) {
        RenderSystem.setShaderTexture(0, OVERLAY_LOCATION);
        gui.blit(poseStack, 0, 0, 0, 39, 34, 82);
        double energy = SpellUser.get(player).map(ISpellUser::getEnergy).orElse(Double.NaN);
        double maxEnergy = SpellUser.get(player).map(ISpellUser::getMaxEnergy).orElse(Double.NaN);
        double energyPercent = energy / maxEnergy;
        double fracturePercent = IChunkEnergy.getAverageFractureEnergy(player.level, player.blockPosition()) / 415.0;
        int currentEnergyNeedleY = 58 - (int) (energyPercent * 22);
        int fractureNeedleY = 58 - (int) (fracturePercent * 22);

        gui.blit(poseStack, 11, currentEnergyNeedleY, 62, 4, 3, 2);
        gui.blit(poseStack, 20, fractureNeedleY, 62, 2, 3, 2);

        ItemStack overlayItem = PlayerHelper.getHeldItem(player, GCItemTags.WAND_ENERGY_GAUGE_OVERLAY);
        if(overlayItem.is(GCItems.WAND.get())) {
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
