package com.auroali.glyphcast.common.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class GCDamageSources {
    public static final DamageSource MAGIC = new DamageSource("glyphcast.magic").setMagic().bypassArmor();
    public static final DamageSource BURN = new DamageSource("glyphcast.burn").bypassArmor().setIsFire();

    public static DamageSource magic(Entity entity) {

        return new EntityDamageSource("glyphcast.magic.entity", entity).setMagic().bypassArmor();
    }

    public static DamageSource burn(Entity entity) {

        return new EntityDamageSource("glyphcast.burn.entity", entity).bypassArmor().setIsFire();
    }

    public static DamageSource burnIndirect(Entity entity, Entity source) {

        return new IndirectEntityDamageSource("glyphcast.burn.entity", entity, source).bypassArmor().setIsFire();
    }
}
