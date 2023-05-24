package com.auroali.glyphcast.common.damage.forge;

import net.minecraft.world.damagesource.DamageSource;

public class GCDamageSourcesImpl {
    public static DamageSource create(String str) {
        return new DamageSource(str);
    }

    public static DamageSource magic(DamageSource source) {
        return source.setMagic().bypassArmor();
    }

    public static DamageSource burn(DamageSource source) {
        return source.setIsFire().bypassArmor();
    }
}
