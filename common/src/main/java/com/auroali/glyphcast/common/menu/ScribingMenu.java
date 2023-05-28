package com.auroali.glyphcast.common.menu;

import com.auroali.glyphcast.common.menu.container.InputContainer;
import com.auroali.glyphcast.common.menu.container.ScribingResultsContainer;
import com.auroali.glyphcast.common.menu.slots.ScribingResultSlot;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.client.menu.SendScribingGlyphListMessage;
import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCMenus;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;


public class ScribingMenu extends AbstractContainerMenu {
    final Inventory inv;
    final ContainerLevelAccess access;
    final InputContainer craftSlots = new InputContainer(this, 1, 1);
    final ScribingResultsContainer resultSlots = new ScribingResultsContainer();
    final List<List<Glyph>> glyphs;

    public ScribingMenu(int pContainerId, Inventory inv) {
        this(pContainerId, inv, ContainerLevelAccess.NULL);
    }

    public ScribingMenu(int pContainerId, Inventory inv, ContainerLevelAccess access) {
        super(GCMenus.SCRIBING_TABLE.get(), pContainerId);
        this.inv = inv;
        this.access = access;

        this.addSlot(new ScribingResultSlot(inv.player, this.craftSlots, this.resultSlots, 0, 192, 77));
        this.addSlot(new Slot(this.craftSlots, 0, 13, 77));


        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inv, l, 29 + l * 18, 172));
        }

        this.glyphs = new ArrayList<>();
        this.glyphs.add(new ArrayList<>());
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 0) {
                this.access.execute((p_39378_, p_39379_) ->
                        itemstack1.getItem().onCraftedBy(itemstack1, p_39378_, pPlayer)
                );
                if (!this.moveItemStackTo(itemstack1, 2, 11, false)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex >= 2 && pIndex < 11) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    if (!this.moveItemStackTo(itemstack1, 2, 11, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 2, 11, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
            if (pIndex == 0) {
                pPlayer.drop(itemstack1, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, GCBlocks.SCRIBING_TABLE.get());
    }

    @Override
    public void slotsChanged(Container pContainer) {
        this.access.execute((level, pos) ->
                inputSlotChanged(level, inv.player, resultSlots, craftSlots)
        );
    }

    @Override
    public void removed(Player pPlayer) {
        Slot slot = getSlot(1);
        if (!moveItemStackTo(slot.getItem(), 2, 11, false))
            pPlayer.drop(slot.getItem(), false);
    }

    public void inputSlotChanged(Level level, Player player, ScribingResultsContainer results, InputContainer craftSlots) {
        if (level.isClientSide)
            return;

        ServerPlayer serverPlayer = (ServerPlayer) player;

        if(glyphs.stream().allMatch(List::isEmpty) || !craftSlots.getItem(0).is(GCItems.BLANK_PARCHMENT.get())) {
            results.setItem(0, ItemStack.EMPTY);
            this.setRemoteSlot(0, ItemStack.EMPTY);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0, ItemStack.EMPTY));
            return;
        }

        ItemStack stack = GCItems.PARCHMENT.get().withGlyphSequence(new GlyphSequence(
                glyphs.stream().map(Ring::of).toList(),
                glyphs.stream().map(Ring::of).toList()
        ));
        results.setItem(0, stack);
        this.setRemoteSlot(0, stack);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0, stack));
    }

    int getMaxPerRing() {
        return switch (glyphs.size()) {
            case 1 -> 1;
            case 2 -> 6;
            case 3 -> 11;
            default -> 0;
        };
    }

    public void addGlyph(Glyph glyph) {
        if(glyph == Glyph.WAND) {
            if(glyphs.size() < 3) {
                glyphs.add(new ArrayList<>());
                sendGlyphsToClient();
                access.execute((level, pos) -> inputSlotChanged(level, inv.player, resultSlots, craftSlots));
            }
            return;
        }
        if(glyphs.get(glyphs.size() - 1).size() >= getMaxPerRing())
            return;

        glyphs.get(glyphs.size() - 1).add(glyph);

        sendGlyphsToClient();
        access.execute((level, pos) -> inputSlotChanged(level, inv.player, resultSlots, craftSlots));
    }

    public void eraseLast() {
        int numRings = glyphs.size();
        int numGlyphs = glyphs.get(numRings - 1).size();
        if(numGlyphs == 0 && numRings != 1) {
            glyphs.remove(glyphs.size() - 1);
            sendGlyphsToClient();
            access.execute((level, pos) -> inputSlotChanged(level, inv.player, resultSlots, craftSlots));
            return;
        }

        if(glyphs.get(numRings - 1).size() > 0) {
            glyphs.get(numRings - 1).remove(numGlyphs - 1);
            sendGlyphsToClient();
            access.execute((level, pos) -> inputSlotChanged(level, inv.player, resultSlots, craftSlots));
        }
    }
    private void sendGlyphsToClient() {
        if(inv.player instanceof ServerPlayer player)
            GCNetwork.CHANNEL.sendToPlayer(player, new SendScribingGlyphListMessage(containerId, glyphs));
    }
}
