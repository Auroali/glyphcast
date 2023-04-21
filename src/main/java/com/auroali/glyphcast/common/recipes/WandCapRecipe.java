package com.auroali.glyphcast.common.recipes;

import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCRecipesSerializers;
import com.auroali.glyphcast.common.registry.GCWandCaps;
import com.auroali.glyphcast.common.wands.WandCap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class WandCapRecipe extends UpgradeRecipe {
    public static final Ingredient WAND_INGREDIENT = Ingredient.of(GCItems.WAND.get());

    public WandCapRecipe(ResourceLocation pId) {
        super(pId, WAND_INGREDIENT, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (!WAND_INGREDIENT.test(pContainer.getItem(0)))
            return false;
        Optional<WandCap> originalCap = GCItems.WAND.get().getCap(pContainer.getItem(0));
        Optional<WandCap> newCap = GCWandCaps.fromItem(pContainer.getItem(1));
        return originalCap.isEmpty() && newCap.isPresent();
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        ItemStack stack = pContainer.getItem(0).copy();
        Optional<WandCap> newCap = GCWandCaps.fromItem(pContainer.getItem(1));
        newCap.ifPresent(cap -> GCItems.WAND.get().setCap(stack, GCWandCaps.getKey(cap)));
        return stack;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GCRecipesSerializers.WAND_CAP.get();
    }
}
