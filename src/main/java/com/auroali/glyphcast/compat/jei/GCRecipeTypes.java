package com.auroali.glyphcast.compat.jei;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.recipes.CarvingRecipe;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import mezz.jei.api.recipe.RecipeType;

public class GCRecipeTypes {
    public static RecipeType<InfuseRecipe> INFUSE = RecipeType.create(GlyphCast.MODID, "infuse", InfuseRecipe.class);
    public static RecipeType<CarvingRecipe> CARVING = RecipeType.create(GlyphCast.MODID, "carving", CarvingRecipe.class);
}
