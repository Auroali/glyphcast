package com.auroali.glyphcast.common.damage;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class GCDamageSources {
    public static final DamageSource MAGIC = magic(create("glyphcast.magic"));
    public static final DamageSource BURN = burn(create("glyphcast.burn"));

    public static DamageSource magic(Entity entity) {

        return magic(new EntityDamageSource("glyphcast.magic.entity", entity));
    }

    public static DamageSource burn(Entity entity) {

        return burn(new EntityDamageSource("glyphcast.burn.entity", entity));
    }

    public static DamageSource burnIndirect(Entity entity, Entity source) {

        return burn(new IndirectEntityDamageSource("glyphcast.burn.entity", entity, source));
    }

    @ExpectPlatform
    private static DamageSource create(String str) {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static DamageSource burn(DamageSource source) {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static DamageSource magic(DamageSource source) {
        throw new AssertionError();
    }
}
