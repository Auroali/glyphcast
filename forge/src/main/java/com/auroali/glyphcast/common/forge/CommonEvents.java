package com.auroali.glyphcast.common.forge;

import com.auroali.glyphcast.Glyphcast;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.capabilities.forge.SpellUserImpl;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommonEvents {
    @SubscribeEvent
    public static void attachPlayerCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(new ResourceLocation(Glyphcast.MODID, "spell_user"), new SpellUserProvider(player));
        }
    }

    public static class SpellUserProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        LazyOptional<ISpellUser> opt;
        ISpellUser user;

        public SpellUserProvider(Player player) {
            user = new SpellUser(player);
            opt = LazyOptional.of(() -> user);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return SpellUserImpl.SPELL_USER.orEmpty(capability, opt);
        }

        @Override
        public CompoundTag serializeNBT() {
            return user.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag arg) {
            user.deserializeNBT(arg);
        }
    }
}
