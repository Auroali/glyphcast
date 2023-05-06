package com.auroali.glyphcast.common.wands;

import com.auroali.glyphcast.common.spells.SpellStats;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public record WandCore(Item item, double efficiency, double fireAffinity, double lightAffinity,
                       double iceAffinity, double earthAffinity) {

    public static final Codec<WandCore> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(WandCore::item),
                    Codec.DOUBLE.fieldOf("efficiency").forGetter(WandCore::efficiency),
                    Codec.DOUBLE.fieldOf("fireAffinity").forGetter(WandCore::fireAffinity),
                    Codec.DOUBLE.fieldOf("lightAffinity").forGetter(WandCore::lightAffinity),
                    Codec.DOUBLE.fieldOf("iceAffinity").forGetter(WandCore::iceAffinity),
                    Codec.DOUBLE.fieldOf("earthAffinity").forGetter(WandCore::earthAffinity)
            ).apply(instance, WandCore::new)
    );

    public void applyStats(SpellStats.Builder builder) {
        builder.addEarthAffinity(earthAffinity);
        builder.addFireAffinity(fireAffinity);
        builder.addIceAffinity(iceAffinity);
        builder.addLightAffinity(lightAffinity);
        builder.addEfficiency(efficiency);
    }
}
