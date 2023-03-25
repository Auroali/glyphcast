package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.Util;
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
    public abstract void activate(Level level, Player player, SpellStats stats);

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
        activate(level, player, stats);
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
}
