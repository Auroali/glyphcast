package com.auroali.glyphcast.common.registry;

import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;

public class GCFluidAttributes {
    public static final ArchitecturyFluidAttributes ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(() -> () -> null, () -> GCFluids.CONDENSED_ENERGY);
}
