package com.auroali.glyphcast.client;

import com.auroali.glyphcast.GlyphCast;
import com.auroali.glyphcast.common.config.GCClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Tracks the dynamic lighting for an entity
 * Currently the brightness is fixed
 *
 * @author Auroali
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = GlyphCast.MODID)
public class LightTracker {
    public static final HashMap<Entity, DynamicLightSource> LIGHTS = new HashMap<>();
    private static final List<Entity> REMOVALS = new ArrayList<>();

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || Minecraft.getInstance().level == null)
            return;

        List<BlockPos> updatePos = new ArrayList<>();
        LIGHTS.entrySet().removeIf(entry -> {
            if (shouldRemove(entry.getKey())) {
                updatePos.add(entry.getValue().position());
                return true;
            }
            return false;
        });

        updatePos.forEach(pos -> Minecraft.getInstance().level.getLightEngine().checkBlock(pos));
        REMOVALS.clear();
    }

    /**
     * Returns whether any dynamic light is present at the given location
     *
     * @param pos the position to check
     * @return whether a light is present
     */
    public static boolean hasAnyLight(BlockPos pos) {
        return LIGHTS.values().stream().anyMatch(light -> light.position().equals(pos));
    }

    /**
     * Get the brightness of the dynamic light at the given position.
     * <br> If there are multiple lights, this will return the brightest.
     *
     * @param pos the position of the light
     * @return the brightness of the light
     */
    public static int getBrightnessAtPosition(BlockPos pos) {
        return LIGHTS.values().stream()
                .filter(light -> light.position().equals(pos))
                .map(DynamicLightSource::brightness)
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    private static boolean shouldRemove(Entity entity) {
        return entity == null || markedForRemoval(entity) || entity.level != Minecraft.getInstance().level || entity.isRemoved();
    }

    private static boolean markedForRemoval(Entity entity) {
        return REMOVALS.contains(entity);
    }

    /**
     * Updates the dynamic lighting on an entity
     * if the entity isn't present in the LIGHTS map, this skips the update frequency check
     *
     * @param entity the entity to update
     */
    public static void update(Entity entity, int brightness) {
        if (Minecraft.getInstance().level == null || (!LIGHTS.containsKey(entity) && Minecraft.getInstance().level.getGameTime() % GCClientConfig.CLIENT.updateFrequency.get() != 0))
            return;
        DynamicLightSource source = LIGHTS.get(entity);
        BlockPos pos = source != null ? source.position() : null;

        LIGHTS.put(entity, new DynamicLightSource(entity.blockPosition(), brightness));
        Minecraft.getInstance().level.getLightEngine().checkBlock(entity.blockPosition());
        if (pos != null)
            Minecraft.getInstance().level.getLightEngine().checkBlock(pos);
    }

    /**
     * Removes the light source for an entity
     *
     * @param entity the entity to remove
     */
    public static void removeEntity(Entity entity) {
        REMOVALS.add(entity);
    }

}
