package com.auroali.glyphcast.compat.fabric.emi;

import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfuseEmiRecipe implements EmiRecipe {
    final ResourceLocation id;
    final List<EmiIngredient> ingredients;
    final EmiStack output;

    public InfuseEmiRecipe(InfuseRecipe recipe) {
        this.id = recipe.getId();
        this.ingredients = recipe.getIngredients()
                .stream()
                .map(EmiIngredient::of)
                .toList();
        this.output = EmiStack.of(recipe.getResultItem());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return GlyphcastEmiPlugin.INFUSE_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return ingredients;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 67;
    }

    @Override
    public int getDisplayHeight() {
        return 61;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(new EmiTexture(GlyphcastEmiPlugin.SPRITES, 0, 0, getDisplayWidth(), getDisplayHeight()), 0, 0);
        widgets.addSlot(ingredients.get(0), 0, 0).drawBack(false);
        widgets.addSlot(ingredients.get(1), 0, 43).drawBack(false);
        widgets.addSlot(output, 49, 0).recipeContext(this).drawBack(false);
    }
}
