package com.auroali.glyphcast.common.spells.glyph;

import net.minecraft.network.chat.Component;

public enum Glyph {
    FIRE(Component.translatable("glyph.glyphcast.fire"), -2536930),
    LIGHT(Component.translatable("glyph.glyphcast.light"), -15327),
    ICE(Component.translatable("glyph.glyphcast.ice"), -13537324),
    EARTH(Component.translatable("glyph.glyphcast.earth"), -15357153);

    final Component component;
    final int color;

    public Component component() {
        return component;
    }

    public int color() {
        return color;
    }

    Glyph(Component component, int color) {
        this.component = component;
        this.color = color;
    }
}
