package com.auroali.glyphcast.client.model;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.items.WandItem;
import com.auroali.glyphcast.common.registry.GCWandCaps;
import com.auroali.glyphcast.common.registry.GCWandMaterials;
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

public class DynamicWandModel implements IUnbakedGeometry<DynamicWandModel> {


    private final ResourceLocation wandTex;
    private final ResourceLocation capTex;

    public DynamicWandModel(ResourceLocation wandTex, ResourceLocation capTex) {
        this.wandTex = wandTex;
        this.capTex = capTex;
    }
    @SuppressWarnings("deprecation")
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        var texture = context.getMaterial("layer0");
        var itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(modelLocation);

        var wandSprite = wandTex != null ? spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, wandTex)) : null;
        var capSprite = capTex != null ? spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, capTex)) : null;

        var builder = CompositeModel.Baked.builder(itemContext, spriteGetter.apply(texture), new DynWandOverride(overrides, this, bakery, itemContext), context.getTransforms());

        var normalRenderTypes = getLayerRenderTypes(false);

        if (wandSprite != null)
        {
            // Base texture
            var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, wandSprite);
            var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> wandSprite, modelState, modelLocation);
            builder.addQuads(normalRenderTypes, quads);
        } else GlyphCast.LOGGER.warn("No wand texture found for path {}", wandTex);

        if (capSprite != null)
        {
            // Wand Cap
            var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(1, capSprite);
            var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> capSprite, modelState, modelLocation);
            builder.addQuads(normalRenderTypes, quads);
        }

        return builder.build();
    }

    public static RenderTypeGroup getLayerRenderTypes(boolean unlit)
    {
        return new RenderTypeGroup(RenderType.translucent(), unlit ? ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Set<Material> materials = Sets.newHashSet();
        if(context.hasMaterial("layer0")) materials.add(context.getMaterial("layer0"));
        return materials;
    }

    public static class Loader implements IGeometryLoader<DynamicWandModel> {

        @Override
        public DynamicWandModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new DynamicWandModel(new ResourceLocation(GlyphCast.MODID, "item/wand/wandering"), null);
        }
    }
    public static class DynWandOverride extends ItemOverrides {
        DynamicWandModel model;
        private final Map<String, BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
        private final ModelBakery bakery;
        private final IGeometryBakingContext owner;
        private final ItemOverrides nested;
        public DynWandOverride(ItemOverrides nested, DynamicWandModel model, ModelBakery bakery, IGeometryBakingContext owner) {
            this.model = model;
            this.bakery = bakery;
            this.owner = owner;
            this.nested = nested;
        }
        @Nullable
        @Override
        public BakedModel resolve(BakedModel pModel, ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
            BakedModel overridden = nested.resolve(pModel, pStack, pLevel, pEntity, pSeed);
            if (overridden != pModel) return overridden;
            if(pStack.getItem() instanceof WandItem wand) {
                ResourceLocation material = wand.getMaterial(pStack).map(GCWandMaterials::getKey).orElse(null);
                ResourceLocation cap = wand.getCap(pStack).map(GCWandCaps::getKey).orElse(null);
                if (material == null)
                    return pModel;
                String key = material + ";" + (cap == null ? "" : cap.toString());
                if (!cache.containsKey(key)) {
                    ResourceLocation texPath = new ResourceLocation(material.getNamespace(), "item/wand/" + material.getPath());
                    ResourceLocation capPath = cap == null ? null : new ResourceLocation(cap.getNamespace(), "item/wand_cap/" + cap.getPath());
                    DynamicWandModel unbaked = new DynamicWandModel(texPath, capPath);
                    BakedModel model = unbaked.bake(owner, bakery, Material::sprite, BlockModelRotation.X0_Y0, this, new ResourceLocation("glyphcast:wand_override"));
                    cache.put(key, model);
                    return model;
                }
                return cache.get(key);
            }
            return pModel;
        }
    }
}
