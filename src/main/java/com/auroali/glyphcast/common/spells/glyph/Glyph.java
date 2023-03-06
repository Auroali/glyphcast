package com.auroali.glyphcast.common.spells.glyph;

import net.minecraft.network.chat.Component;

public enum Glyph {
    FIRE(Component.translatable("glyph.glyphcast.fire")),
    LIGHT(Component.translatable("glyph.glyphcast.light")),
    ICE(Component.translatable("glyph.glyphcast.ice")),
    EARTH(Component.translatable("glyph.glyphcast.earth"));

    final Component component;
    public Component component() {
        return component;
    }

    Glyph(Component component) {
        this.component = component;
    }
}
