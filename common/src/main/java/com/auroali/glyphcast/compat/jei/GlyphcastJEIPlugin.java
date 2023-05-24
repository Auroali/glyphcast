package com.auroali.glyphcast.compat.jei;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.registry.GCBlocks;
import com.auroali.glyphcast.common.registry.GCItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class GlyphcastJEIPlugin implements IModPlugin {
    InfuseRecipeCategory infuseCategory;
    CarvingRecipeCategory carvingCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Glyphcast.MODID, "jei");
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
        GlyphcastRecipes recipes = new GlyphcastRecipes(registration.getIngredientManager());
        registration.addRecipes(GCRecipeTypes.INFUSE, recipes.getInfuseRecipes(infuseCategory));
        registration.addRecipes(GCRecipeTypes.CARVING, recipes.getCarvingRecipes(carvingCategory));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        Registry.ITEM.stream().forEach(item -> {
            if(item instanceof IWandLike wandLike)
                registration.addRecipeCatalyst(wandWithCore(wandLike, new ResourceLocation(Glyphcast.MODID, "petal")), GCRecipeTypes.INFUSE);
        });

        registration.addRecipeCatalyst(new ItemStack(GCBlocks.CARVING_TABLE.get()), GCRecipeTypes.CARVING);
    }

    public ItemStack wandWithCore(IWandLike item, ResourceLocation location) {
        ItemStack wand = new ItemStack((Item)item);
        item.setCore(wand, location);
        return wand;
    }
}
