package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import net.minecraft.resources.ResourceLocation;

public class GCFluidAttributes {
    public static final ArchitecturyFluidAttributes ATTRIBUTES = SimpleArchitecturyFluidAttributes
            .ofSupplier(() -> () -> null, () -> GCFluids.CONDENSED_ENERGY)
            .sourceTexture(new ResourceLocation(Glyphcast.MODID, "block/condensed_energy"));
}
