package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.blocks.GlyphFlowerBlock;
import com.auroali.glyphcast.common.blocks.TrimmedGlyphFlowerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GCBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GlyphCast.MODID);
    public static final RegistryObject<GlyphFlowerBlock> BLUE_GLYPH_FLOWER = BLOCKS.register("blue_glyph_flower", GlyphFlowerBlock::new);
    public static final RegistryObject<Block> TRIMMED_GLYPH_FLOWER = BLOCKS.register("trimmed_glyph_flower", TrimmedGlyphFlowerBlock::new);
}
