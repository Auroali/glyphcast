//package com.auroali.glyphcast.common.items;
//
//import com.auroali.glyphcast.client.screen.SpellWheelScreen;
//import com.auroali.glyphcast.common.capabilities.SpellUser;
//import com.auroali.glyphcast.common.network.GCNetwork;
//import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
//import com.auroali.glyphcast.common.registry.GCItems;
//import com.auroali.glyphcast.common.spells.HoldSpell;
//import com.auroali.glyphcast.common.spells.glyph.Glyph;
//import com.auroali.glyphcast.mixins.client.KeyMappingAccessor;
//import com.mojang.blaze3d.platform.InputConstants;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.math.Quaternion;
//import net.minecraft.ChatFormatting;
//import net.minecraft.client.Minecraft;
//import net.minecraft.core.NonNullList;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.InteractionResultHolder;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityDimensions;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.*;
//import net.minecraft.world.level.Level;
//import org.jetbrains.annotations.Nullable;
//
//import java.text.DecimalFormat;
//import java.util.List;
//import java.util.UUID;
//
//public class StaffItem extends Item implements IPointItem, IWandLike {
//
//    public static final Variant[] VARIANTS = {
//            new Variant("cat", StaffClass.QUICK, Glyph.LIGHT, EntityDimensions.fixed(0.6f, 0.6f), false),
//            new Variant("dragon", StaffClass.AVERAGE, Glyph.FIRE, EntityDimensions.fixed(0.6f, 0.6f), true)
//    };
//
//    private static final DecimalFormat STATS_FORMAT = new DecimalFormat("###");
//    private static final DecimalFormat STATS_COOLDOWN_FORMAT = new DecimalFormat("##.#");
//
//    public StaffItem() {
//        super(new Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
//    }
//
//    private static void openSpellWheelEditor(Level pLevel, Player pPlayer, ItemStack other) {
//        if (pLevel.isClientSide) {
//            ISpellHolder holder = (ISpellHolder) other.getItem();
//            holder.getSpell(other).ifPresent(spell ->
//                    SpellUser.get(pPlayer).ifPresent(user ->
//                            SpellWheelScreen.openScreenWith(user.getManuallyAssignedSlots(), entry -> GCNetwork.CHANNEL.sendToServer(new SetSlotSpellMessage(entry.index, spell)), s -> {
//                            }, false, true)
//                    )
//            );
//        }
//    }
//
//    @Override
//    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
//        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
//
//        String coreKey = pStack.getOrCreateTag().getBoolean("Alive") ? "item.glyphcast.staff.tooltip.living" : "item.glyphcast.staff.tooltip.dormant";
//
//        pTooltipComponents.add(Component.translatable("item.glyphcast.wand.tooltip", Component.translatable(coreKey).withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
//
//        if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), ((KeyMappingAccessor) Minecraft.getInstance().options.keySprint).getKey().getValue())) {
//            pTooltipComponents.add(Component.translatable("tooltip.glyphcast.more_info", Component.keybind("key.sprint").withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
//            return;
//        }
//        SpellStats stats = buildStats(pStack);
//        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.efficiency", STATS_FORMAT.format(stats.efficiency() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
//        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.cooldown", STATS_COOLDOWN_FORMAT.format(stats.cooldown() / 20.0)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
//        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.fireAffinity", STATS_FORMAT.format(stats.fireAffinity() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
//        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.lightAffinity", STATS_FORMAT.format(stats.lightAffinity() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
//        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.iceAffinity", STATS_FORMAT.format(stats.iceAffinity() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
//        pTooltipComponents.add(Component.translatable("wand_stat.glyphcast.earthAffinity", STATS_FORMAT.format(stats.earthAffinity() * 100)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
//    }
//
//    @Override
//    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
//        if (pLivingEntity instanceof Player player) {
//            player.getCooldowns().addCooldown(this, buildStats(pStack).cooldown());
//
//            SpellUser.get(player)
//                    .ifPresent(cap ->
//                            cap.getTickingSpells().removeIf(data -> data.getSpell() instanceof HoldSpell)
//                    );
//        }
//    }
//
//    @Override
//    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
//        if (this.allowedIn(pCategory)) {
//            for (Variant variant : VARIANTS) {
//                ItemStack stack = new ItemStack(this);
//                setVariant(stack, variant.name);
//                pItems.add(stack);
//                ItemStack stack1 = new ItemStack(this);
//                setVariant(stack1, variant.name);
//                setAlive(stack1, true);
//                pItems.add(stack1);
//            }
//        }
//    }
//
//    public void setEntityUUID(ItemStack stack, UUID uuid) {
//        stack.getOrCreateTag().putUUID("UUID", uuid);
//    }
//
//    public void setPresent(ItemStack stack, boolean present) {
//        stack.getOrCreateTag().putBoolean("Present", present);
//    }
//
//    void setVariant(ItemStack stack, String variant) {
//        stack.getOrCreateTag().putString("Variant", variant);
//    }
//
//    void setAlive(ItemStack stack, boolean alive) {
//        stack.getOrCreateTag().putBoolean("Alive", alive);
//        if (alive)
//            stack.getOrCreateTag().putBoolean("Present", true);
//    }
//
//    public Variant getVariant(ItemStack stack) {
//        String variant = stack.getOrCreateTag().getString("Variant");
//        for (Variant v : VARIANTS) {
//            if (v.name.equals(variant))
//                return v;
//        }
//        return null;
//    }
//
//    @Override
//    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
//        if (!pStack.getOrCreateTag().getBoolean("Alive") || pStack.getOrCreateTag().getBoolean("Present") || !pStack.getOrCreateTag().getUUID("UUID").equals(pInteractionTarget.getUUID()))
//            return InteractionResult.PASS;
//
//
//        setPresent(pPlayer.getItemInHand(pUsedHand), true);
//        pInteractionTarget.remove(Entity.RemovalReason.DISCARDED);
//
//        return InteractionResult.sidedSuccess(pPlayer.level.isClientSide);
//    }
//
//    @Override
//    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
//        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
//        ItemStack other = pPlayer.getItemInHand(pUsedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
//        if (pPlayer.isCrouching() && other.is(GCItems.PARCHMENT.get())) {
//            openSpellWheelEditor(pLevel, pPlayer, other);
//            return InteractionResultHolder.sidedSuccess(stack, !pLevel.isClientSide);
//        }
//        if (!pLevel.isClientSide) {
//            activateSpell(pLevel, pPlayer, pUsedHand, stack);
//        }
//        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
//    }
//
//    private void activateSpell(Level pLevel, Player pPlayer, InteractionHand hand, ItemStack stack) {
//        SpellUser.get(pPlayer).ifPresent(user -> {
//            if (user.getSelectedSpell() != null) {
//                SpellStats stats = buildStats(stack);
//                user.getSelectedSpell().tryActivate(pLevel, pPlayer, hand, stats);
//                if (user.getSelectedSpell() instanceof HoldSpell)
//                    pPlayer.startUsingItem(hand);
//                else
//                    pPlayer.getCooldowns().addCooldown(this, stats.cooldown());
//            }
//        });
//    }
//
//    public SpellStats buildStats(ItemStack stack) {
//        Variant variant = getVariant(stack);
//        if (variant == null)
//            return SpellStats.PARCHMENT;
//        return variant.stats();
//    }
//
//    @Override
//    public int getUseDuration(ItemStack pStack) {
//        return 7200;
//    }
//
////    @Override
////    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
////        super.initializeClient(consumer);
////        consumer.accept(new IClientItemExtensions() {
////            private static final HumanoidModel.ArmPose POSE = HumanoidModel.ArmPose.create("GLYPHCAST_WAND", false, (model, entity, arm) -> {
////                if (arm == HumanoidArm.LEFT) {
////                    model.leftArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
////                    model.leftArm.yRot = 0.1F + model.head.yRot;
////                }
////                if (arm == HumanoidArm.RIGHT) {
////                    model.rightArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
////                    model.rightArm.yRot = -0.1F + model.head.yRot;
////                }
////            });
////
////            @Override
////            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
////                return entityLiving.getUsedItemHand() == hand && entityLiving.getUseItemRemainingTicks() > 0 ? POSE : HumanoidModel.ArmPose.EMPTY;
////            }
////        });
////    }
//
//    @Override
//    public void transform(PoseStack stack) {
//        stack.mulPose(Quaternion.fromXYZ((float) (-Math.PI / 2.6), 0, 0));
//        stack.translate(0, 0.15, -0.2);
//    }
//
//    public enum StaffClass {
//        QUICK,
//        AVERAGE,
//        SLOW
//    }
//
//    public record Variant(String name, StaffClass staffClass, Glyph affinity, EntityDimensions dimensions,
//                          boolean flying) {
//        SpellStats stats() {
//            SpellStats.Builder builder = new SpellStats.Builder();
//            switch (affinity) {
//                case FIRE -> builder.addFireAffinity(0.15);
//                case LIGHT -> builder.addLightAffinity(0.15);
//                case ICE -> builder.addEarthAffinity(0.15);
//                case EARTH -> builder.addIceAffinity(0.15);
//            }
//            switch (staffClass) {
//                case QUICK ->
//                        builder.addCooldown(10).addEarthAffinity(-0.1).addFireAffinity(-0.1).addIceAffinity(-0.1).addLightAffinity(-0.1);
//                case AVERAGE -> builder.addCooldown(12);
//                case SLOW ->
//                        builder.addCooldown(14).addEarthAffinity(0.1).addFireAffinity(0.1).addIceAffinity(0.1).addLightAffinity(0.1);
//            }
//            return builder
//                    .addEfficiency(1.0)
//                    .addFireAffinity(1.0)
//                    .addLightAffinity(1.0)
//                    .addIceAffinity(1.0)
//                    .addEarthAffinity(1.0)
//                    .build();
//        }
//    }
//}
