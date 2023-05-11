package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.blocks.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class GCBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GlyphCast.MODID);
    public static final RegistryObject<GlyphFlowerBlock> BLUE_GLYPH_FLOWER = BLOCKS.register("blue_glyph_flower", GlyphFlowerBlock::new);
    public static final RegistryObject<Block> TRIMMED_GLYPH_FLOWER = BLOCKS.register("trimmed_glyph_flower", TrimmedGlyphFlowerBlock::new);
    public static final RegistryObject<Block> CRYSTAL_ORE = BLOCKS.register("crystal_ore", () -> new ParticleEmittingBlock(BlockBehaviour.Properties.copy(Blocks.STONE), () -> GCParticles.MAGIC_AMBIENCE.get()));
    public static final RegistryObject<Block> DEEPSLATE_CRYSTAL_ORE = BLOCKS.register("deepslate_crystal_ore", () -> new ParticleEmittingBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE), () -> GCParticles.MAGIC_AMBIENCE.get()));
    public static final RegistryObject<Block> CARVING_TABLE = BLOCKS.register("carving_table", CarvingTableBlock::new);
    public static final RegistryObject<Block> FRACTURE_SIPHON = BLOCKS.register("fracture_siphon", FractureSiphonBlock::new);
    public static final RegistryObject<Block> CONDENSED_ENERGY_CAULDRON = BLOCKS.register("condensed_energy_cauldron", CondensedEnergyCauldron::new);
    public static final RegistryObject<Block> CUT_CRYSTAL_BLOCK = BLOCKS.register("cut_crystal_block", () -> new ParticleEmittingBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK), () -> GCParticles.MAGIC_AMBIENCE.get()));
}
