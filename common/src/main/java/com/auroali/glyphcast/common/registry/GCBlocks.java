package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.blocks.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

@SuppressWarnings("unused")
public class GCBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Glyphcast.MODID, Registry.BLOCK_REGISTRY);
    public static final RegistrySupplier<GlyphFlowerBlock> BLUE_GLYPH_FLOWER = BLOCKS.register("blue_glyph_flower", GlyphFlowerBlock::new);
    public static final RegistrySupplier<Block> TRIMMED_GLYPH_FLOWER = BLOCKS.register("trimmed_glyph_flower", TrimmedGlyphFlowerBlock::new);
    public static final RegistrySupplier<Block> CRYSTAL_ORE = BLOCKS.register("crystal_ore", () -> new ParticleEmittingBlock(BlockBehaviour.Properties.copy(Blocks.STONE), () -> GCParticles.MAGIC_AMBIENCE.get()));
    public static final RegistrySupplier<Block> DEEPSLATE_CRYSTAL_ORE = BLOCKS.register("deepslate_crystal_ore", () -> new ParticleEmittingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE), () -> GCParticles.MAGIC_AMBIENCE.get()));
    public static final RegistrySupplier<Block> CARVING_TABLE = BLOCKS.register("carving_table", CarvingTableBlock::new);
    public static final RegistrySupplier<Block> FRACTURE_SIPHON = BLOCKS.register("fracture_siphon", FractureSiphonBlock::new);
    public static final RegistrySupplier<Block> CONDENSED_ENERGY_CAULDRON = BLOCKS.register("condensed_energy_cauldron", CondensedEnergyCauldron::new);
    public static final RegistrySupplier<Block> CUT_CRYSTAL_BLOCK = BLOCKS.register("cut_crystal_block", () -> new ParticleEmittingBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK), () -> GCParticles.MAGIC_AMBIENCE.get()));
}
