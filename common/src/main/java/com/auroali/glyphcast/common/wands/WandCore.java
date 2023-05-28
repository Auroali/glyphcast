package com.auroali.glyphcast.common.wands;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import java.util.List;

public record WandCore(Item item, List<CastingTrait> traits) {

    public static final Codec<WandCore> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Registry.ITEM.byNameCodec().fieldOf("item").forGetter(WandCore::item),
                    CastingTrait.CODEC.listOf().fieldOf("traits").forGetter(WandCore::traits)
            ).apply(instance, WandCore::new)
    );
}
