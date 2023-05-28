package com.auroali.glyphcast.compat.fabric.emi;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.registry.GCItems;
import com.auroali.glyphcast.common.registry.GCRecipeTypes;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

public class GlyphcastEmiPlugin implements EmiPlugin {
    public static final ResourceLocation SPRITES = new ResourceLocation(Glyphcast.MODID, "textures/gui/jei.png");
    public static final EmiStack CARVING_TABLE = EmiStack.of(GCItems.CARVING_TABLE.get());
    public static final EmiStack WAND = EmiStack.of(GCItems.WANDERING_WAND.get());
    public static final EmiRecipeCategory CARVING_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation(Glyphcast.MODID, "carving"),
            CARVING_TABLE,
            new EmiTexture(SPRITES, 67, 0, 16, 16));
    public static final EmiRecipeCategory INFUSE_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation(Glyphcast.MODID, "infuse"),
            WAND,
            new EmiTexture(SPRITES, 67, 16, 16, 16));
    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CARVING_CATEGORY);
        registry.addCategory(INFUSE_CATEGORY);
        registry.addWorkstation(CARVING_CATEGORY, CARVING_TABLE);
        registry.addWorkstation(INFUSE_CATEGORY, EmiStack.of(new ItemStack(GCItems.WANDERING_WAND.get())));
        registry.addWorkstation(INFUSE_CATEGORY, EmiStack.of(new ItemStack(GCItems.OAK_WAND.get())));

        RecipeManager manager = registry.getRecipeManager();
        manager.getAllRecipesFor(GCRecipeTypes.CARVING_RECIPE.get())
                .stream()
                .map(CarvingEmiRecipe::new)
                .forEach(registry::addRecipe);
        manager.getAllRecipesFor(GCRecipeTypes.INFUSE_RECIPE.get())
                .stream()
                .map(InfuseEmiRecipe::new)
                .forEach(registry::addRecipe);
    }

    public EmiStack wandWithCore(Item item, ResourceLocation location) {
        ItemStack wand = new ItemStack(item);
        if(item instanceof IWandLike wandLike)
            wandLike.setCore(wand, location);
        return EmiStack.of(wand);
    }
}
