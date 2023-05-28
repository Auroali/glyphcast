package com.auroali.glyphcast.common.network.client.menu;

import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.network.client.ClientPacketHandler;
import com.auroali.glyphcast.common.spells.glyph.Glyph;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class SendScribingGlyphListMessage extends NetworkMessage {
    public final int containerId;
    public final List<List<Glyph>> glyphs;
    public SendScribingGlyphListMessage(int containerId, List<List<Glyph>> glyphs) {
        this.containerId = containerId;
        this.glyphs = glyphs;
    }
    public SendScribingGlyphListMessage(FriendlyByteBuf buf) {
        this.containerId = buf.readInt();
        this.glyphs = new ArrayList<>();
        int numRings = buf.readInt();

        for(int i = 0; i < numRings; i++) {
            this.glyphs.add(new ArrayList<>());
            int numGlyphs = buf.readInt();
            for(int j = 0; j < numGlyphs; j++) {
                this.glyphs.get(i).add(Glyph.values()[buf.readByte()]);
            }
        }
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(containerId);
        buf.writeInt(glyphs.size());
        glyphs.forEach(list -> {
            buf.writeInt(list.size());
            list.stream()
                    .map(glyph -> (byte)glyph.ordinal())
                    .forEach(buf::writeByte);
        });
    }

    @Override
    public void handleS2C() {
        ClientPacketHandler.sendScribingGlyphList(this);
    }
}
