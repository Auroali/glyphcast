package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.entities.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class GCEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Glyphcast.MODID, Registry.ENTITY_TYPE_REGISTRY);

    public static final RegistrySupplier<EntityType<FireSpellProjectile>> FIRE = ENTITIES.register("fire", () ->
            EntityType.Builder.<FireSpellProjectile>of(FireSpellProjectile::new, MobCategory.MISC)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .updateInterval(3)
                    .sized(0.5f, 0.5f)
                    .build(new ResourceLocation(Glyphcast.MODID, "fire").toString()));

    public static final RegistrySupplier<EntityType<FloatingLight>> FLOATING_LIGHT = ENTITIES.register("floating_light", () ->
            EntityType.Builder.<FloatingLight>of(FloatingLight::new, MobCategory.MISC)
                    .clientTrackingRange(8)
                    .fireImmune()
                    .updateInterval(20)
                    .sized(0.5f, 0.5f)
                    .build(new ResourceLocation(Glyphcast.MODID, "floating_light").toString()));

    public static final RegistrySupplier<EntityType<Flare>> FLARE = ENTITIES.register("flare", () ->
            EntityType.Builder.<Flare>of(Flare::new, MobCategory.MISC)
                    .clientTrackingRange(256)
                    .fireImmune()
                    .updateInterval(3)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(Glyphcast.MODID, "flare").toString()));

    public static final RegistrySupplier<EntityType<FractureEntity>> FRACTURE = ENTITIES.register("fracture", () ->
            EntityType.Builder.<FractureEntity>of(FractureEntity::new, MobCategory.MISC)
                    .clientTrackingRange(64)
                    .fireImmune()
                    .updateInterval(20)
                    .sized(0.25f, 0.25f)
                    .build(new ResourceLocation(Glyphcast.MODID, "fracture").toString()));
}
