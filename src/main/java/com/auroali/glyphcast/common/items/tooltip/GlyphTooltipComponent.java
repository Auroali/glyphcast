package com.auroali.glyphcast.common.items.tooltip;

import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record GlyphTooltipComponent(GlyphSequence sequence) implements TooltipComponent {
}
