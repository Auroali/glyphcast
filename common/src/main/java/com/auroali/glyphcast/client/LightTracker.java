package com.auroali.glyphcast.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks the dynamic lighting for an entity
 * Currently the brightness is fixed
 *
 * @author Auroali
 */
public class LightTracker {
    public static final ConcurrentHashMap<Entity, DynamicLightSource> LIGHTS = new ConcurrentHashMap<>();
    private static final List<Entity> REMOVALS = new ArrayList<>();

    public static void tick(ClientLevel level) {
        List<BlockPos> updatePos = new ArrayList<>();
        LIGHTS.entrySet().removeIf(entry -> {
            if (shouldRemove(entry.getKey())) {
                updatePos.add(entry.getValue().position());
                return true;
            }
            return false;
        });

        updatePos.forEach(pos -> level.getLightEngine().checkBlock(pos));
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
        // TODO: Reimplement config
        if (Minecraft.getInstance().level == null || (!LIGHTS.containsKey(entity) && Minecraft.getInstance().level.getGameTime() % 1 != 0))
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
