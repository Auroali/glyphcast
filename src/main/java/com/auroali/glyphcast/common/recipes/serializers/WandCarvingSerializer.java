package com.auroali.glyphcast.common.recipes.serializers;

import com.auroali.glyphcast.common.recipes.CarvingRecipe;
import com.auroali.glyphcast.common.registry.GCItems;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class WandCarvingSerializer implements RecipeSerializer<CarvingRecipe> {
    @Override
    public CarvingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        if (!pSerializedRecipe.has("input"))
            throw new JsonParseException("No input item for carving recipe");
        if (!pSerializedRecipe.has("wand_material"))
            throw new JsonParseException("No wand_material id for carving recipe");

        Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
        ResourceLocation material = ResourceLocation.tryParse(pSerializedRecipe.get("wand_material").getAsString());
        if (material == null)
            throw new JsonParseException("Error parsing resource location %s! Is it in the format of 'key:path'?".formatted(pSerializedRecipe.get("wand_material").getAsString()));
        ItemStack result = new ItemStack(GCItems.WAND.get());
        GCItems.WAND.get().setMaterial(result, material);
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
        pBuffer.writeItem(pRecipe.output());
    }
}

