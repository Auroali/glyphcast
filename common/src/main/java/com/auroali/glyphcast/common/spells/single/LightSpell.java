package com.auroali.glyphcast.common.spells.single;

import com.auroali.glyphcast.common.PlayerHelper;
import com.auroali.glyphcast.common.entities.FloatingLight;
import com.auroali.glyphcast.common.registry.GCEntities;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class LightSpell extends Spell {
    public LightSpell() {
        super(new GlyphSequence(Ring.of(Glyph.LIGHT)));
    }

    @Override
    public double getCost() {
        return 2;
    }

    @Override
    public void activate(IContext ctx) {
        if(ctx.player().isCrouching())
            handleCrouching(ctx);
        else
            handleDefault(ctx);
    }

    void handleCrouching(IContext ctx) {
        BlockHitResult result = ctx.clipBlock(ClipContext.Block.OUTLINE, PlayerHelper.getReachDistance(ctx.player()));
        if(result.getType() != HitResult.Type.BLOCK)
            return;

        BlockPos pos = result.getBlockPos().relative(result.getDirection());
        List<FloatingLight> lights = ctx.level().getEntities(
                GCEntities.FLOATING_LIGHT.get(),
                new AABB(pos),
                e -> !e.isRemoved() && e.getOwner() == null
        );
        lights.forEach(light -> light.remove(Entity.RemovalReason.DISCARDED));
        if(!lights.isEmpty())
            return;
        FloatingLight light = new FloatingLight(ctx.level(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        ctx.level().addFreshEntity(light);
    }

    void handleDefault(IContext ctx) {
        List<FloatingLight> lights = FloatingLight.getAllFollowing(ctx.player());
        lights.forEach(e -> e.remove(Entity.RemovalReason.DISCARDED));
        if(lights.size() > 0)
            return;

        FloatingLight light = new FloatingLight(ctx.level(), ctx.player().getX(), ctx.player().getY(), ctx.player().getZ());
        light.setOwner(ctx.player());
        ctx.level().addFreshEntity(light);
    }
}
