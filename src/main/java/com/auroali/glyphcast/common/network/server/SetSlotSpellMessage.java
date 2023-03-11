package com.auroali.glyphcast.common.network.server;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.spells.Spell;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class SetSlotSpellMessage extends NetworkMessage {

    final Spell spell;
    final int slot;

    public SetSlotSpellMessage(int slot, Spell spell) {
        this.spell = spell;
        this.slot = slot;
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeRegistryId(GlyphCast.SPELL_REGISTRY.get(), spell);
        buf.writeInt(slot);
    }

    public SetSlotSpellMessage(FriendlyByteBuf buf) {
        spell = buf.readRegistryId();
        slot = buf.readInt();

    }
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getSender() == null)
                return;

            SpellUser.get(ctx.get().getSender()).ifPresent(user -> {
                // The client has sent an undiscovered spell, so we don't set it
                if(!ctx.get().getSender().isCreative() && !user.hasDiscoveredSpell(spell))
                    return;
                user.setSpellForSlot(slot, spell);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
