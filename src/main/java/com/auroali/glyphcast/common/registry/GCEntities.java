package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.entities.FireEntity;
import com.auroali.glyphcast.common.entities.LightEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GCEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GlyphCast.MODID);

    public static final RegistryObject<EntityType<FireEntity>> FIRE = ENTITIES.register("fire", () ->
            EntityType.Builder.<FireEntity>of(FireEntity::new, MobCategory.MISC)
            .clientTrackingRange(64)
            .fireImmune()
            .setShouldReceiveVelocityUpdates(true)
            .updateInterval(3)
            .sized(0.5f, 0.5f)
            .build(new ResourceLocation(GlyphCast.MODID, "fire").toString()));

    public static final RegistryObject<EntityType<LightEntity>> FLOATING_LIGHT = ENTITIES.register("floating_light", () ->
            EntityType.Builder.<LightEntity>of(LightEntity::new, MobCategory.MISC)
                    .clientTrackingRange(8)
                    .fireImmune()
                    .setShouldReceiveVelocityUpdates(true)
                    .updateInterval(20)
                    .sized(0.5f, 0.5f)
                    .build(new ResourceLocation(GlyphCast.MODID, "floating_light").toString()));
}
