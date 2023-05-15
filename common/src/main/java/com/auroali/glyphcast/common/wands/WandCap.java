package com.auroali.glyphcast.common.wands;

import com.auroali.glyphcast.common.spells.SpellStats;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;

public record WandCap(List<Either<TagKey<Item>, Item>> validMaterials, double efficency) {
    public static final Codec<WandCap> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.either(TagKey.hashedCodec(Registry.ITEM_REGISTRY), Registry.ITEM.byNameCodec()).listOf().fieldOf("items").forGetter(WandCap::validMaterials),
                    Codec.DOUBLE.fieldOf("efficiency").forGetter(WandCap::efficency)
            ).apply(instance, WandCap::new)
    );

    public void applyStats(SpellStats.Builder builder) {
        builder.addEfficiency(efficency);
    }
}
