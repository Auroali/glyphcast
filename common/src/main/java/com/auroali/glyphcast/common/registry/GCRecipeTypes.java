package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.recipes.CarvingRecipe;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeType;

@SuppressWarnings("unused")
public class GCRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Glyphcast.MODID, Registry.RECIPE_TYPE_REGISTRY);
    public static final RegistrySupplier<RecipeType<InfuseRecipe>> INFUSE_RECIPE = RECIPE_TYPES.register("infuse", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return Glyphcast.MODID + ":infuse";
        }
    });
    public static final RegistrySupplier<RecipeType<CarvingRecipe>> CARVING_RECIPE = RECIPE_TYPES.register("carving", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return Glyphcast.MODID + ":carving";
        }
    });
}
