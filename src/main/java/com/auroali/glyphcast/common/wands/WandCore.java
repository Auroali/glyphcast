package com.auroali.glyphcast.common.wands;

import com.auroali.glyphcast.common.spells.SpellStats;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public record WandCore(ItemStack stack, double efficiency, double fireAffinity, double lightAffinity, double iceAffinity, double earthAffinity) {

    public static final Codec<WandCore> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("item").forGetter(WandCore::stackAsResourceLocation),
                    Codec.DOUBLE.fieldOf("efficiency").forGetter(WandCore::efficiency),
                    Codec.DOUBLE.fieldOf("fireAffinity").forGetter(WandCore::fireAffinity),
                    Codec.DOUBLE.fieldOf("lightAffinity").forGetter(WandCore::lightAffinity),
                    Codec.DOUBLE.fieldOf("iceAffinity").forGetter(WandCore::iceAffinity),
                    Codec.DOUBLE.fieldOf("earthAffinity").forGetter(WandCore::earthAffinity)
            ).apply(instance, WandCore::new)
    );


    public WandCore(ResourceLocation itemLocation, double efficiency, double fireAffinity, double lightAffinity, double iceAffinity, double earthAffinity) {
        this(ForgeRegistries.ITEMS.getValue(itemLocation) == null ? ItemStack.EMPTY : new ItemStack(ForgeRegistries.ITEMS.getValue(itemLocation)), efficiency, fireAffinity, lightAffinity, iceAffinity, earthAffinity);
    }

    public ResourceLocation stackAsResourceLocation() {
        return ForgeRegistries.ITEMS.getKey(this.stack.getItem());
    }

    public void applyStats(SpellStats.Builder builder) {
        builder.addEarthAffinity(earthAffinity);
        builder.addFireAffinity(fireAffinity);
        builder.addIceAffinity(iceAffinity);
        builder.addLightAffinity(lightAffinity);
        builder.addEfficiency(efficiency);
    }
}
