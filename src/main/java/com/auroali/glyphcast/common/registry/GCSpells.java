package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.*;
import com.auroali.glyphcast.common.spells.composite.*;
import com.auroali.glyphcast.common.spells.wand.MagicDamageSpell;
import com.auroali.glyphcast.common.spells.wand.MagicExtractSpell;
import com.auroali.glyphcast.common.spells.wand.MagicInfuseSpell;
import com.auroali.glyphcast.common.spells.wand.staff.SeperateStaffSpell;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class GCSpells {
    public static final ResourceKey<Registry<Spell>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(GlyphCast.MODID, "spells"));
    public static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(REGISTRY_KEY, GlyphCast.MODID);
    public static final RegistryObject<Spell> FIRE_SPELL = SPELLS.register("fire", FireSpell::new);
    public static final RegistryObject<Spell> LIGHT_SPELL = SPELLS.register("light", LightSpell::new);
    public static final RegistryObject<Spell> ICE_SPELL = SPELLS.register("ice", IceSpell::new);
    public static final RegistryObject<Spell> EARTH_SPELL = SPELLS.register("earth", EarthSpell::new);
    public static final RegistryObject<Spell> MIST = SPELLS.register("mist", MistSpell::new);
    public static final RegistryObject<Spell> PUSH = SPELLS.register("push", PushSpell::new);
    public static final RegistryObject<Spell> WAND_ATTACK = SPELLS.register("wand_attack", MagicDamageSpell::new);
    public static final RegistryObject<Spell> FLARE = SPELLS.register("flare", FlareSpell::new);
    public static final RegistryObject<Spell> INFUSE = SPELLS.register("infuse", MagicInfuseSpell::new);
    public static final RegistryObject<Spell> EXTRACT = SPELLS.register("extract", MagicExtractSpell::new);
    public static final RegistryObject<Spell> SHIELD = SPELLS.register("shield", ShieldSpell::new);
    public static final RegistryObject<Spell> SEPERATE_STAFF = SPELLS.register("seperate_staff", SeperateStaffSpell::new);
    public static final RegistryObject<Spell> MEND_ITEM = SPELLS.register("mend_item", MendItemSpell::new);
}
