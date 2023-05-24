//package com.auroali.glyphcast.common.spells.wand.staff;
//
//import com.auroali.glyphcast.common.entities.StaffEntity;
//import com.auroali.glyphcast.common.items.StaffItem;
//import com.auroali.glyphcast.common.registry.GCItems;
//import com.auroali.glyphcast.common.spells.Spell;
//import com.auroali.glyphcast.common.spells.glyph.Glyph;
//import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
//import com.auroali.glyphcast.common.spells.glyph.Ring;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.ClipContext;
//import net.minecraft.world.phys.BlockHitResult;
//import net.minecraft.world.phys.HitResult;
//
//public class SeperateStaffSpell extends Spell {
//    public SeperateStaffSpell() {
//        super(new GlyphSequence(Ring.of(Glyph.WAND)));
//    }
//
//    @Override
//    public double getCost() {
//        return 0;
//    }
//
//    @Override
//    public void activate(IContext ctx) {
//        if (!ctx.getCastingItem().is(GCItems.STAFF.get()) || !ctx.getCastingItem().getOrCreateTag().getBoolean("Present"))
//            return;
//        BlockHitResult result = ctx.level().clip(new ClipContext(
//                ctx.player().getEyePosition(),
//                ctx.player().getEyePosition().add(ctx.player().getLookAngle().scale(4.0f)),
//                ClipContext.Block.COLLIDER,
//                ClipContext.Fluid.NONE,
//                null));
//        if (result.getType() == HitResult.Type.MISS)
//            return;
//        StaffItem.Variant variant = GCItems.STAFF.get().getVariant(ctx.getCastingItem());
//        StaffEntity entity = new StaffEntity(ctx.level(), variant, ctx.player());
//        entity.setPos(result.getLocation());
//        entity.setOwner(ctx.player());
//        ctx.level().addFreshEntity(entity);
//
//
//        GCItems.STAFF.get().setPresent(ctx.getCastingItem(), false);
//        GCItems.STAFF.get().setEntityUUID(ctx.getCastingItem(), entity.getUUID());
//    }
//
//    @Override
//    public boolean canCastSpell(ItemStack castingItem) {
//        return castingItem.getItem() instanceof StaffItem;
//    }
//}
