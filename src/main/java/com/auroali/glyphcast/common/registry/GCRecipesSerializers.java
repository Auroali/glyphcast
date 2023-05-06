package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.recipes.CarvingRecipe;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import com.auroali.glyphcast.common.recipes.WandCapRecipe;
import com.auroali.glyphcast.common.recipes.WandCoreRecipe;
import com.auroali.glyphcast.common.recipes.serializers.WandCarvingSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class GCRecipesSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, GlyphCast.MODID);
    public static final RegistryObject<RecipeSerializer<?>> WAND_CORE = RECIPES.register("wand_core", () -> new SimpleRecipeSerializer<>(WandCoreRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> WAND_CAP = RECIPES.register("wand_cap", () -> new SimpleRecipeSerializer<>(WandCapRecipe::new));
    public static final RegistryObject<RecipeSerializer<?>> INFUSE = RECIPES.register("infuse", InfuseRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> CARVING = RECIPES.register("carving", CarvingRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> WAND_CARVING = RECIPES.register("wand_carving", WandCarvingSerializer::new);
}
