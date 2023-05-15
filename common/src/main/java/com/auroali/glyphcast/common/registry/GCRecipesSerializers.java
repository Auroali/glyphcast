package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.recipes.CarvingRecipe;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import com.auroali.glyphcast.common.recipes.WandCapRecipe;
import com.auroali.glyphcast.common.recipes.WandCoreRecipe;
import com.auroali.glyphcast.common.recipes.serializers.WandCarvingSerializer;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

@SuppressWarnings("unused")
public class GCRecipesSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(Glyphcast.MODID, Registry.RECIPE_SERIALIZER_REGISTRY);
    public static final RegistrySupplier<RecipeSerializer<?>> WAND_CORE = RECIPES.register("wand_core", () -> new SimpleRecipeSerializer<>(WandCoreRecipe::new));
    public static final RegistrySupplier<RecipeSerializer<?>> WAND_CAP = RECIPES.register("wand_cap", () -> new SimpleRecipeSerializer<>(WandCapRecipe::new));
    public static final RegistrySupplier<RecipeSerializer<?>> INFUSE = RECIPES.register("infuse", InfuseRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeSerializer<?>> CARVING = RECIPES.register("carving", CarvingRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeSerializer<?>> WAND_CARVING = RECIPES.register("wand_carving", WandCarvingSerializer::new);
}
