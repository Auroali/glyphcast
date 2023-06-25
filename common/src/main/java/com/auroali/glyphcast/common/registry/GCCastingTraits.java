package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.wands.CastingTrait;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.ChatFormatting;

@SuppressWarnings("unused")
public class GCCastingTraits {

    public static final DeferredRegister<CastingTrait> TRAITS = DeferredRegister.create(Glyphcast.MODID, Glyphcast.CASTING_TRAIT_REGISTRY);
    public static final RegistrySupplier<CastingTrait> ANCIENT = TRAITS.register("ancient", () -> new CastingTrait(ChatFormatting.GOLD));
}
