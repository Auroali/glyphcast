package com.auroali.glyphcast.common.spells.wand;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy;
import com.auroali.glyphcast.common.spells.HoldSpell;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import com.auroali.glyphcast.common.spells.glyph.Ring;

public class MagicExtractSpell extends HoldSpell {
    public MagicExtractSpell() {
        super(new GlyphSequence(Ring.of(Glyph.WAND)));
    }

    @Override
    protected void run(IContext ctx, int usedTicks) {
        GlyphCast.LOGGER.debug("Nearby Fractures: {}", IChunkEnergy.getNearbyFractures(ctx.level(), ctx.player().blockPosition(), 1).size());
    }

    @Override
    public double getCost() {
        return 0;
    }
}
