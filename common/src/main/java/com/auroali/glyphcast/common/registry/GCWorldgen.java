package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

@SuppressWarnings("unused")
public class GCWorldgen {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Glyphcast.MODID, Registry.CONFIGURED_FEATURE_REGISTRY);
    public static final RegistrySupplier<ConfiguredFeature<?, ?>> CRYSTAL_ORE = CONFIGURED_FEATURES.register("crystal_ore", () -> new ConfiguredFeature<>(Feature.ORE,
            new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, GCBlocks.DEEPSLATE_CRYSTAL_ORE.get().defaultBlockState(), 17, 0.25f)
    ));
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Glyphcast.MODID, Registry.PLACED_FEATURE_REGISTRY);
    public static final RegistrySupplier<PlacedFeature> PLACED_CRYSTAL_ORE = PLACED_FEATURES.register("crystal_ore_placed", () -> new PlacedFeature(Holder.direct(CRYSTAL_ORE.get()),
            List.of(CountPlacement.of(4), InSquarePlacement.spread(), PlacementUtils.RANGE_10_10, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(50))
            )));
}
