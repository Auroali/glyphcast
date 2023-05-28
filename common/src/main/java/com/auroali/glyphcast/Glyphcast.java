package com.auroali.glyphcast;

import com.auroali.glyphcast.client.GCKeybinds;
import com.auroali.glyphcast.client.LightTracker;
import com.auroali.glyphcast.client.model.FloatingLightModel;
import com.auroali.glyphcast.client.particles.FractureProvider;
import com.auroali.glyphcast.client.particles.MagicAmbienceProvider;
import com.auroali.glyphcast.client.particles.MagicDripProvider;
import com.auroali.glyphcast.client.particles.MagicPulseProvider;
import com.auroali.glyphcast.client.render.entity.EmptyEntityRenderer;
import com.auroali.glyphcast.client.render.entity.LightEntityRenderer;
import com.auroali.glyphcast.client.render.overlays.EnergyGaugeOverlay;
import com.auroali.glyphcast.client.screen.CarvingTableScreen;
import com.auroali.glyphcast.client.screen.ScribingTableScreen;
import com.auroali.glyphcast.common.capabilities.ISpellUser;
import com.auroali.glyphcast.common.capabilities.SpellUser;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.client.SyncCooldownManagerMessage;
import com.auroali.glyphcast.common.network.client.SyncWandCoresMessage;
import com.auroali.glyphcast.common.registry.*;
import com.auroali.glyphcast.common.registry.listeners.WandCoreReloadListener;
import com.auroali.glyphcast.common.spells.Spell;
import com.auroali.glyphcast.common.wands.CastingTrait;
import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class Glyphcast {
    public static final String MODID = "glyphcast";
    // We can use this if we don't want to use DeferredRegister
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MODID));
    public static final Registrar<Spell> SPELLS = REGISTRIES.get().<Spell>builder(new ResourceLocation(MODID, "spells")).build();
    public static final Registrar<CastingTrait> CASTING_TRAITS = REGISTRIES.get().<CastingTrait>builder(new ResourceLocation(MODID, "casting_traits")).build();
    // Registering a new creative tab
    public static final CreativeModeTab GLYPHCAST_TAB = CreativeTabRegistry.create(new ResourceLocation(MODID, "glyphcast_tab"), () ->
            new ItemStack(GCItems.PARCHMENT.get()));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MODID, Registry.ITEM_REGISTRY);
    public static final ResourceKey<Registry<Spell>> SPELL_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(Glyphcast.MODID, "spells"));
    public static final ResourceKey<Registry<CastingTrait>> CASTING_TRAIT_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(Glyphcast.MODID, "casting_traits"));

    public static void init() {

        GCNetwork.registerPackets();

        GCFluids.FLUIDS.register();
        GCBlocks.BLOCKS.register();
        GCItems.ITEMS.register();
        GCParticles.PARTICLES.register();
        GCRecipesSerializers.RECIPES.register();
        GCRecipeTypes.RECIPE_TYPES.register();
        GCSpells.SPELLS.register();
        GCMenus.MENUS.register();
        GCEntities.ENTITIES.register();
        GCWorldgen.CONFIGURED_FEATURES.register();
        GCWorldgen.PLACED_FEATURES.register();


        System.out.println(GlyphcastExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());

        PlayerEvent.PLAYER_JOIN.register(player -> {
            GCNetwork.CHANNEL.sendToPlayer(player, new SyncWandCoresMessage());
            SpellUser.get(player).ifPresent(ISpellUser::loadFloatingLights);
            SpellUser.get(player).ifPresent(ISpellUser::sync);
        });

        PlayerEvent.PLAYER_QUIT.register(player -> SpellUser.get(player).ifPresent(ISpellUser::saveFloatingLights));
        PlayerEvent.PLAYER_RESPAWN.register((player, f) -> SpellUser.get(player).ifPresent(ISpellUser::sync));
        PlayerEvent.CHANGE_DIMENSION.register((player, oldLevel, newLevel) -> SpellUser.get(player).ifPresent(ISpellUser::sync));

        TickEvent.PLAYER_PRE.register(player -> {
            Level level = player.level;
            if (level.isClientSide)
                return;

            SpellUser.get(player).ifPresent(user -> {
                if (user.getEnergy() > user.getMaxEnergy())
                    user.setEnergy(user.getEnergy());

                user.getTickingSpells().removeIf(data -> {
                    boolean flag = !data.getSpell().tick(new Spell.BasicContext(player.level, player, data.getHand()), data.getTicks(), data.getTag());
                    data.setTicks(data.getTicks() + 1);
                    return flag;
                });

                user.getCooldownManager().tickCooldowns();
                if(user.getCooldownManager().markedDirty()) {
                    user.getCooldownManager().markClean();
                    if(player instanceof ServerPlayer serverPlayer)
                        GCNetwork.CHANNEL.sendToPlayer(serverPlayer, new SyncCooldownManagerMessage(user.getCooldownManager()));
                }
            });
        });



        ReloadListenerRegistry.register(PackType.SERVER_DATA, new WandCoreReloadListener(new Gson(), "wand_cores"));
    }

    public static void initClient() {
        GCKeybinds.register();
        ClientTickEvent.CLIENT_LEVEL_PRE.register(LightTracker::tick);

        EntityModelLayerRegistry.register(FloatingLightModel.LAYER_LOCATION, FloatingLightModel::createBodyLayer);

        EntityRendererRegistry.register(GCEntities.FLOATING_LIGHT, LightEntityRenderer::new);
        EntityRendererRegistry.register(GCEntities.FLARE, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(GCEntities.FRACTURE, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(GCEntities.FIRE, EmptyEntityRenderer::new);

        EntityDataSerializers.registerSerializer(GCEntityDataSerializers.DOUBLE);

        ParticleProviderRegistry.register(GCParticles.FRACTURE, FractureProvider::new);
        ParticleProviderRegistry.register(GCParticles.MAGIC_DRIP, MagicDripProvider::new);
        ParticleProviderRegistry.register(GCParticles.MAGIC_PULSE, MagicPulseProvider::new);
        ParticleProviderRegistry.register(GCParticles.MAGIC_AMBIENCE, MagicAmbienceProvider::new);

        ClientGuiEvent.RENDER_HUD.register((EnergyGaugeOverlay::renderOverlay));
    }

    public static void postClient() {
        RenderTypeRegistry.register(RenderType.cutout(), GCBlocks.FRACTURE_SIPHON.get());

        MenuRegistry.registerScreenFactory(GCMenus.CARVING_TABLE.get(), CarvingTableScreen::new);
        MenuRegistry.registerScreenFactory(GCMenus.SCRIBING_TABLE.get(), ScribingTableScreen::new);

        ItemPropertiesRegistry.register(GCItems.VIAL.get(), new ResourceLocation(MODID, "vial_level"), ((itemStack, clientLevel, livingEntity, i) ->
                (float) (itemStack.getOrCreateTag().getDouble("Amount") / 250.0)
        ));
    }
}
