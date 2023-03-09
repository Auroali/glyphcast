package com.auroali.glyphcast.client.screen;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.screen.widgets.SpellListWidget;
import com.auroali.glyphcast.client.screen.widgets.SpellSlotButton;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.items.tooltip.GlyphTooltipComponent;
import com.auroali.glyphcast.common.network.server.ClearSpellSlotMessage;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.SpellSlot;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;
import java.util.Optional;

public class SpellSelectionScreen extends Screen {
    public static final ResourceLocation SELECTION_ICONS = new ResourceLocation(GlyphCast.MODID, "textures/gui/selection_screen.png");
    SpellListWidget list;
    public static void openScreen() {
        SpellSelectionScreen screen = new SpellSelectionScreen();
        Minecraft.getInstance().setScreen(screen);
    }

    protected SpellSelectionScreen() {
        super(Component.literal("test"));
    }

    @Override
    protected void init() {
        this.list = new SpellListWidget(this, width / 4, height, 0, height, Minecraft.getInstance().font.lineHeight * 2 + 2);

        SpellUser.get(Minecraft.getInstance().player).ifPresent(user -> {
            int center = height / 2;
            int padding = 2;
            list.setSpells(user.getDiscoveredSpells());
            SpellSlotButton slot1 = new SpellSlotButton(0, user, list, width / 4 + 4, center - (32 + padding * 2), b -> selectSpell(0));
            SpellSlotButton slot2 = new SpellSlotButton(1, user, list, width / 4 + 4, center - (16 + padding / 2), b -> selectSpell(1));
            SpellSlotButton slot3 = new SpellSlotButton(2, user, list, width / 4 + 4, center + padding / 2, b -> selectSpell(2));
            SpellSlotButton slot4 = new SpellSlotButton(3, user, list, width / 4 + 4, center + (16 + padding * 2), b -> selectSpell(3));
            addRenderableWidget(slot1);
            addRenderableWidget(slot2);
            addRenderableWidget(slot3);
            addRenderableWidget(slot4);
        });

        addRenderableWidget(list);

    }

    void selectSpell(int slot) {
        SpellListWidget.SpellListEntry entry = list.getSelected();
        if(entry == null) {
            GCNetwork.sendToServer(new ClearSpellSlotMessage(slot));
            return;
        }

        GCNetwork.sendToServer(new SetSlotSpellMessage(slot, entry.spell));
        list.setSelected(null);
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
        this.blit(pPoseStack, width / 4, (height - 96)/ 2, 0, 0, 24, 96);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
