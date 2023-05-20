package com.auroali.glyphcast.common.capabilities.fabric;

import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class SpellUserImpl extends SpellUser implements Component {
    public SpellUserImpl(Player player) {
        super(player);
    }

    public static Optional<ISpellUser> get(Player player) {
        return player == null ? Optional.empty() : Optional.of(player.getComponent(GCComponents.SPELL_USER));
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.deserializeNBT(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.merge(this.serializeNBT());
    }
}
