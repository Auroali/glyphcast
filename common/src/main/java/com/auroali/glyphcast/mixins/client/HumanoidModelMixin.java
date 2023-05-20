package com.auroali.glyphcast.mixins.client;

import com.auroali.glyphcast.common.registry.tags.GCItemTags;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("RETURN"))
    public void glyphcast$setupAnimMixin(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof Player player) {
            if (!player.isUsingItem() || !player.getUseItem().is(GCItemTags.POINT_ON_USE))
                return;

            if (getArmFromHand(player, player.getUsedItemHand()) == HumanoidArm.RIGHT) {
                ((HumanoidModel<T>) (Object) this).rightArm.xRot = -1.39626f + ((HumanoidModel<T>) (Object) this).head.xRot;
                ((HumanoidModel<T>) (Object) this).rightArm.yRot = -.08726f + ((HumanoidModel<T>) (Object) this).head.yRot;
            }
            if (getArmFromHand(player, player.getUsedItemHand()) == HumanoidArm.LEFT) {
                ((HumanoidModel<T>) (Object) this).leftArm.xRot = -1.39626f + ((HumanoidModel<T>) (Object) this).head.xRot;
                ((HumanoidModel<T>) (Object) this).leftArm.yRot = .08726f + ((HumanoidModel<T>) (Object) this).head.yRot;
            }
        }
    }

    HumanoidArm getArmFromHand(Player player, InteractionHand hand) {
        return switch (hand) {
            case MAIN_HAND -> player.getMainArm() == HumanoidArm.RIGHT ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
            case OFF_HAND -> player.getMainArm() == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        };
    }
}
