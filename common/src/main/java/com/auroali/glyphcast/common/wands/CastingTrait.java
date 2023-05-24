package com.auroali.glyphcast.common.wands;

import com.auroali.glyphcast.Glyphcast;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class CastingTrait {
    private String key = null;
    private final ChatFormatting formatting;

    public CastingTrait(ChatFormatting formatting) {
        this.formatting = formatting;
    }
    public static final Codec<CastingTrait> CODEC = ResourceLocation.CODEC.flatXmap(
            resourceLocation -> Optional.ofNullable(Glyphcast.CASTING_TRAITS.get(resourceLocation)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown registry key '" + resourceLocation + "' in " + Glyphcast.CASTING_TRAIT_REGISTRY)),
            trait -> Glyphcast.CASTING_TRAITS.getKey(trait).map(ResourceKey::location).map(DataResult::success).orElseGet(() -> DataResult.error("Object was not present in registry '" + Glyphcast.CASTING_TRAIT_REGISTRY + "'"))
    );

    public Component getTranslationComponent() {
        if(key == null) {
            ResourceLocation location = Glyphcast.CASTING_TRAITS.getId(this);
            key = "casting_traits.%s.%s".formatted(location.getNamespace(), location.getPath());
        }
        return Component.translatable(key).withStyle(formatting);
    }
}
