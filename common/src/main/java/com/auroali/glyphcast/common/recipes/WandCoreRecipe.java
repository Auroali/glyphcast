package com.auroali.glyphcast.common.recipes;

import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCRecipesSerializers;
import com.auroali.glyphcast.common.registry.GCWandCores;
import com.auroali.glyphcast.common.registry.tags.GCItemTags;
import com.auroali.glyphcast.common.wands.WandCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WandCoreRecipe extends CustomRecipe {
    public WandCoreRecipe(ResourceLocation pId) {
        super(pId);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        int numItems = 0;
        boolean hasWand = false;
        boolean hasCore = false;
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack stack = pContainer.getItem(i);
            if (!stack.isEmpty())
                numItems++;
            WandCore core = GCWandCores.fromItem(stack).orElse(null);
            if (core != null)
                hasCore = true;
            if (stack.getItem() instanceof IWandLike wandLike && wandLike.getCore(stack).isEmpty())
                hasWand = true;
        }
        return numItems == 2 && hasWand && hasCore;
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer) {
        ItemStack stack = ItemStack.EMPTY;
        for(int i = 0; i < pContainer.getContainerSize(); i++) {
            if(pContainer.getItem(i).getItem() instanceof IWandLike)
                stack = pContainer.getItem(i);
        }
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack slot = pContainer.getItem(i);
            if (slot.isEmpty())
                continue;
            if(stack.getItem() instanceof IWandLike wandLike) {
                GCWandCores.fromItem(slot).ifPresent(core -> wandLike.setCore(slot, GCWandCores.getKey(core)));
            }
        }
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GCRecipesSerializers.WAND_CORE.get();
    }
}
