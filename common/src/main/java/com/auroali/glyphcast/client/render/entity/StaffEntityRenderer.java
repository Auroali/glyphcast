//package com.auroali.glyphcast.client.render.entity;
//
//import com.auroali.glyphcast.Glyphcast;
//import com.auroali.glyphcast.client.model.entity.StaffCatModel;
//import com.auroali.glyphcast.client.render.entity.layers.StaffCatEyesLayer;
//import com.auroali.glyphcast.common.entities.StaffEntity;
//import com.auroali.glyphcast.common.items.StaffItem;
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.model.EntityModel;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.entity.EntityRendererProvider;
//import net.minecraft.client.renderer.entity.layers.RenderLayer;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//
//import java.util.List;
//
//public class StaffEntityRenderer extends MultiModelLivingEntityRenderer<StaffEntity, EntityModel<StaffEntity>, StaffItem.Variant> {
//    public static final ResourceLocation LOCATION = new ResourceLocation(Glyphcast.MODID, "textures/entity/staff/cat.png");
//
//    public StaffEntityRenderer(EntityRendererProvider.Context pContext) {
//        super(pContext, 0.25f);
//    }
//
//    @Override
//    public List<RenderLayer<StaffEntity, EntityModel<StaffEntity>>> getLayers(StaffEntity entity) {
//        return renderLayers.get(entity.getEntityData().get(StaffEntity.VARIANT));
//    }
//
//    @Override
//    public void render(StaffEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
//        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
//    }
//
//    @Override
//    public EntityModel<StaffEntity> getCurrentModel(StaffEntity entity) {
//        return models.get(entity.getEntityData().get(StaffEntity.VARIANT));
//    }
//
//    @Override
//    void registerModels(List<ModelRegEntry<StaffItem.Variant, StaffEntity, EntityModel<StaffEntity>>> registry) {
//        ModelRegEntry<StaffItem.Variant, StaffEntity, EntityModel<StaffEntity>> entry = new ModelRegEntry<>(StaffItem.VARIANTS[0], StaffCatModel::new, StaffCatModel.LAYER_LOCATION);
//        entry.addRenderLayer(new StaffCatEyesLayer(this));
//        registry.add(entry);
//    }
//
//    @Override
//    protected void renderNameTag(StaffEntity pEntity, Component pDisplayName, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
//        if (pEntity.hasCustomName())
//            super.renderNameTag(pEntity, pDisplayName, pMatrixStack, pBuffer, pPackedLight);
//    }
//
//    @Override
//    public ResourceLocation getTextureLocation(StaffEntity pEntity) {
//        return LOCATION;
//    }
//}
