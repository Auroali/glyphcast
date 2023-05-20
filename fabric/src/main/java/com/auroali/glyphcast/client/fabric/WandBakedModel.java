package com.auroali.glyphcast.client.fabric;

import com.auroali.glyphcast.common.items.WandItem;
import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WandBakedModel implements BakedModel {
    List<BakedQuad> unculled;
    Map<Direction, List<BakedQuad>> culled;
    ModelState state;

    public WandBakedModel(List<BakedQuad> unculled, Map<Direction, List<BakedQuad>> culled, ModelState state) {
        this.unculled = unculled;
        this.culled = culled;
        this.state = state;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return direction == null ? unculled : culled.get(direction);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override
    public ItemTransforms getTransforms() {
        ItemTransform thirdp_left = new ItemTransform(
                new Vector3f(0, 90, -55),
                makeTrans(0, 0.8f, 0.5f),
                new Vector3f(0.85f, 0.85f, 0.85f));
        ItemTransform thirdp_right = new ItemTransform(
                new Vector3f(0, 90, 55),
                makeTrans(0, 0.8f, 0.5f),
                new Vector3f(0.85f, 0.85f, 0.85f));
        ItemTransform firstp_left = new ItemTransform(
                new Vector3f(0, 90, -25),
                makeTrans(1.13f, 3.2f, 1.13f),
                new Vector3f(0.68f, 0.68f, 0.68f));
        ItemTransform firstp_right = new ItemTransform(
                new Vector3f(0, -90, 25),
                makeTrans(1.13f, 3.2f, 1.13f),
                new Vector3f(0.68f, 0.68f, 0.68f));

        ItemTransform fixed = new ItemTransform(
                new Vector3f(0, 180, 0),
                makeTrans(0, 0, 0),
                new Vector3f(1f, 1f, 1f));
        ItemTransform ground = new ItemTransform(
                new Vector3f(0, 0, 0),
                makeTrans(0, 2, 0),
                new Vector3f(0.5f, 0.5f, 0.5f));
        ItemTransform head = new ItemTransform(
                new Vector3f(0, 180, 0),
                makeTrans(0, 13, 7),
                new Vector3f(1f, 1f, 1f));
        return new ItemTransforms(thirdp_left, thirdp_right, firstp_left, firstp_right, head, ItemTransform.NO_TRANSFORM, ground, fixed);
    }

    Vector3f makeTrans(float x, float y, float z) {
        Vector3f f = new Vector3f(x, y, z);
        // why
        f.mul(0.0625f);
        return f;
    }

    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides(null, null, null, Collections.emptyList()) {
            final HashMap<String, BakedModel> cache = new HashMap<>();

            @Nullable
            @Override
            public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
                if (stack.getItem() instanceof WandItem wand) {
                    ResourceLocation material = wand.getMaterialResourceLocation(stack);
                    ResourceLocation cap = wand.getCapResourceLocation(stack);
                    String key = material + ";" + cap.toString();
                    if (!cache.containsKey(key)) {
                        ResourceLocation texPath = new ResourceLocation(material.getNamespace(), "item/wand/" + material.getPath());
                        ResourceLocation capPath = cap.getPath().isEmpty() ? null : new ResourceLocation(cap.getNamespace(), "item/wand_cap/" + cap.getPath());
                        WandUnbakedModel unbaked = new WandUnbakedModel(texPath, capPath);
                        BakedModel bakedModel = unbaked.bake(null, Material::sprite, state, new ResourceLocation("glyphcast:wand_override"));
                        cache.put(key, bakedModel);
                        return bakedModel;
                    }
                    return cache.get(key);
                }
                return super.resolve(model, stack, level, entity, seed);
            }
        };
    }
}
