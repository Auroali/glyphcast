package com.auroali.glyphcast.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class GCCommonConfig {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final GCCommonConfig COMMON;

    static {
        Pair<GCCommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                .configure(GCCommonConfig::new);
        COMMON = pair.getLeft();
        COMMON_SPEC = pair.getRight();
    }

    public GCCommonConfig(ForgeConfigSpec.Builder builder) {

    }

}
