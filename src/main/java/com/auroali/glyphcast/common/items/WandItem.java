package com.auroali.glyphcast.common.items;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.client.screen.SpellWheelScreen;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.server.SetSlotSpellMessage;
import com.auroali.glyphcast.common.registry.*;
import com.auroali.glyphcast.common.spells.SpellStats;
import com.auroali.glyphcast.common.wands.WandCap;
import com.auroali.glyphcast.common.wands.WandCore;
import com.auroali.glyphcast.common.wands.WandMaterial;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

public class WandItem extends Item {
    private static final DecimalFormat STATS_FORMAT = new DecimalFormat("###");
    private static final DecimalFormat STATS_COOLDOWN_FORMAT = new DecimalFormat("##.#");
    public WandItem() {
        super(new Properties().stacksTo(1).tab(GlyphCast.GLYPHCAST_TAB));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        String coreKey = getCore(pStack).map(core -> {
            ResourceLocation location = GCWandCores.getKey(core);
            return "wand_core.%s.%s".formatted(location.getNamespace(), location.getPath());
        }).orElse("wand_core.none");

        pTooltipComponents.add(Component.translatable("item.glyphcast.wand.tooltip", Component.translatable(coreKey).withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        if(!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), Minecraft.getInstance().options.keySprint.getKey().getValue())) {
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
        if(matLoc == null)
            return super.getName(pStack);
        if(capLoc != null)
            return Component.translatable("item.glyphcast.wand_capped", Component.translatable("wand_cap.%s.%s".formatted(capLoc.getNamespace(), capLoc.getPath())), Component.translatable("wand_material.%s.%s".formatted(matLoc.getNamespace(), matLoc.getPath())));
        return Component.translatable("item.glyphcast.wand", Component.translatable("wand_material.%s.%s".formatted(matLoc.getNamespace(), matLoc.getPath())));
    }
    @Override
    public void fillItemCategory(CreativeModeTab pCategory, NonNullList<ItemStack> pItems) {
        if (this.allowedIn(pCategory)) {
            for(ResourceLocation key : GCWandMaterials.KEY_MAP.keySet()) {
                ItemStack stack = new ItemStack(this);
                setCore(stack, new ResourceLocation(GlyphCast.MODID, "petal"));
                setCap(stack, new ResourceLocation(GlyphCast.MODID, "iron"));
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
        ResourceLocation location = new ResourceLocation(stack.getOrCreateTag().getString("WandMaterial"));
        return Optional.ofNullable(GCWandMaterials.getValue(location));
    }
    public Optional<WandCap> getCap(ItemStack stack) {
        ResourceLocation location = new ResourceLocation(stack.getOrCreateTag().getString("WandCap"));
        return Optional.ofNullable(GCWandCaps.getValue(location));
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        ItemStack other = pPlayer.getItemInHand(pUsedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if(pPlayer.isCrouching() && other.is(GCItems.PARCHMENT.get())) {
            openSpellWheelEditor(pLevel, pPlayer, other);
            return InteractionResultHolder.sidedSuccess(stack, !pLevel.isClientSide);
        }
        if(!pLevel.isClientSide) {
            activateSpell(pLevel, pPlayer, stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
    }

    private void activateSpell(Level pLevel, Player pPlayer, ItemStack stack) {
        SpellUser.get(pPlayer).ifPresent(user -> {
            if(user.getSelectedSpell() != null) {
                Optional<WandMaterial> mat = getMaterial(stack);
                if(mat.isEmpty()) {
                    pPlayer.displayClientMessage(Component.translatable("msg.glyphcast.wand_material_error", stack.getOrCreateTag().getString("WandMaterial")).withStyle(ChatFormatting.RED), false);
                    return;
                }
                SpellStats stats = buildStats(stack);
                user.getSelectedSpell().tryActivate(pLevel, pPlayer, stats);
                pPlayer.getCooldowns().addCooldown(this, stats.cooldown());
            }
        });
    }

    private static void openSpellWheelEditor(Level pLevel, Player pPlayer, ItemStack other) {
        if(pLevel.isClientSide) {
            ISpellHolder holder = (ISpellHolder) other.getItem();
            holder.getSpell(other).ifPresent(spell ->
                SpellUser.get(pPlayer).ifPresent(user ->
                    SpellWheelScreen.openScreenWith(user.getManuallyAssignedSlots(), entry -> GCNetwork.sendToServer(new SetSlotSpellMessage(entry.index, spell)), s -> {}, false, true)
                )
            );
        }
    }

    public SpellStats buildStats(ItemStack stack) {
        SpellStats.Builder stats = new SpellStats.Builder();
        Optional<WandCore> core = getCore(stack);
        core.ifPresent(core1 -> core1.applyStats(stats));
        getMaterial(stack).ifPresent(mat -> mat.applyStats(stats));
        Optional<WandCap> cap = getCap(stack);
        cap.ifPresent(cap1 -> cap1.applyStats(stats));
        if(cap.isEmpty() && core.isPresent())
            stats.addEfficiency(-core.get().efficiency() / 2);
        return stats.build();
    }
}
