package com.auroali.glyphcast.common.network.server;

import com.auroali.glyphcast.common.items.IGlyphWriteable;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class WriteParchmentMessage extends NetworkMessage {
    final int slot;
    final GlyphSequence sequence;

    public WriteParchmentMessage(int slot, GlyphSequence sequence) {
        this.slot = slot;
        this.sequence = sequence;
    }
    public WriteParchmentMessage(FriendlyByteBuf buf) {
        slot = buf.readInt();
        sequence = GlyphSequence.fromNetwork(buf);
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        sequence.encode(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if(player == null)
                return;

            // Write the glyph sequence to the target item
            ItemStack stack = player.getInventory().getItem(slot);
            if(stack.getItem() instanceof IGlyphWriteable glyphWriteable)
                player.getInventory().setItem(slot, glyphWriteable.writeGlyphs(stack, sequence));
        });
        ctx.get().setPacketHandled(true);
    }
}
