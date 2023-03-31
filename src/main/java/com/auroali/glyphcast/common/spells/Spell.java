package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy;
import com.auroali.glyphcast.common.network.client.SpellEventMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
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
 * @see com.auroali.glyphcast.common.spells.glyph.GlyphSequence
 * @see com.auroali.glyphcast.common.spells.FireSpell
 * @author Auroali
 */
public abstract class Spell {
    protected final GlyphSequence sequence;
    protected String descriptionId;

    public Spell(GlyphSequence sequence) {
        this.sequence = sequence;
    }
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("spell", GlyphCast.SPELL_REGISTRY.get().getKey(this));
        }

        return this.descriptionId;
    }

    /**
     * Gets the spell's unlocalized name
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

    public void handleEvent(Byte id, PositionedContext ctx) {}
    public void triggerEvent(Byte id, PositionedContext ctx) {
        GCNetwork.sendToNear(ctx.level(), ctx.player().position(), 64, new SpellEventMessage(id, this, ctx));
    }


    /**
     * Wrapper around activate that automatically handles verifying stats and energy costs
     * @param level the level this spell is activated in
     * @param player the player who activated this spell
     * @param stats the stats used to activate this spell
     */
    public void tryActivate(Level level, Player player, SpellStats stats) {
        if(stats.efficiency() <= 0 || stats.averageAffinity() <= 0 || !canDrainEnergy(stats, player, getCost()))
            return;

        drainEnergy(stats, player, getCost());
        activate(new BasicContext(level, player, stats));
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
        IChunkEnergy.drainAt(player.level, player.chunkPosition(), amount / stats.efficiency(), false);
    }

    protected boolean canDrainEnergy(SpellStats stats, Player player, double amount) {
        return IChunkEnergy.drainAt(player.level, player.chunkPosition(), amount / stats.efficiency(), true) == amount / stats.efficiency();
    }

    public interface IContext {
        Level level();
        Player player();
        SpellStats stats();
        default void toNetwork(FriendlyByteBuf buf) {
            buf.writeInt(ctxType());
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

    public record BasicContext(Level level, Player player, SpellStats stats) implements IContext {
        @Override
        public int ctxType() {
            return 0;
        }

        @Override
        public void writeAdditional(FriendlyByteBuf buf) {

        }
    }

    public record PositionedContext(Level level, Player player, SpellStats stats, Vec3 start, Vec3 end) implements IContext {

        public static PositionedContext with(IContext ctx, Vec3 pos) {
            return new PositionedContext(ctx.level(), ctx.player(), ctx.stats(), pos, Vec3.ZERO);
        }
        public static PositionedContext with(IContext ctx, BlockPos pos) {
            return new PositionedContext(ctx.level(), ctx.player(), ctx.stats(), new Vec3(pos.getX(), pos.getY(), pos.getZ()), Vec3.ZERO);
        }
        public static PositionedContext withRange(IContext ctx, Vec3 start, Vec3 end) {
            return new PositionedContext(ctx.level(), ctx.player(), ctx.stats(), start, end);
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
