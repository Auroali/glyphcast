package com.auroali.glyphcast.common.capabilities.forge;

import com.auroali.glyphcast.common.capabilities.ISpellUser;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.Optional;

public class SpellUserImpl {
    public static Capability<ISpellUser> SPELL_USER = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static Optional<ISpellUser> get(Player player) {
        return player == null ? Optional.empty() : player.getCapability(SPELL_USER).resolve();
    }
}
