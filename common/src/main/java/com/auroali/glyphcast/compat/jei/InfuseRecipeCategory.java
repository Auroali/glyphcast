package com.auroali.glyphcast.compat.jei;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import com.auroali.glyphcast.common.registry.GCItems;
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

public class InfuseRecipeCategory implements IRecipeCategory<InfuseRecipe> {
    public static final int width = 67;
    public static final int height = 61;

    Component name;
    IDrawable background;
    IDrawable icon;

    public InfuseRecipeCategory(IGuiHelper helper) {
        name = Component.translatable("gui.glyphcast.category.infuse");
        ResourceLocation location = new ResourceLocation(Glyphcast.MODID, "textures/gui/jei.png");
        background = helper.createDrawable(location, 0, 0, width, height);
        ItemStack stack = new ItemStack(GCItems.WANDERING_WAND.get());
        icon = helper.createDrawableItemStack(stack);
    }

    @Override
    public RecipeType<InfuseRecipe> getRecipeType() {
        return GCRecipeTypes.INFUSE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, InfuseRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 44)
                .addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 50, 1)
                .addItemStack(recipe.getResultItem());
    }
}
