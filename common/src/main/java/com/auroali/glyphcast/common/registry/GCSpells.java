package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.spells.*;
import com.auroali.glyphcast.common.spells.single.LightSpell;
import com.auroali.glyphcast.common.spells.wand.MagicDamageSpell;
import com.auroali.glyphcast.common.spells.wand.MagicExtractSpell;
import com.auroali.glyphcast.common.spells.wand.MagicInfuseSpell;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

@SuppressWarnings("unused")
public class GCSpells {

    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(Glyphcast.MODID, Glyphcast.SPELL_REGISTRY);
    public static final RegistrySupplier<Spell> LIGHT = SPELLS.register("light", LightSpell::new);
    public static final RegistrySupplier<Spell> WAND_ATTACK = SPELLS.register("focused_stream", MagicDamageSpell::new);
    public static final RegistrySupplier<Spell> WAND_INFUSE = SPELLS.register("focused_infuse", MagicInfuseSpell::new);
    public static final RegistrySupplier<Spell> WAND_EXTRACT = SPELLS.register("focused_extract", MagicExtractSpell::new);
}
