package com.auroali.glyphcast.mixins.client;

import com.auroali.glyphcast.common.items.WandItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandMixin {
    @Inject(method = "renderArmWithItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            shift = At.Shift.BEFORE
    ))
    public void glyphcast$renderArmWithItemPre(LivingEntity pLivingEntity, ItemStack pItemStack, ItemTransforms.TransformType pTransformType, HumanoidArm pArm, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        if (pLivingEntity.getUseItem().getItem() instanceof WandItem && pLivingEntity.getUseItemRemainingTicks() > 0) {
            pPoseStack.pushPose();
            pPoseStack.mulPose(Quaternion.fromXYZ((float) (-Math.PI / 2.3), 0, 0));
            pPoseStack.translate(0, 0.15, -0.2);
        }
    }

    @Inject(method = "renderArmWithItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            shift = At.Shift.AFTER
    ))
    public void glyphcast$renderArmWithItemPost(LivingEntity pLivingEntity, ItemStack pItemStack, ItemTransforms.TransformType pTransformType, HumanoidArm pArm, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        if (pLivingEntity.getUseItem().getItem() instanceof WandItem && pLivingEntity.getUseItemRemainingTicks() > 0)
            pPoseStack.popPose();
    }
}
