package com.auroali.glyphcast.compat.jei;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCWandMaterials;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

@JeiPlugin
public class GlyphcastJEIPlugin implements IModPlugin {
    InfuseRecipeCategory infuseCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(GlyphCast.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                infuseCategory = new InfuseRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        GlyphCastRecipes recipes = new GlyphCastRecipes(registration.getIngredientManager());
        registration.addRecipes(GCRecipeTypes.INFUSE, recipes.getInfuseRecipes(infuseCategory));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        GCWandMaterials.KEY_MAP.keySet().forEach(mat -> {
            ItemStack wand = new ItemStack(GCItems.WAND.get());
            GCItems.WAND.get().setCap(wand, new ResourceLocation(GlyphCast.MODID, "iron"));
            GCItems.WAND.get().setMaterial(wand, mat);
            GCItems.WAND.get().setCore(wand, new ResourceLocation(GlyphCast.MODID, "petal"));
            registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, wand, GCRecipeTypes.INFUSE);
        });
    }
}
