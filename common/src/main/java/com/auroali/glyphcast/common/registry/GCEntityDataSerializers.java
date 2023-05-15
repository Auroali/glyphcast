package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.items.StaffItem;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;

public class GCEntityDataSerializers {
    //public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(Glyphcast.MODID, F)
    public static final EntityDataSerializer<StaffItem.Variant> STAFF_VARIANT = new EntityDataSerializer.ForValueType<>() {
        @Override
        public void write(FriendlyByteBuf pBuffer, StaffItem.Variant pValue) {
            pBuffer.writeUtf(pValue.name());
        }

        @Override
        public StaffItem.Variant read(FriendlyByteBuf pBuffer) {
            String name = pBuffer.readUtf();
            for (StaffItem.Variant variant : StaffItem.VARIANTS) {
                if (name.equals(variant.name()))
                    return variant;
            }
            return null;
        }

        @Override
        public StaffItem.Variant copy(StaffItem.Variant pValue) {
            return pValue;
        }
    };

//    public static void register(Event event) {
//        event.register(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, helper -> {
//            helper.register("staff_variant", STAFF_VARIANT);
//        });
//    }

}
