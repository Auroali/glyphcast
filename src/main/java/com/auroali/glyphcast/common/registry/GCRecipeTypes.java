package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.recipes.InfuseRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class GCRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, GlyphCast.MODID);
    public static final RegistryObject<RecipeType<InfuseRecipe>> INFUSE_RECIPE = RECIPE_TYPES.register("infuse", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return GlyphCast.MODID + ":infuse";
        }
    });
}
