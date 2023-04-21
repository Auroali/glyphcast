package com.auroali.glyphcast.common.recipes;

import com.auroali.glyphcast.common.registry.GCRecipeTypes;
import com.auroali.glyphcast.common.registry.GCRecipesSerializers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

public class InfuseRecipe implements Recipe<Container> {

    public final double cost;
    public final Ingredient input;
    public final Ingredient other;
    public final ItemStack result;
    final ResourceLocation id;

    public InfuseRecipe(ResourceLocation id, double cost, Ingredient input, Ingredient other, ItemStack result) {
        this.id = id;
        this.cost = cost;
        this.input = input;
        this.other = other;
        this.result = result;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        return result.copy();
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

            Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            Ingredient other = pSerializedRecipe.has("other") ? Ingredient.fromJson(pSerializedRecipe.get("other")) : Ingredient.EMPTY;
            double cost = pSerializedRecipe.get("cost").getAsDouble();
            ItemStack result = CraftingHelper.getItemStack(pSerializedRecipe.getAsJsonObject("result"), true, true);
            return new InfuseRecipe(pRecipeId, cost, input, other, result);
        }

        @Override
        public @Nullable InfuseRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            double cost = pBuffer.readDouble();
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            Ingredient other = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            return new InfuseRecipe(pRecipeId, cost, input, other, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, InfuseRecipe pRecipe) {
            pBuffer.writeDouble(pRecipe.cost);
            pRecipe.input.toNetwork(pBuffer);
            pRecipe.other.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
        }
    }
}
