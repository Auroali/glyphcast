package com.auroali.glyphcast.common.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

public class GCDamageSources {
    public static final DamageSource MAGIC = new DamageSource("glyphcast.magic").setMagic().bypassArmor();

    public static DamageSource magic(Entity entity) {
        return new EntityDamageSource("glyphcast.magic", entity).setMagic().bypassArmor();
    }
}
