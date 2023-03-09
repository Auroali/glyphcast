package com.auroali.glyphcast.common.spells;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

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
    public boolean isSequence(GlyphSequence sequence) {
        return this.sequence.equals(sequence);
    }
    public abstract void activate(Level level, Player player);
}
