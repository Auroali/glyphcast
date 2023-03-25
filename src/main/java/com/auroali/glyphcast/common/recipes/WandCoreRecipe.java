package com.auroali.glyphcast.common.recipes;

import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCRecipes;
import com.auroali.glyphcast.common.registry.GCWandCores;
import com.auroali.glyphcast.common.registry.GCWandMaterials;
import com.auroali.glyphcast.common.wands.WandCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WandCoreRecipe extends CustomRecipe {
    public static final Ingredient WAND_INGREDIENT = Ingredient.of(GCItems.WAND.get());
    public WandCoreRecipe(ResourceLocation pId) {
        super(pId);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        int numItems = 0;
        boolean hasWand = false;
        boolean hasCore = false;
        for(int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack stack = pContainer.getItem(i);
            if(!stack.isEmpty())
                numItems++;
            WandCore core = GCWandCores.fromItem(stack).orElse(null);
            if(core != null)
                hasCore = true;
            if(WAND_INGREDIENT.test(stack) && GCItems.WAND.get().getCap(stack).isEmpty() && GCItems.WAND.get().getCore(stack).isEmpty())
                hasWand = true;
        }
        return numItems == 2 && hasWand && hasCore;
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer) {
        ItemStack stack = new ItemStack(GCItems.WAND.get());
        for(int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack slot = pContainer.getItem(i);
            if(stack.isEmpty())
                continue;
            GCWandCores.fromItem(slot).ifPresent(core -> GCItems.WAND.get().setCore(stack, GCWandCores.getKey(core)));
            if(WAND_INGREDIENT.test(slot))
                GCItems.WAND.get().getMaterial(slot).ifPresent(mat -> GCItems.WAND.get().setMaterial(stack, GCWandMaterials.getKey(mat)));
        }
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GCRecipes.WAND_CORE.get();
    }
}
