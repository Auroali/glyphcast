package com.auroali.glyphcast.common.damage.forge;

import net.minecraft.world.damagesource.DamageSource;

public class GCDamageSourcesImpl {
    private static DamageSource create(String str) {
        return new DamageSource(str);
    }

    private static DamageSource magic(DamageSource source) {
        return source.setMagic().bypassArmor();
    }

    private static DamageSource burn(DamageSource source) {
        return source.setIsFire().bypassArmor();
    }
}
