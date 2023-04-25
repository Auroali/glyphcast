package com.auroali.glyphcast.client.model;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.items.StaffItem;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.geometry.*;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class DynamicStaffModel implements IUnbakedGeometry<DynamicStaffModel> {
    private final ResourceLocation staffTex;

    public DynamicStaffModel(ResourceLocation staffTex) {
        this.staffTex = staffTex;
    }

    public static RenderTypeGroup getLayerRenderTypes(boolean unlit) {
        return new RenderTypeGroup(RenderType.translucent(), unlit ? ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
    }

    @SuppressWarnings("deprecation")
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        var texture = context.getMaterial("layer0");
        var itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(modelLocation);

        var staffSprite = staffTex != null ? spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, staffTex)) : null;


        var builder = CompositeModel.Baked.builder(itemContext, spriteGetter.apply(texture), new DynWandOverride(overrides, bakery, itemContext), context.getTransforms());

        var normalRenderTypes = getLayerRenderTypes(false);

        if (staffSprite != null) {
            // Base texture
            var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, staffSprite);
            var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> staffSprite, modelState, modelLocation);
            builder.addQuads(normalRenderTypes, quads);
        } else GlyphCast.LOGGER.warn("No wand texture found for path {}", staffTex);

        return builder.build();
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Set<Material> materials = Sets.newHashSet();
        if (context.hasMaterial("layer0")) materials.add(context.getMaterial("layer0"));
        return materials;
    }

    public static class Loader implements IGeometryLoader<DynamicStaffModel> {

        @Override
        public DynamicStaffModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new DynamicStaffModel(new ResourceLocation(GlyphCast.MODID, "item/staff/empty"));
        }
    }

    public static class DynWandOverride extends ItemOverrides {
        private final Map<String, BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
        private final ModelBakery bakery;
        private final IGeometryBakingContext owner;
        private final ItemOverrides nested;

        public DynWandOverride(ItemOverrides nested, ModelBakery bakery, IGeometryBakingContext owner) {
            this.bakery = bakery;
            this.owner = owner;
            this.nested = nested;
        }

        @Nullable
        @Override
        public BakedModel resolve(BakedModel pModel, ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
            BakedModel overridden = nested.resolve(pModel, pStack, pLevel, pEntity, pSeed);
            if (overridden != pModel) return overridden;
            if (pStack.getItem() instanceof StaffItem) {
                String variant = pStack.getOrCreateTag().getString("Variant");
                boolean alive = pStack.getOrCreateTag().getBoolean("Alive");
                boolean active = pStack.getOrCreateTag().getBoolean("Present");
                ResourceLocation loc = new ResourceLocation(GlyphCast.MODID, "item/staff/" + (variant.isEmpty() ? "empty" : variant + (alive ? active ? "_active" : "_empty" : "")));
                if (!cache.containsKey(loc.toString())) {
                    DynamicStaffModel unbaked = new DynamicStaffModel(loc);
                    BakedModel model = unbaked.bake(owner, bakery, Material::sprite, BlockModelRotation.X0_Y0, this, new ResourceLocation("glyphcast:wand_override"));
                    cache.put(loc.toString(), model);
                    return model;
                }
                return cache.get(loc.toString());
            }
            return pModel;
        }
    }


}
