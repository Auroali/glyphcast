package com.auroali.glyphcast.compat.jei;

import com.auroali.glyphcast.common.recipes.CarvingRecipe;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import com.auroali.glyphcast.common.registry.GCRecipeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

public class GlyphcastRecipes {
    private final RecipeManager recipeManager;
    private final IIngredientManager ingredientManager;

    public GlyphcastRecipes(IIngredientManager ingredientManager) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel world = minecraft.level;
        this.recipeManager = world.getRecipeManager();
        this.ingredientManager = ingredientManager;
    }

    public List<InfuseRecipe> getInfuseRecipes(IRecipeCategory<InfuseRecipe> category) {
        return recipeManager.getAllRecipesFor(GCRecipeTypes.INFUSE_RECIPE.get())
                .stream()
                .filter(category::isHandled)
                .toList();
    }

    public List<CarvingRecipe> getCarvingRecipes(IRecipeCategory<CarvingRecipe> category) {
        return recipeManager.getAllRecipesFor(GCRecipeTypes.CARVING_RECIPE.get())
                .stream()
                .filter(category::isHandled)
                .toList();
    }

}
