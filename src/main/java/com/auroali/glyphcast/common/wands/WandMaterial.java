package com.auroali.glyphcast.common.wands;

import com.auroali.glyphcast.common.spells.SpellStats;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.UUID;

public record WandMaterial(int cooldown, double affinityMod, UUID uuid) {
    public static final Codec<WandMaterial> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.INT.fieldOf("cooldown").forGetter(WandMaterial::cooldown),
                    Codec.DOUBLE.optionalFieldOf("affinityMod", 0.0).forGetter(WandMaterial::affinityMod)
            ).apply(instance, WandMaterial::new)
    );

    public WandMaterial(int cooldown, double affinityMod) {
        // probably a dumb solution but it solves all wand varaints being the same
        this(cooldown, affinityMod, UUID.randomUUID());
    }

    public void applyStats(SpellStats.Builder builder) {
        builder.addCooldown(cooldown);
        builder.addIceAffinity(affinityMod)
                .addLightAffinity(affinityMod)
                .addFireAffinity(affinityMod)
                .addEarthAffinity(affinityMod);
    }

}
