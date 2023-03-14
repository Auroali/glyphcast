package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.*;
import com.auroali.glyphcast.common.spells.composite.MistSpell;
import com.auroali.glyphcast.common.spells.composite.PushSpell;
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
}
