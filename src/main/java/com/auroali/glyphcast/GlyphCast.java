package com.auroali.glyphcast;

import com.auroali.glyphcast.client.ClientEvents;
import com.auroali.glyphcast.common.config.GCClientConfig;
import com.auroali.glyphcast.common.config.GCCommonConfig;
import com.auroali.glyphcast.common.registry.*;
import com.auroali.glyphcast.common.registry.listeners.WandCapReloadListener;
import com.auroali.glyphcast.common.registry.listeners.WandCoreReloadListener;
import com.auroali.glyphcast.common.registry.listeners.WandMaterialReloadListener;
import com.auroali.glyphcast.common.spells.Spell;
import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GlyphCast.MODID)
public class GlyphCast {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "glyphcast";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Supplier<IForgeRegistry<Spell>> SPELL_REGISTRY = GCSpells.SPELLS.makeRegistry(RegistryBuilder::new);
    public static final CreativeModeTab GLYPHCAST_TAB = new CreativeModeTab(MODID + ".glyphcast") {
        @Override
        public ItemStack makeIcon() {
            ItemStack stack = new ItemStack(GCItems.WAND.get());
            GCItems.WAND.get().setCap(stack, new ResourceLocation(MODID, "iron"));
            GCItems.WAND.get().setMaterial(stack, new ResourceLocation(MODID, "wandering"));
            return stack;
        }
    };

    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public GlyphCast() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        GCItems.ITEMS.register(modEventBus);
        GCSpells.SPELLS.register(modEventBus);
        GCEntities.ENTITIES.register(modEventBus);
        GCBlocks.BLOCKS.register(modEventBus);
        GCParticles.PARTICLES.register(modEventBus);
        GCMenus.MENUS.register(modEventBus);
        GCRecipesSerializers.RECIPES.register(modEventBus);
        GCRecipeTypes.RECIPE_TYPES.register(modEventBus);
        GCFluids.FLUIDS.register(modEventBus);
        modEventBus.addListener(GCEntityDataSerializers::register);

        GCNetwork.registerPackets();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GCClientConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GCCommonConfig.COMMON_SPEC);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::attributesEvent);
        modEventBus.addListener(this::sendImc);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }


    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(GCOres::init);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientEvents.clientSetup(event);
    }

    private void attributesEvent(final EntityAttributeCreationEvent event) {
        event.put(GCEntities.STAFF_ENTITY.get(), LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, Float.MAX_VALUE)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.FLYING_SPEED, 0.32)
                .build());
    }

    private void sendImc(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
    }

    @SubscribeEvent
    public void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new WandCoreReloadListener(new Gson(), "wands/core"));
        event.addListener(new WandMaterialReloadListener(new Gson(), "wands/material"));
        event.addListener(new WandCapReloadListener(new Gson(), "wands/cap"));
    }
}
