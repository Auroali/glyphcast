package com.auroali.glyphcast.common.spells.wand;

import com.auroali.glyphcast.common.damage.GCDamageSources;
import com.auroali.glyphcast.common.network.client.SpawnParticlesMessage;
import com.auroali.glyphcast.common.registry.GCNetwork;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MagicDamageSpell extends Spell {
    public MagicDamageSpell() {
        super(new GlyphSequence(Ring.of(Glyph.WAND)));
    }

    @Override
    public void activate(Level level, Player player) {
        double maxDist = player.getReachDistance() * 2;
        EntityHitResult result = clipEntityFromPlayer(player, maxDist, e -> !e.isRemoved());
        if(result == null) {
            spawnEffects(player, getDist(player, maxDist));
            return;
        }

        result.getEntity().hurt(GCDamageSources.magic(player), 10);
        double dist = Math.ceil(player.getEyePosition().distanceTo(result.getLocation()));
        spawnEffects(player, dist);
    }

    double getDist(Player player, double max) {
        BlockHitResult result = player.level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(max)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        return result.getType() != HitResult.Type.BLOCK ? max : player.getEyePosition().distanceTo(result.getLocation());
    }

    void spawnEffects(Player player, double maxDist) {
        for(int i = 2; i < (int) maxDist * 2; i++) {
            Vec3 pos = player.getEyePosition().add(player.getLookAngle().scale(maxDist * (i / (maxDist * 2))));
            // TODO: Custom particles
            SpawnParticlesMessage msg = new SpawnParticlesMessage(ParticleTypes.SOUL_FIRE_FLAME, 0.02, 8, pos, player.getLookAngle(), 0.01);
            GCNetwork.sendNearPlayer((ServerPlayer) player, 16, msg);
        }
    }
}