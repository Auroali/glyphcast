package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.client.screen.SpellWheelScreen;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.registry.*;
import com.auroali.glyphcast.common.spells.HoldSpell;
import com.auroali.glyphcast.common.spells.SpellStats;
import com.auroali.glyphcast.common.wands.WandCap;
import com.auroali.glyphcast.common.wands.WandCore;
import com.auroali.glyphcast.common.wands.WandMaterial;
import com.auroali.glyphcast.mixins.client.KeyMappingAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

public class WandItem extends Item implements IPointItem, IWandLike {
    private static final DecimalFormat STATS_FORMAT = new DecimalFormat("###");
    private static final DecimalFormat STATS_COOLDOWN_FORMAT = new DecimalFormat("##.#");

    public WandItem() {
        super(new Properties().stacksTo(1).tab(Glyphcast.GLYPHCAST_TAB).durability(250));
    }

    private static void openSpellWheelEditor(Level pLevel, Player pPlayer, ItemStack other) {
        if (pLevel.isClientSide) {
            ISpellHolder holder = (ISpellHolder) other.getItem();
            holder.getSpell(other).ifPresent(spell ->
                    SpellUser.get(pPlayer).ifPresent(user ->
                            SpellWheelScreen.openScreenWith(user.getManuallyAssignedSlots(), entry -> GCNetwork.CHANNEL.sendToServer(new SetSlotSpellMessage(entry.index, spell)), s -> {
                            }, false, true)
                    )
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        String coreKey = getCore(pStack).map(core -> {
            ResourceLocation location = GCWandCores.getKey(core);
            return "wand_core.%s.%s".formatted(location.getNamespace(), location.getPath());
        }).orElse("wand_core.none");

        pTooltipComponents.add(Component.translatable("item.glyphcast.wand.tooltip", Component.translatable(coreKey).withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), ((KeyMappingAccessor)Minecraft.getInstance().options.keySprint).getKey().getValue())) {
            pTooltipComponents.add(Component.translatable("tooltip.glyphcast.more_info", Component.keybind("key.sprint").withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            return;
        }
        SpellStats stats = buildStats(pStack);
        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.efficiency", STATS_FORMAT.format(stats.efficiency() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.cooldown", STATS_COOLDOWN_FORMAT.format(stats.cooldown() / 20.0)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.fireAffinity", STATS_FORMAT.format(stats.fireAffinity() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.lightAffinity", STATS_FORMAT.format(stats.lightAffinity() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.iceAffinity", STATS_FORMAT.format(stats.iceAffinity() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.earthAffinity", STATS_FORMAT.format(stats.earthAffinity() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }

    @Override
    public Component getName(ItemStack pStack) {
        WandMaterial mat = getMaterial(pStack).orElse(null);
        WandCap cap = getCap(pStack).orElse(null);
        ResourceLocation matLoc = mat != null ? GCWandMaterials.getKey(mat) : null;
        ResourceLocation capLoc = cap != null ? GCWandCaps.getKey(cap) : null;
        if (matLoc == null)
            return super.getName(pStack);
        if (capLoc != null)
            return Component.translatable("item.glyphcast.wand_capped", Component.translatable("wand_cap.%s.%s".formatted(capLoc.getNamespace(), capLoc.getPath())), Component.translatable("wand_material.%s.%s".formatted(matLoc.getNamespace(), matLoc.getPath())));
        return Component.translatable("item.glyphcast.wand", Component.translatable("wand_material.%s.%s".formatted(matLoc.getNamespace(), matLoc.getPath())));
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLivingEntity instanceof Player player) {
            player.getCooldowns().addCooldown(this, buildStats(pStack).cooldown());

            SpellUser.get(player)
                    .ifPresent(cap ->
                            cap.getTickingSpells().removeIf(data -> data.getSpell() instanceof HoldSpell)
                    );
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pCategory)) {
            for (ResourceLocation key : GCWandMaterials.KEY_MAP.keySet()) {
                ItemStack stack = new ItemStack(this);
                setCore(stack, new ResourceLocation(Glyphcast.MODID, "petal"));
                setCap(stack, new ResourceLocation(Glyphcast.MODID, "iron"));
                setMaterial(stack, key);
                pItems.add(stack);
            }
        }
    }

    public void setCore(ItemStack stack, ResourceLocation core) {
        stack.getOrCreateTag().putString("WandCore", core.toString());
    }

    public void setMaterial(ItemStack stack, ResourceLocation material) {
        stack.getOrCreateTag().putString("WandMaterial", material.toString());
    }

    public void setCap(ItemStack stack, ResourceLocation cap) {
        stack.getOrCreateTag().putString("WandCap", cap.toString());
    }

    public Optional<WandCore> getCore(ItemStack stack) {
        ResourceLocation location = new ResourceLocation(stack.getOrCreateTag().getString("WandCore"));
        return Optional.ofNullable(GCWandCores.getValue(location));
    }

    public Optional<WandMaterial> getMaterial(ItemStack stack) {
        ResourceLocation location = getMaterialResourceLocation(stack);
        return Optional.ofNullable(GCWandMaterials.getValue(location));
    }

    public Optional<WandCap> getCap(ItemStack stack) {
        ResourceLocation location = getCapResourceLocation(stack);
        return Optional.ofNullable(GCWandCaps.getValue(location));
    }

    public ResourceLocation getMaterialResourceLocation(ItemStack stack) {
        return stack.getOrCreateTag().contains("WandMaterial")
                ? new ResourceLocation(stack.getOrCreateTag().getString("WandMaterial"))
                : new ResourceLocation("", "");
    }

    public ResourceLocation getCapResourceLocation(ItemStack stack) {
        return stack.getOrCreateTag().contains("WandCap")
                ? new ResourceLocation(stack.getOrCreateTag().getString("WandCap"))
                : new ResourceLocation("", "");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        ItemStack other = pPlayer.getItemInHand(pUsedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (pPlayer.isCrouching() && other.is(GCItems.PARCHMENT.get())) {
            openSpellWheelEditor(pLevel, pPlayer, other);
            return InteractionResultHolder.sidedSuccess(stack, !pLevel.isClientSide);
        }
        if (!pLevel.isClientSide) {
            activateSpell(pLevel, pPlayer, pUsedHand, stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }

    private void activateSpell(Level pLevel, Player pPlayer, InteractionHand hand, ItemStack stack) {
        SpellUser.get(pPlayer).ifPresent(user -> {
            if (user.getSelectedSpell() != null) {
                Optional<WandMaterial> mat = getMaterial(stack);
                if (mat.isEmpty()) {
                    pPlayer.displayClientMessage(Component.translatable("msg.glyphcast.wand_material_error", stack.getOrCreateTag().getString("WandMaterial")).withStyle(ChatFormatting.RED), false);
                    return;
                }
                SpellStats stats = buildStats(stack);
                user.getSelectedSpell().tryActivate(pLevel, pPlayer, hand, stats);
                if (user.getSelectedSpell() instanceof HoldSpell)
                    pPlayer.startUsingItem(hand);
                else
                    pPlayer.getCooldowns().addCooldown(this, stats.cooldown());

                stack.hurtAndBreak(1, pPlayer, (p_35997_) -> {
                    p_35997_.broadcastBreakEvent(hand);
                });
            }
        });
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 7200;
    }


//    @Override
//    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
//        super.initializeClient(consumer);
//        consumer.accept(new IClientItemExtensions() {
//            private static final HumanoidModel.ArmPose POSE = HumanoidModel.ArmPose.create("GLYPHCAST_WAND", false, (model, entity, arm) -> {
//                if (arm == HumanoidArm.LEFT) {
//                    model.leftArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
//                    model.leftArm.yRot = 0.1F + model.head.yRot;
//                }
//                if (arm == HumanoidArm.RIGHT) {
//                    model.rightArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
//                    model.rightArm.yRot = -0.1F + model.head.yRot;
//                }
//            });
//
//            @Override
//            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
//                return entityLiving.getUsedItemHand() == hand && entityLiving.getUseItemRemainingTicks() > 0 ? POSE : HumanoidModel.ArmPose.EMPTY;
//            }
//        });
//    }


    public SpellStats buildStats(ItemStack stack) {
        SpellStats.Builder stats = new SpellStats.Builder();
        Optional<WandCore> core = getCore(stack);
        core.ifPresent(core1 -> core1.applyStats(stats));
        getMaterial(stack).ifPresent(mat -> mat.applyStats(stats));
        Optional<WandCap> cap = getCap(stack);
        cap.ifPresent(cap1 -> cap1.applyStats(stats));
        if (cap.isEmpty() && core.isPresent())
            stats.addEfficiency(-core.get().efficiency() / 2);
        return stats.build();
    }

    @Override
    public void transform(PoseStack stack) {
        stack.mulPose(Quaternion.fromXYZ((float) (-Math.PI / 2.3), 0, 0));
        stack.translate(0, 0.15, -0.2);
    }
}
