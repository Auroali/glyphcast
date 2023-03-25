package com.auroali.glyphcast.common.menu;

import com.auroali.glyphcast.common.menu.container.CarvingResultsContainer;
import com.auroali.glyphcast.common.menu.container.CarvingTableContainer;
import com.auroali.glyphcast.common.menu.slots.CarvingResultSlot;
import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCMenus;
import com.auroali.glyphcast.common.registry.GCWandMaterials;
import com.auroali.glyphcast.common.wands.WandMaterial;
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

import java.util.Optional;


public class CarvingMenu extends AbstractContainerMenu {
    final Inventory inv;
    final ContainerLevelAccess access;
    final CarvingTableContainer craftSlots = new CarvingTableContainer(this, 1, 1);
    final CarvingResultsContainer resultSlots = new CarvingResultsContainer();

    public CarvingMenu(int pContainerId, Inventory inv) {
        this(pContainerId, inv, ContainerLevelAccess.NULL);
    }
    public CarvingMenu(int pContainerId, Inventory inv, ContainerLevelAccess access) {
        super(GCMenus.CARVING_TABLE.get(), pContainerId);
        this.inv = inv;
        this.access = access;

        this.addSlot(new CarvingResultSlot(inv.player, this.craftSlots, this.resultSlots, 0, 124, 35));
        this.addSlot(new Slot(this.craftSlots, 0, 48, 35));


        for(int k = 0; k < 3; ++k) {
            for(int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(inv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inv, l, 8 + l * 18, 142));
        }
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
                if (!this.moveItemStackTo(itemstack1, 3, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex >= 3 && pIndex < 39) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    if (pIndex < 29) {
                        if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 3, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 38, false)) {
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
        return stillValid(access, pPlayer, GCBlocks.CARVING_TABLE.get());
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
        if(!moveItemStackTo(slot.getItem(), 3, 38, false))
            pPlayer.drop(slot.getItem(), false);
    }

    public void inputSlotChanged(Level level, Player player, CarvingResultsContainer results, CarvingTableContainer craftSlots) {
        if(level.isClientSide)
            return;
        ServerPlayer splayer = (ServerPlayer) player;
        ItemStack stack = craftSlots.getItem(0);
        ItemStack out = ItemStack.EMPTY;
        Optional<WandMaterial> opt = GCWandMaterials.fromItem(stack);
        if(opt.isPresent()) {
            ItemStack wand = new ItemStack(GCItems.WAND.get());
            GCItems.WAND.get().setMaterial(wand, GCWandMaterials.getKey(opt.get()));
            out = wand;
        }

        results.setItem(0, out);
        this.setRemoteSlot(0, out);
        splayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0, out));
    }
}
