package com.auroali.glyphcast.common.network.server.menu;

import com.auroali.glyphcast.common.menu.ScribingMenu;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class ScribingGlyphButtonPress extends NetworkMessage {

    final Glyph glyph;
    public ScribingGlyphButtonPress(Glyph glyph) {
        this.glyph = glyph;
    }

    public ScribingGlyphButtonPress(FriendlyByteBuf buf) {
        this.glyph = Glyph.values()[buf.readByte()];
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(glyph.ordinal());
    }

    @Override
    public void handleC2S(ServerPlayer player) {
        if(player.containerMenu instanceof ScribingMenu menu) {
            menu.addGlyph(glyph);
        }
    }
}
