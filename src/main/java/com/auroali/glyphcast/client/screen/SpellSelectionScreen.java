package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.screen.widgets.SpellListWidget;
import com.auroali.glyphcast.client.screen.widgets.SpellSlotButton;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import com.auroali.glyphcast.common.network.server.ClearSpellSlotMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellSlot;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;
import java.util.Optional;

// TODO: Replace with like a wheel or something, just not this
public class SpellSelectionScreen extends Screen {
    public static final ResourceLocation SELECTION_ICONS = new ResourceLocation(GlyphCast.MODID, "textures/gui/selection_screen.png");
    SpellListWidget list;
    public static void openScreen() {
        SpellSelectionScreen screen = new SpellSelectionScreen();
        Minecraft.getInstance().setScreen(screen);
    }

    protected SpellSelectionScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    protected void init() {
        this.list = new SpellListWidget(this, 128, height - 24, 0, height - 24, Minecraft.getInstance().font.lineHeight * 2 + 2);

        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> {
            list.setUser(user);
            list.setSpells(user.getDiscoveredSpells());
            int spacing = 12;
            int offset = (128 - (user.getSlots().size() * spacing)) / 2;
            for(int i = 0; i < user.getSlots().size(); i++) {
                SpellSlotButton button = new SpellSlotButton(i, user, list, offset + i * spacing, height - 14, b ->  GCNetwork.sendToServer(new ClearSpellSlotMessage(((SpellSlotButton)b).slot)));
                addRenderableWidget(button);
            }
        });

        addRenderableWidget(list);

    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderButtonTooltips(pPoseStack, pMouseX, pMouseY);
        renderListEntryTooltips(pPoseStack, pMouseX, pMouseY);
    }

    private void renderListEntryTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        SpellListWidget.SpellListEntry entry = list.getHoveredEntry();
        if(entry != null) {
            Optional<TooltipComponent> tooltipComponent = Optional.empty();
            if(!entry.spell.isSequence(GlyphSequence.EMPTY))
                tooltipComponent = Optional.of(new GlyphTooltipComponent(entry.spell.getSequence()));

            renderTooltip(pPoseStack, List.of(entry.spell.getName(), entry.spell.getSpellDescription()), tooltipComponent, pMouseX, pMouseY);
        }
    }

    private void renderButtonTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        children().stream().filter(w -> w instanceof SpellSlotButton).map(w -> (SpellSlotButton)w).forEach(button -> {
            if(button.isMouseOver(pMouseX, pMouseY)) {
                SpellSlot slot = SpellUser.get(Minecraft.getInstance().player).map(user -> user.getSlots().get(button.slot)).orElse(new SpellSlot(button.slot));
                if(slot.isEmpty())
                    return;

                Spell spell = slot.getSpell();
                renderTooltip(pPoseStack, List.of(spell.getName(), spell.getSpellDescription()), Optional.empty(), pMouseX, pMouseY);
            }
        });
    }

    @Override
    public void renderBackground(PoseStack pPoseStack) {
        super.renderBackground(pPoseStack);
        RenderSystem.setShaderTexture(0, SELECTION_ICONS);
        this.blit(pPoseStack, 0, height - 24, 0, 0, 128, 24);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
