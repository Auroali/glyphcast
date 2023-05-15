package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.spells.*;
import com.auroali.glyphcast.common.spells.composite.*;
import com.auroali.glyphcast.common.spells.wand.MagicDamageSpell;
import com.auroali.glyphcast.common.spells.wand.MagicExtractSpell;
import com.auroali.glyphcast.common.spells.wand.MagicInfuseSpell;
import com.auroali.glyphcast.common.spells.wand.staff.SeperateStaffSpell;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

@SuppressWarnings("unused")
public class GCSpells {

    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(Glyphcast.MODID, Glyphcast.SPELL_REGISTRY);
    public static final RegistrySupplier<Spell> FIRE_SPELL = SPELLS.register("fire", FireSpell::new);
    public static final RegistrySupplier<Spell> LIGHT_SPELL = SPELLS.register("light", LightSpell::new);
    public static final RegistrySupplier<Spell> ICE_SPELL = SPELLS.register("ice", IceSpell::new);
    public static final RegistrySupplier<Spell> EARTH_SPELL = SPELLS.register("earth", EarthSpell::new);
    public static final RegistrySupplier<Spell> MIST = SPELLS.register("mist", MistSpell::new);
    public static final RegistrySupplier<Spell> PUSH = SPELLS.register("push", PushSpell::new);
    public static final RegistrySupplier<Spell> WAND_ATTACK = SPELLS.register("wand_attack", MagicDamageSpell::new);
    public static final RegistrySupplier<Spell> FLARE = SPELLS.register("flare", FlareSpell::new);
    public static final RegistrySupplier<Spell> INFUSE = SPELLS.register("infuse", MagicInfuseSpell::new);
    public static final RegistrySupplier<Spell> EXTRACT = SPELLS.register("extract", MagicExtractSpell::new);
    public static final RegistrySupplier<Spell> SHIELD = SPELLS.register("shield", ShieldSpell::new);
    public static final RegistrySupplier<Spell> SEPERATE_STAFF = SPELLS.register("seperate_staff", SeperateStaffSpell::new);
    public static final RegistrySupplier<Spell> MEND_ITEM = SPELLS.register("mend_item", MendItemSpell::new);
}
