package com.auroali.glyphcast.compat.jei;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCWandMaterials;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class GlyphcastJEIPlugin implements IModPlugin {
    InfuseRecipeCategory infuseCategory;
    CarvingRecipeCategory carvingCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(GlyphCast.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                infuseCategory = new InfuseRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                carvingCategory = new CarvingRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        GlyphCastRecipes recipes = new GlyphCastRecipes(registration.getIngredientManager());
        registration.addRecipes(GCRecipeTypes.INFUSE, recipes.getInfuseRecipes(infuseCategory));
        registration.addRecipes(GCRecipeTypes.CARVING, recipes.getCarvingRecipes(carvingCategory));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        GCWandMaterials.KEY_MAP.keySet().forEach(mat -> {
            ItemStack wand = new ItemStack(GCItems.WAND.get());
            GCItems.WAND.get().setCap(wand, new ResourceLocation(GlyphCast.MODID, "iron"));
            GCItems.WAND.get().setMaterial(wand, mat);
            GCItems.WAND.get().setCore(wand, new ResourceLocation(GlyphCast.MODID, "petal"));
            registration.addRecipeCatalyst(wand, GCRecipeTypes.INFUSE);
        });
        registration.addRecipeCatalyst(new ItemStack(GCBlocks.CARVING_TABLE.get()), GCRecipeTypes.CARVING);
    }
}
