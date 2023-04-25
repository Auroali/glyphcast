package com.auroali.glyphcast.client.render.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public abstract class MultiModelLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>, K> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float EYE_BED_OFFSET = 0.1F;
    public final HashMap<K, M> models;
    public final HashMap<K, List<RenderLayer<T, M>>> renderLayers;

    public MultiModelLivingEntityRenderer(EntityRendererProvider.Context pContext, float pShadowRadius) {
        super(pContext);
        models = new HashMap<>();
        this.renderLayers = new HashMap<>();
        List<ModelRegEntry<K, T, M>> registry = new ArrayList<>();
        registerModels(registry);
        registry.forEach(e -> {
            models.put(e.key, e.bake(pContext));
            renderLayers.put(e.key, e.getLayers());
        });

        this.shadowRadius = pShadowRadius;
    }

    public static int getOverlayCoords(LivingEntity pLivingEntity, float pU) {
        return OverlayTexture.pack(OverlayTexture.u(pU), OverlayTexture.v(pLivingEntity.hurtTime > 0 || pLivingEntity.deathTime > 0));
    }

    private static float sleepDirectionToRotation(Direction pFacing) {
        return switch (pFacing) {
            case SOUTH -> 90.0F;
            case NORTH -> 270.0F;
            case EAST -> 180.0F;
            default -> 0.0F;
        };
    }

    public static boolean isEntityUpsideDown(LivingEntity pEntity) {
        if (pEntity instanceof Player || pEntity.hasCustomName()) {
            String s = ChatFormatting.stripFormatting(pEntity.getName().getString());
            if ("Dinnerbone".equals(s) || "Grumm".equals(s)) {
                return !(pEntity instanceof Player) || ((Player) pEntity).isModelPartShown(PlayerModelPart.CAPE);
            }
        }

        return false;
    }

    public M getModel() {
        return models.values().iterator().next();
    }

    public abstract List<RenderLayer<T, M>> getLayers(T entity);

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        M model = getCurrentModel(pEntity);
        model.attackTime = this.getAttackAnim(pEntity, pPartialTicks);

        boolean shouldSit = pEntity.isPassenger() && (pEntity.getVehicle() != null && pEntity.getVehicle().shouldRiderSit());
        model.riding = shouldSit;
        model.young = pEntity.isBaby();
        float f = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
        float f1 = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
        float f2 = f1 - f;
        if (shouldSit && pEntity.getVehicle() instanceof LivingEntity livingentity) {
            f = Mth.rotLerp(pPartialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            f2 = f1 - f;
            float f3 = Mth.wrapDegrees(f2);
            if (f3 < -85.0F) {
                f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
                f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
                f += f3 * 0.2F;
            }

            f2 = f1 - f;
        }

        float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        if (isEntityUpsideDown(pEntity)) {
            f6 *= -1.0F;
            f2 *= -1.0F;
        }

        if (pEntity.hasPose(Pose.SLEEPING)) {
            Direction direction = pEntity.getBedOrientation();
            if (direction != null) {
                float f4 = pEntity.getEyeHeight(Pose.STANDING) - 0.1F;
                pMatrixStack.translate((float) (-direction.getStepX()) * f4, 0.0D, (float) (-direction.getStepZ()) * f4);
            }
        }

        float f7 = this.getBob(pEntity, pPartialTicks);
        this.setupRotations(pEntity, pMatrixStack, f7, f, pPartialTicks);
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(pEntity, pMatrixStack, pPartialTicks);
        pMatrixStack.translate(0.0D, -1.501F, 0.0D);
        float f8 = 0.0F;
        float f5 = 0.0F;
        if (!shouldSit && pEntity.isAlive()) {
            f8 = Mth.lerp(pPartialTicks, pEntity.animationSpeedOld, pEntity.animationSpeed);
            f5 = pEntity.animationPosition - pEntity.animationSpeed * (1.0F - pPartialTicks);
            if (pEntity.isBaby()) {
                f5 *= 3.0F;
            }

            if (f8 > 1.0F) {
                f8 = 1.0F;
            }
        }

        model.prepareMobModel(pEntity, f5, f8, pPartialTicks);
        model.setupAnim(pEntity, f5, f8, f7, f2, f6);
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = this.isBodyVisible(pEntity);
        boolean flag1 = !flag && !pEntity.isInvisibleTo(minecraft.player);
        boolean flag2 = minecraft.shouldEntityAppearGlowing(pEntity);
        RenderType rendertype = this.getRenderType(pEntity, flag, flag1, flag2);
        if (rendertype != null) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(rendertype);
            int i = getOverlayCoords(pEntity, this.getWhiteOverlayProgress(pEntity, pPartialTicks));
            model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
        }

        if (!pEntity.isSpectator()) {
            for (RenderLayer<T, M> renderlayer : this.getLayers(pEntity)) {
                renderlayer.render(pMatrixStack, pBuffer, pPackedLight, pEntity, f5, f8, pPartialTicks, f7, f2, f6);
            }
        }

        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    public abstract M getCurrentModel(T entity);

    abstract void registerModels(List<ModelRegEntry<K, T, M>> registry);

    @Nullable
    protected RenderType getRenderType(T pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        ResourceLocation resourcelocation = this.getTextureLocation(pLivingEntity);
        if (pTranslucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (pBodyVisible) {
            return getCurrentModel(pLivingEntity).renderType(resourcelocation);
        } else {
            return pGlowing ? RenderType.outline(resourcelocation) : null;
        }
    }

    protected boolean isBodyVisible(T pLivingEntity) {
        return !pLivingEntity.isInvisible();
    }

    protected boolean isShaking(T pEntity) {
        return pEntity.isFullyFrozen();
    }

    protected void setupRotations(T pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        if (this.isShaking(pEntityLiving)) {
            pRotationYaw += (float) (Math.cos((double) pEntityLiving.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }

        if (!pEntityLiving.hasPose(Pose.SLEEPING)) {
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - pRotationYaw));
        }

        if (pEntityLiving.deathTime > 0) {
            float f = ((float) pEntityLiving.deathTime + pPartialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(f * this.getFlipDegrees(pEntityLiving)));
        } else if (pEntityLiving.isAutoSpinAttack()) {
            pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F - pEntityLiving.getXRot()));
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(((float) pEntityLiving.tickCount + pPartialTicks) * -75.0F));
        } else if (pEntityLiving.hasPose(Pose.SLEEPING)) {
            Direction direction = pEntityLiving.getBedOrientation();
            float f1 = direction != null ? sleepDirectionToRotation(direction) : pRotationYaw;
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(f1));
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(this.getFlipDegrees(pEntityLiving)));
            pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(270.0F));
        } else if (isEntityUpsideDown(pEntityLiving)) {
            pMatrixStack.translate(0.0D, pEntityLiving.getBbHeight() + 0.1F, 0.0D);
            pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        }

    }

    /**
     * Returns where in the swing animation the living entity is (from 0 to 1).  Args : entity, partialTickTime
     */
    protected float getAttackAnim(T pLivingBase, float pPartialTickTime) {
        return pLivingBase.getAttackAnim(pPartialTickTime);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float getBob(T pLivingBase, float pPartialTicks) {
        return (float) pLivingBase.tickCount + pPartialTicks;
    }

    protected float getFlipDegrees(T pLivingEntity) {
        return 90.0F;
    }

    protected float getWhiteOverlayProgress(T pLivingEntity, float pPartialTicks) {
        return 0.0F;
    }

    protected void scale(T pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
    }

    protected boolean shouldShowName(T pEntity) {
        double d0 = this.entityRenderDispatcher.distanceToSqr(pEntity);
        float f = pEntity.isDiscrete() ? 32.0F : 64.0F;
        if (d0 >= (double) (f * f)) {
            return false;
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localplayer = minecraft.player;
            boolean flag = !pEntity.isInvisibleTo(localplayer);
            if (pEntity != localplayer) {
                Team team = pEntity.getTeam();
                Team team1 = localplayer.getTeam();
                if (team != null) {
                    Team.Visibility team$visibility = team.getNameTagVisibility();
                    return switch (team$visibility) {
                        case ALWAYS -> flag;
                        case NEVER -> false;
                        case HIDE_FOR_OTHER_TEAMS ->
                                team1 == null ? flag : team.isAlliedTo(team1) && (team.canSeeFriendlyInvisibles() || flag);
                        case HIDE_FOR_OWN_TEAM -> team1 == null ? flag : !team.isAlliedTo(team1) && flag;
                    };
                }
            }

            return Minecraft.renderNames() && pEntity != minecraft.getCameraEntity() && flag && !pEntity.isVehicle();
        }
    }

    public static class ModelRegEntry<T, E extends LivingEntity, U extends EntityModel<E>> {
        final T key;
        private final Function<ModelPart, U> factory;
        private final ModelLayerLocation location;
        private final ImmutableList.Builder<RenderLayer<E, U>> builder;

        ModelRegEntry(T key, Function<ModelPart, U> factory, ModelLayerLocation location) {
            this.key = key;
            this.factory = factory;
            this.location = location;
            this.builder = new ImmutableList.Builder<>();
        }

        void addRenderLayer(RenderLayer<E, U> layer) {
            builder.add(layer);
        }

        List<RenderLayer<E, U>> getLayers() {
            return builder.build();
        }

        U bake(EntityRendererProvider.Context ctx) {
            return factory.apply(ctx.bakeLayer(location));
        }
    }
}
