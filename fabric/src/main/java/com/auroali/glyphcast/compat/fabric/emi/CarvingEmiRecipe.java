package com.auroali.glyphcast.compat.fabric.emi;

import com.auroali.glyphcast.common.recipes.CarvingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CarvingEmiRecipe implements EmiRecipe {
    final ResourceLocation id;
    final EmiIngredient ingredient;
    final EmiStack output;

    public CarvingEmiRecipe(CarvingRecipe recipe) {
        this.id = recipe.getId();
        this.ingredient = EmiIngredient.of(recipe.input());
        this.output = EmiStack.of(recipe.getResultItem());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return GlyphcastEmiPlugin.CARVING_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(ingredient);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 59;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(new EmiTexture(GlyphcastEmiPlugin.SPRITES, 0, 61, getDisplayWidth(), getDisplayHeight()), 0, 0);
        widgets.addSlot(ingredient, 20, 20).drawBack(false);
        widgets.addSlot(output, 98, 20).recipeContext(this).drawBack(false);
    }
}
