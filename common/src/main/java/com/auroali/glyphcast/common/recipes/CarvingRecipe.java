package com.auroali.glyphcast.common.recipes;

import com.auroali.glyphcast.common.menu.container.InputContainer;
import com.auroali.glyphcast.common.registry.GCRecipeTypes;
import com.auroali.glyphcast.common.registry.GCRecipesSerializers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record CarvingRecipe(ResourceLocation id, Ingredient input,
                            ItemStack output) implements Recipe<InputContainer> {
    @Override
    public boolean matches(InputContainer pContainer, Level pLevel) {
        return input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(InputContainer pContainer) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 1;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GCRecipesSerializers.CARVING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return GCRecipeTypes.CARVING_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<CarvingRecipe> {
        @Override
        public CarvingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            if (!pSerializedRecipe.has("input"))
                throw new JsonParseException("No input item for carving recipe");
            if (!pSerializedRecipe.has("result"))
                throw new JsonParseException("No result item for carving recipe");

            Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            ItemStack result = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result"));
            return new CarvingRecipe(pRecipeId, input, result);
        }

        @Override
        public @Nullable CarvingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            return new CarvingRecipe(pRecipeId, input, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CarvingRecipe pRecipe) {
            pRecipe.input().toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.output);
        }
    }
}
