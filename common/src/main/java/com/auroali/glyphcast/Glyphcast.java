package com.auroali.glyphcast;

import com.auroali.glyphcast.client.GCKeybinds;
import com.auroali.glyphcast.client.LightTracker;
import com.auroali.glyphcast.common.network.GCNetwork;
import com.auroali.glyphcast.common.network.client.SyncWandCapsMessage;
import com.auroali.glyphcast.common.network.client.SyncWandCoresMessage;
import com.auroali.glyphcast.common.network.client.SyncWandMaterialsMessage;
import com.auroali.glyphcast.common.registry.*;
import com.auroali.glyphcast.common.spells.Spell;
import com.google.common.base.Suppliers;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class Glyphcast {
    public static final String MODID = "glyphcast";
    // We can use this if we don't want to use DeferredRegister
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MODID));
    // Registering a new creative tab
    public static final CreativeModeTab GLYPHCAST_TAB = CreativeTabRegistry.create(new ResourceLocation(MODID, "glyphcast_tab"), () ->
            new ItemStack(GCItems.PARCHMENT.get()));
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MODID, Registry.ITEM_REGISTRY);
    public static final ResourceKey<Registry<Spell>> SPELL_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation(Glyphcast.MODID, "spells"));
    public static final Registrar<Spell> SPELLS = REGISTRIES.get().<Spell>builder(new ResourceLocation(MODID, "spells")).build();

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

        System.out.println(GlyphcastExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());

        PlayerEvent.PLAYER_JOIN.register(player -> {
            GCNetwork.CHANNEL.sendToPlayer(player, new SyncWandCapsMessage());
            GCNetwork.CHANNEL.sendToPlayer(player, new SyncWandCoresMessage());
            GCNetwork.CHANNEL.sendToPlayer(player, new SyncWandMaterialsMessage());
        });
    }

    public static void initClient() {
        GCKeybinds.register();
        ClientTickEvent.CLIENT_LEVEL_PRE.register(LightTracker::tick);

    }
}
