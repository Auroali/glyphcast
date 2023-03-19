package com.auroali.glyphcast.common.registry;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.capabilities.chunk.ChunkEnergy;
import com.auroali.glyphcast.common.capabilities.chunk.IChunkEnergy;
import com.auroali.glyphcast.common.network.client.SyncSpellUserDataMessage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = GlyphCast.MODID)
public class GCCapabilities {
    public static final Capability<ISpellUser> SPELL_USER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IChunkEnergy> CHUNK_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});

    public static class SpellUserProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final ISpellUser cap;
        private final LazyOptional<ISpellUser> optional;

        public SpellUserProvider(Player player) {
            this.cap = new SpellUser(player);
            this.optional = LazyOptional.of(() -> cap);
        }
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return SPELL_USER.orEmpty(cap, this.optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            return cap.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            cap.deserializeNBT(nbt);
        }

    }

    public static class ChunkEnergyProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final IChunkEnergy cap;
        private final LazyOptional<IChunkEnergy> optional;

        public ChunkEnergyProvider(LevelChunk chunk) {
            this.cap = new ChunkEnergy(chunk);
            this.optional = LazyOptional.of(() -> cap);
        }
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CHUNK_ENERGY.orEmpty(cap, this.optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            return cap.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            cap.deserializeNBT(nbt);
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player player)
            event.addCapability(new ResourceLocation(GlyphCast.MODID, "spell_user"), new SpellUserProvider(player));
    }

    @SubscribeEvent
    public static void attachChunkCapabilities(final AttachCapabilitiesEvent<LevelChunk> event) {
        event.addCapability(new ResourceLocation(GlyphCast.MODID, "energy"), new ChunkEnergyProvider(event.getObject()));
    }

    @SubscribeEvent
    public static void clonePlayer(final PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        SpellUser.get(event.getOriginal()).ifPresent(user -> user.cloneTo(SpellUser.get(event.getEntity())));
        event.getOriginal().invalidateCaps();
    }

    /*
        Events to sync the ISpellUser data
     */

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer player)
            SpellUser.get(event.getEntity()).ifPresent(user -> GCNetwork.sendToClient(player, new SyncSpellUserDataMessage(user)));
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if(event.getEntity() instanceof ServerPlayer player)
            SpellUser.get(event.getEntity()).ifPresent(user -> GCNetwork.sendToClient(player, new SyncSpellUserDataMessage(user)));
    }

    @SubscribeEvent
    public static void onPlayerChangedDimensions(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(event.getEntity() instanceof ServerPlayer player)
            SpellUser.get(event.getEntity()).ifPresent(user -> GCNetwork.sendToClient(player, new SyncSpellUserDataMessage(user)));
    }
}
