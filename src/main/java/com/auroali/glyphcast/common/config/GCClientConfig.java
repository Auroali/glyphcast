package com.auroali.glyphcast.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class GCClientConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final GCClientConfig CLIENT;
    static {
        Pair<GCClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                .configure(GCClientConfig::new);
        CLIENT = pair.getLeft();
        CLIENT_SPEC = pair.getRight();
    }

    public ForgeConfigSpec.ConfigValue<Integer> updateFrequency;
    public GCClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("lighting");
        updateFrequency = builder.comment("The frequency at which the dynamic lighting updates").define("updateFrequency", 10);
        builder.pop();
    }
}
