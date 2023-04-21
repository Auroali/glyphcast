package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

@SuppressWarnings("unused")
public class GCOres {
    public static final List<OreConfiguration.TargetBlockState> CRYSTAL_TARGETS = List.of(OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, GCBlocks.DEEPSLATE_CRYSTAL_ORE.get().defaultBlockState()));
    public static final Holder<ConfiguredFeature<?, ?>> CRYSTAL_ORE = BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(GlyphCast.MODID, "crystal_ore").toString(), new ConfiguredFeature<>(Feature.SCATTERED_ORE, new OreConfiguration(CRYSTAL_TARGETS, 17, 0.25f)));
    public static final Holder<PlacedFeature> CRYSTAL_ORE_PLACEMENT = BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(GlyphCast.MODID, "crystal_ore_placed").toString(), new PlacedFeature(Holder.hackyErase(CRYSTAL_ORE), rareOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-50), VerticalAnchor.aboveBottom(50)))));

    // I'm not quite sure why, but this seems to be required for everything to properly register
    public static void init() {
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> rareOrePlacement(int pChance, PlacementModifier pHeightRange) {
        return orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange);
    }
}
