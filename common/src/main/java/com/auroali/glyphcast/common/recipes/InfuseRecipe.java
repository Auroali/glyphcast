package com.auroali.glyphcast.common.recipes;

import com.auroali.glyphcast.common.registry.GCRecipeTypes;
import com.auroali.glyphcast.common.registry.GCRecipesSerializers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public record InfuseRecipe(ResourceLocation id, double cost, Ingredient input, Ingredient other,
                           ItemStack result, boolean preserveNbt) implements Recipe<Container> {

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return itemsMatch(pContainer.getItem(0), pContainer.getItem(1));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input, other);
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        return assemble(pContainer.getItem(0));
    }

    public ItemStack assemble(ItemStack input) {
        ItemStack output = result.copy();
        if (preserveNbt)
            output.getOrCreateTag().merge(input.getOrCreateTag());
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GCRecipesSerializers.INFUSE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return GCRecipeTypes.INFUSE_RECIPE.get();
    }

    public boolean itemsMatch(ItemStack input, ItemStack other) {
        return this.input.test(input) && (!this.consumesOther() || this.other.test(other));
    }

    public boolean consumesOther() {
        return other != Ingredient.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<InfuseRecipe> {
        @Override
        public InfuseRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            if (!pSerializedRecipe.has("input"))
                throw new JsonParseException("No input item for infuse recipe");
            if (!pSerializedRecipe.has("result"))
                throw new JsonParseException("No result item for infuse recipe");
            if (!pSerializedRecipe.has("cost"))
                throw new JsonParseException("No cost for infuse recipe");

            boolean preserveNbt = pSerializedRecipe.has("preserve_input_nbt") && pSerializedRecipe.get("preserve_input_nbt").getAsBoolean();
            Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            Ingredient other = pSerializedRecipe.has("other") ? Ingredient.fromJson(pSerializedRecipe.get("other")) : Ingredient.EMPTY;
            double cost = pSerializedRecipe.get("cost").getAsDouble();
            ItemStack result = ShapedRecipe.itemStackFromJson(pSerializedRecipe.getAsJsonObject("result"));
            return new InfuseRecipe(pRecipeId, cost, input, other, result, preserveNbt);
        }

        @Override
        public @Nullable InfuseRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            double cost = pBuffer.readDouble();
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            Ingredient other = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            boolean preserveNbt = pBuffer.readBoolean();
            return new InfuseRecipe(pRecipeId, cost, input, other, result, preserveNbt);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, InfuseRecipe pRecipe) {
            pBuffer.writeDouble(pRecipe.cost);
            pRecipe.input.toNetwork(pBuffer);
            pRecipe.other.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeBoolean(pRecipe.preserveNbt);
        }
    }
}
