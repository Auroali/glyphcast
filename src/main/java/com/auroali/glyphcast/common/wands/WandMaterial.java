package com.auroali.glyphcast.common.wands;

import com.auroali.glyphcast.common.spells.SpellStats;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public record WandMaterial(List<Either<TagKey<Item>, Item>> validMaterials, int cooldown, double affinityMod) {
    public static final Codec<WandMaterial> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.either(TagKey.hashedCodec(Registry.ITEM_REGISTRY), ForgeRegistries.ITEMS.getCodec()).listOf().fieldOf("items").forGetter(WandMaterial::validMaterials),
                    Codec.INT.fieldOf("cooldown").forGetter(WandMaterial::cooldown),
                    Codec.DOUBLE.optionalFieldOf("affinityMod", 0.0).forGetter(WandMaterial::affinityMod)
            ).apply(instance, WandMaterial::new)
    );


    public void applyStats(SpellStats.Builder builder) {
        builder.addCooldown(cooldown);
        builder.addIceAffinity(affinityMod)
                .addLightAffinity(affinityMod)
                .addFireAffinity(affinityMod)
                .addEarthAffinity(affinityMod);
    }
}
