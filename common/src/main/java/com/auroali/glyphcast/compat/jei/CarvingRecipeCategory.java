package com.auroali.glyphcast.compat.jei;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.recipes.CarvingRecipe;
import com.auroali.glyphcast.common.registry.GCBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CarvingRecipeCategory implements IRecipeCategory<CarvingRecipe> {
    public static final int width = 118;
    public static final int height = 59;

    Component name;
    IDrawable background;
    IDrawable icon;

    public CarvingRecipeCategory(IGuiHelper helper) {
        name = Component.translatable("gui.glyphcast.category.carving");
        ResourceLocation location = new ResourceLocation(Glyphcast.MODID, "textures/gui/jei.png");
        background = helper.createDrawable(location, 0, 61, width, height);
        icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.CARVING_TABLE.get()));
    }

    @Override
    public RecipeType<CarvingRecipe> getRecipeType() {
        return GCRecipeTypes.CARVING;
    }

    @Override
    public Component getTitle() {
        return name;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CarvingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 21)
                .addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 97, 21)
                .addItemStack(recipe.getResultItem());
    }
}
