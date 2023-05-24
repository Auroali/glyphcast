package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.wand.MagicDamageSpell;
import com.auroali.glyphcast.common.spells.wand.MagicExtractSpell;
import com.auroali.glyphcast.common.spells.wand.MagicInfuseSpell;
import com.auroali.glyphcast.common.wands.CastingTrait;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

@SuppressWarnings("unused")
public class GCCastingTraits {

    public static final DeferredRegister<CastingTrait> TRAITS = DeferredRegister.create(Glyphcast.MODID, Glyphcast.CASTING_TRAIT_REGISTRY);
}
