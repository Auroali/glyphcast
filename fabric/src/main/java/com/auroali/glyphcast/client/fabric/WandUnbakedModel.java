package com.auroali.glyphcast.client.fabric;

import com.auroali.glyphcast.Glyphcast;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class WandUnbakedModel implements UnbakedModel {

    public Material material = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(Glyphcast.MODID, "item/blank_parchment"));
    public Material wandMat;
    public Material wandCap;

    public WandUnbakedModel(ResourceLocation wandMat, ResourceLocation wandCap) {
        this.wandMat = wandMat == null ? null : new Material(TextureAtlas.LOCATION_BLOCKS, wandMat);
        this.wandCap = wandCap == null ? null : new Material(TextureAtlas.LOCATION_BLOCKS, wandCap);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        List<Material> materials = new ArrayList<>();

        // this is pretty bad but it works
        Minecraft.getInstance().getResourceManager().listResources("textures/item/wand", p -> p.getPath().endsWith(".png")).forEach((l, r) -> {
            ResourceLocation sprite = new ResourceLocation(l.getNamespace(), l.getPath().substring(9, l.getPath().length() - 4));
            materials.add(new Material(TextureAtlas.LOCATION_BLOCKS, sprite));
        });
        Minecraft.getInstance().getResourceManager().listResources("textures/item/wand_cap", p -> p.getPath().endsWith(".png")).forEach((l, r) -> {
            ResourceLocation sprite = new ResourceLocation(l.getNamespace(), l.getPath().substring(9, l.getPath().length() - 4));
            materials.add(new Material(TextureAtlas.LOCATION_BLOCKS, sprite));
        });
        return materials;
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBakery modelBakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation location) {
        List<BakedQuad> unculled = new ArrayList<>();
        Map<Direction, List<BakedQuad>> culled = Maps.newEnumMap(Direction.class);
        for (Direction direction : Direction.values()) {
            culled.put(direction, Lists.newArrayList());
        }

        if (wandMat != null) {
            TextureAtlasSprite sprite = spriteGetter.apply(wandMat);
            var bakedQuads = bakeSprite(location, transform, "wand_material", sprite);
            unculled.addAll(bakedQuads.getFirst());
            bakedQuads.getSecond().forEach((k, v) ->
                    culled.get(k).addAll(v)
            );
        }
        if (wandCap != null) {
            TextureAtlasSprite sprite = spriteGetter.apply(wandCap);
            var bakedQuads = bakeSprite(location, transform, "wand_cap", sprite);
            unculled.addAll(bakedQuads.getFirst());
            bakedQuads.getSecond().forEach((k, v) ->
                    culled.get(k).addAll(v)
            );
        }

        return new WandBakedModel(unculled, culled, transform);
    }

    public Pair<List<BakedQuad>, Map<Direction, List<BakedQuad>>> bakeSprite(ResourceLocation location, ModelState transform, String name, TextureAtlasSprite sprite) {
        ItemModelGenerator generator = new ItemModelGenerator();
        FaceBakery bakery = new FaceBakery();

        List<BlockElement> elements = generator.processFrames(0, name, sprite);
        Map<Direction, List<BakedQuad>> culled = Maps.newEnumMap(Direction.class);
        for (Direction direction : Direction.values()) {
            culled.put(direction, Lists.newArrayList());
        }

        List<BakedQuad> unculled = new ArrayList<>();
        for (BlockElement element : elements) {
            for (Direction d : element.faces.keySet()) {
                BlockElementFace f = element.faces.get(d);
                BakedQuad quad = bakery.bakeQuad(element.from, element.to, f, sprite, d, transform, element.rotation, element.shade, location);
                if (f.cullForDirection == null) {
                    unculled.add(quad);
                    continue;
                }

                culled.get(Direction.rotate(transform.getRotation().getMatrix(), f.cullForDirection)).add(quad);
            }
        }
        return Pair.of(unculled, culled);
    }
}
