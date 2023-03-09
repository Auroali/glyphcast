package com.auroali.glyphcast.common.network.server;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.items.IGlyphWriteable;
import com.auroali.glyphcast.common.network.NetworkMessage;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.spells.glyph.GlyphSequence;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class SelectSpellMessage extends NetworkMessage {

    final Spell spell;

    public SelectSpellMessage(Spell spell) {
        this.spell = spell;
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeRegistryId(GlyphCast.SPELL_REGISTRY.get(), spell);
    }

    public SelectSpellMessage(FriendlyByteBuf buf) {
        spell = buf.readRegistryId();
    }
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(ctx.get().getSender() == null)
                return;

            SpellUser.get(ctx.get().getSender()).ifPresent(user -> {
                user.selectSpell(spell);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
