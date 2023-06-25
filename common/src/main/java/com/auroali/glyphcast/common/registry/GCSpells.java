package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.composite.MendSpell;
import com.auroali.glyphcast.common.spells.composite.PullSpell;
import com.auroali.glyphcast.common.spells.composite.PushSpell;
import com.auroali.glyphcast.common.spells.single.EarthSpell;
import com.auroali.glyphcast.common.spells.single.FireSpell;
import com.auroali.glyphcast.common.spells.single.IceSpell;
import com.auroali.glyphcast.common.spells.single.LightSpell;
import com.auroali.glyphcast.common.spells.wand.MagicDamageSpell;
import com.auroali.glyphcast.common.spells.wand.MagicExtractSpell;
import com.auroali.glyphcast.common.spells.wand.MagicInfuseSpell;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

@SuppressWarnings("unused")
public class GCSpells {

    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(Glyphcast.MODID, Glyphcast.SPELL_REGISTRY);
    // Single
    public static final RegistrySupplier<Spell> FIRE = SPELLS.register("fire", FireSpell::new);
    public static final RegistrySupplier<Spell> LIGHT = SPELLS.register("light", LightSpell::new);
    public static final RegistrySupplier<Spell> ICE = SPELLS.register("ice", IceSpell::new);
    public static final RegistrySupplier<Spell> EARTH = SPELLS.register("earth", EarthSpell::new);
    // Composite
    public static final RegistrySupplier<Spell> PULL = SPELLS.register("pull", PullSpell::new);
    public static final RegistrySupplier<Spell> PUSH = SPELLS.register("push", PushSpell::new);
    public static final RegistrySupplier<Spell> MEND = SPELLS.register("mend", MendSpell::new);
    // Wand
    public static final RegistrySupplier<Spell> WAND_ATTACK = SPELLS.register("focused_stream", MagicDamageSpell::new);
    public static final RegistrySupplier<Spell> WAND_INFUSE = SPELLS.register("focused_infuse", MagicInfuseSpell::new);
    public static final RegistrySupplier<Spell> WAND_EXTRACT = SPELLS.register("focused_extract", MagicExtractSpell::new);
}
