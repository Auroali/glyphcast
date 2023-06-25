package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.items.GlyphParchmentItem;
import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.client.SpellEventMessage;
import com.auroali.glyphcast.common.registry.GCCastingTraits;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.wands.CastingTraitHelper;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * A spell that performs an action,
 * represented by a <code>GlyphSequence</code>
 *
 * @author Auroali
 * @see GlyphSequence
 * @see com.auroali.glyphcast.common.spells.single.FireSpell
 */
public abstract class Spell {
    protected final GlyphSequence sequence;
    protected String descriptionId;

    public Spell(GlyphSequence sequence) {
        this.sequence = sequence;
    }

    public boolean canCastSpell(ItemStack castingItem) {
        return true;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("spell", Glyphcast.SPELLS.getKey(this).get().location());
        }

        return this.descriptionId;
    }

    /**
     * Gets the spell's unlocalized name
     *
     * @return the spell's unlocalized name
     * @see Item#getDescriptionId()
     */
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getName() {
        return Component.translatable(getDescriptionId());
    }

    public Component getSpellDescription() {
        return Component.translatable(getDescriptionId() + ".desc");
    }

    public GlyphSequence getSequence() {
        return sequence;
    }

    public abstract double getCost();

    public boolean isSequence(GlyphSequence sequence) {
        return this.sequence.equals(sequence);
    }

    public abstract void activate(IContext ctx);

    public void handleEvent(Byte id, PositionedContext ctx) {
    }

    public void triggerEvent(Byte id, PositionedContext ctx) {
        GCNetwork.CHANNEL.sendToNear(ctx.level(), ctx.player().position(), 64, new SpellEventMessage(id, this, ctx));
    }

    public long getCooldown() {
        return 12;
    }

    /**
     * Wrapper around activate that automatically handles verifying stats and energy costs
     *
     * @param level  the level this spell is activated in
     * @param player the player who activated this spell
     */
    public void tryActivate(Level level, Player player, InteractionHand hand) {
        IContext ctx = new BasicContext(level, player, hand);
        if (!canDrainEnergy(ctx, getCost()))
            return;

        drainEnergy(ctx, getCost());
        activate(ctx);
    }

    protected void drainEnergy(IContext ctx, double amount) {
        double finalAmount = CastingTraitHelper.calculateFinalCost(ctx.getCastingItem(), amount);
        SpellUser.get(ctx.player()).ifPresent(user ->
                user.drainEnergy(finalAmount, false)
        );
    }

    protected boolean canDrainEnergy(IContext ctx, double amount) {
        double finalAmount = CastingTraitHelper.calculateFinalCost(ctx.getCastingItem(), amount);
        return SpellUser.get(ctx.player())
                .map(user -> user.drainEnergy(finalAmount, true))
                .orElse(Double.NaN)
                == finalAmount;
    }

    public interface IContext {
        Level level();

        InteractionHand hand();

        default boolean isWand() {
            return player() != null && player().getItemInHand(hand()).getItem() instanceof IWandLike;
        }

        default boolean isParchment() {
            return player() != null && player().getItemInHand(hand()).getItem() instanceof GlyphParchmentItem;
        }

        default ItemStack getCastingItem() {
            return player() != null ? player().getItemInHand(hand()) : ItemStack.EMPTY;
        }

        default ItemStack getOtherHandItem() {
            return player() != null ? player().getItemInHand(InteractionHand.values()[(hand().ordinal() + 1) % 2]) : ItemStack.EMPTY;
        }

        /**
         * Plays a sound at the player's position
         *
         * @param sound  the sound to play
         * @param volume the volume to play the sound at
         */
        default void playSound(SoundEvent sound, float volume) {
            level().playSound(null, player(), sound, SoundSource.PLAYERS, volume, 1.0f);
        }

        @Nullable
        default EntityHitResult clipEntity(Entity entity, Vec3 startVec, Vec3 direction, Predicate<Entity> filter, double dist) {
            Vec3 endVec = startVec.add(direction.scale(dist));
            AABB bounds = new AABB(startVec, endVec).inflate(1);
            return ProjectileUtil.getEntityHitResult(entity, startVec, endVec, bounds, filter, Double.MAX_VALUE);
        }

        @Nullable
        default EntityHitResult clipEntity(double dist, Predicate<Entity> filter) {
            return clipEntity(player(), player().getEyePosition(), player().getLookAngle(), filter, dist);
        }

        @Nullable
        default EntityHitResult clipEntityWithCollision(double dist, Predicate<Entity> filter) {
            BlockHitResult blockResult = clipBlock(ClipContext.Block.COLLIDER, dist);
            EntityHitResult entityHitResult = clipEntity(dist, filter);
            if(blockResult.getType() == HitResult.Type.MISS)
                return entityHitResult;
            if(entityHitResult == null || blockResult.distanceTo(player()) < entityHitResult.distanceTo(player()))
                return null;
            return entityHitResult;
        }

        Player player();

        default void toNetwork(FriendlyByteBuf buf) {
            buf.writeInt(ctxType());
            buf.writeInt(hand().ordinal());
            buf.writeInt(player().getId());
            writeAdditional(buf);
        }

        default BlockHitResult clipBlock(ClipContext.Block block, ClipContext.Fluid fluid, double range) {
            return level().clip(new ClipContext(
                    player().getEyePosition(),
                    player().getEyePosition().add(player().getLookAngle().scale(range)),
                    block,
                    fluid,
                    player()
            ));
        }

        default BlockHitResult clipBlock(ClipContext.Block block, double range) {
            return clipBlock(block, ClipContext.Fluid.NONE, range);
        }

        default BlockHitResult clipBlock(ClipContext.Fluid fluid, double range) {
            return clipBlock(ClipContext.Block.COLLIDER, fluid, range);
        }

        default BlockHitResult clipBlock(double range) {
            return clipBlock(ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, range);
        }

        int ctxType();

        void writeAdditional(FriendlyByteBuf buf);
    }

    public record BasicContext(Level level, Player player, InteractionHand hand) implements IContext {
        @Override
        public int ctxType() {
            return 0;
        }

        @Override
        public void writeAdditional(FriendlyByteBuf buf) {

        }
    }

    public record PositionedContext(Level level, Player player, InteractionHand hand, Vec3 start,
                                    Vec3 end) implements IContext {

        public static PositionedContext with(IContext ctx, Vec3 pos) {
            return new PositionedContext(ctx.level(), ctx.player(), ctx.hand(), pos, Vec3.ZERO);
        }

        public static PositionedContext with(IContext ctx, BlockPos pos) {
            return new PositionedContext(ctx.level(), ctx.player(), ctx.hand(), new Vec3(pos.getX(), pos.getY(), pos.getZ()), Vec3.ZERO);
        }

        public static PositionedContext withRange(IContext ctx, Vec3 start, Vec3 end) {
            return new PositionedContext(ctx.level(), ctx.player(), ctx.hand(), start, end);
        }

        @Override
        public int ctxType() {
            return 1;
        }

        @Override
        public void writeAdditional(FriendlyByteBuf buf) {
            buf.writeDouble(start.x);
            buf.writeDouble(start.y);
            buf.writeDouble(start.z);
            buf.writeDouble(end.x);
            buf.writeDouble(end.y);
            buf.writeDouble(end.z);
        }
    }
}
