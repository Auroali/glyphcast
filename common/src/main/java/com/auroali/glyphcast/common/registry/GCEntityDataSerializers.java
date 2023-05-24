package com.auroali.glyphcast.common.registry;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class GCEntityDataSerializers {
    public static final EntityDataSerializer<Double> DOUBLE = EntityDataSerializer.simple(FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble);
}
