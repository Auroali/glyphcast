package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.fluids.CondensedEnergyFluid;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.material.Fluid;

public class GCFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Glyphcast.MODID, Registry.FLUID_REGISTRY);
    public static final RegistrySupplier<Fluid> CONDENSED_ENERGY = FLUIDS.register("condensed_energy", () ->
            new CondensedEnergyFluid.Source(GCFluidAttributes.ATTRIBUTES)
    );
}
