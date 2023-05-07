package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.blocks.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class GCBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GlyphCast.MODID);
    public static final RegistryObject<GlyphFlowerBlock> BLUE_GLYPH_FLOWER = BLOCKS.register("blue_glyph_flower", GlyphFlowerBlock::new);
    public static final RegistryObject<Block> TRIMMED_GLYPH_FLOWER = BLOCKS.register("trimmed_glyph_flower", TrimmedGlyphFlowerBlock::new);
    public static final RegistryObject<Block> CRYSTAL_ORE = BLOCKS.register("crystal_ore", () -> new CrystalOreBlock(SoundType.STONE));
    public static final RegistryObject<Block> DEEPSLATE_CRYSTAL_ORE = BLOCKS.register("deepslate_crystal_ore", () -> new CrystalOreBlock(SoundType.DEEPSLATE));
    public static final RegistryObject<Block> CARVING_TABLE = BLOCKS.register("carving_table", CarvingTableBlock::new);
    public static final RegistryObject<Block> FRACTURE_SIPHON = BLOCKS.register("fracture_siphon", FractureSiphonBlock::new);
    public static final RegistryObject<Block> CONDENSED_ENERGY_CAULDRON = BLOCKS.register("condensed_energy_cauldron", CondensedEnergyCauldron::new);
}
