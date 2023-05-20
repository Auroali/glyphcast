package com.auroali.glyphcast.common.capabilities.fabric;

import com.auroali.glyphcast.Glyphcast;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;

public class GCComponents implements EntityComponentInitializer {
    public static final ComponentKey<SpellUserImpl> SPELL_USER = ComponentRegistry.getOrCreate(new ResourceLocation(Glyphcast.MODID, "spell_user"), SpellUserImpl.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(SPELL_USER, SpellUserImpl::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
