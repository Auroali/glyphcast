package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.items.GlyphParchmentItem;
import com.auroali.glyphcast.common.items.IWandLike;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.client.SpellEventMessage;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * A spell that performs an action,
 * represented by a <code>GlyphSequence</code>
 *
 * @author Auroali
 * @see GlyphSequence
 * @see FireSpell
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


    /**
     * Wrapper around activate that automatically handles verifying stats and energy costs
     *
     * @param level  the level this spell is activated in
     * @param player the player who activated this spell
     * @param stats  the stats used to activate this spell
     */
    public void tryActivate(Level level, Player player, InteractionHand hand, SpellStats stats) {
        if (stats.efficiency() <= 0 || stats.averageAffinity() <= 0 || !canDrainEnergy(stats, player, getCost()))
            return;

        drainEnergy(stats, player, getCost());
        activate(new BasicContext(level, player, hand, stats));
    }

    @Nullable
    protected EntityHitResult clipEntity(Level level, Entity entity, Vec3 startVec, Vec3 direction, Predicate<Entity> filter, double dist) {
        Vec3 endVec = startVec.add(direction.scale(dist));
        AABB bounds = new AABB(startVec, endVec).inflate(1);
        return ProjectileUtil.getEntityHitResult(level, entity, startVec, endVec, bounds, filter, 0.3f);
    }

    @Nullable
    protected EntityHitResult clipEntityFromPlayer(Player player, double dist, Predicate<Entity> filter) {
        return clipEntity(player.level, player, player.getEyePosition(), player.getLookAngle(), filter, dist);
    }

    protected void drainEnergy(SpellStats stats, Player player, double amount) {
        SpellUser.get(player).ifPresent(user ->
                user.drainEnergy(amount / stats.efficiency(), false)
        );
    }

    protected boolean canDrainEnergy(SpellStats stats, Player player, double amount) {
        return SpellUser.get(player)
                .map(user -> user.drainEnergy(amount / stats.efficiency(), true))
                .orElse(Double.NaN)
                == (amount / stats.efficiency());
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

        Player player();

        SpellStats stats();

        default void toNetwork(FriendlyByteBuf buf) {
            buf.writeInt(ctxType());
            buf.writeInt(hand().ordinal());
            buf.writeInt(player().getId());
            buf.writeDouble(stats().efficiency());
            buf.writeInt(stats().cooldown());
            buf.writeDouble(stats().fireAffinity());
            buf.writeDouble(stats().lightAffinity());
            buf.writeDouble(stats().iceAffinity());
            buf.writeDouble(stats().earthAffinity());
            writeAdditional(buf);
        }

        int ctxType();

        void writeAdditional(FriendlyByteBuf buf);
    }

    public record BasicContext(Level level, Player player, InteractionHand hand, SpellStats stats) implements IContext {
        @Override
        public int ctxType() {
            return 0;
        }

        @Override
        public void writeAdditional(FriendlyByteBuf buf) {

        }
    }

    public record PositionedContext(Level level, Player player, InteractionHand hand, SpellStats stats, Vec3 start,
                                    Vec3 end) implements IContext {

        public static PositionedContext with(IContext ctx, Vec3 pos) {
            return new PositionedContext(ctx.level(), ctx.player(), ctx.hand(), ctx.stats(), pos, Vec3.ZERO);
        }

        public static PositionedContext with(IContext ctx, BlockPos pos) {
            return new PositionedContext(ctx.level(), ctx.player(), ctx.hand(), ctx.stats(), new Vec3(pos.getX(), pos.getY(), pos.getZ()), Vec3.ZERO);
        }

        public static PositionedContext withRange(IContext ctx, Vec3 start, Vec3 end) {
            return new PositionedContext(ctx.level(), ctx.player(), ctx.hand(), ctx.stats(), start, end);
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
