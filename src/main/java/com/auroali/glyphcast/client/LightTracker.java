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
import java.util.HashMap;
import java.util.List;

/**
 * Tracks the dynamic lighting for an entity
 * Currently the brightness is fixed
 * @author Auroali
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = GlyphCast.MODID)
public class LightTracker {
    public static final HashMap<Entity, BlockPos> LIGHTS = new HashMap<>();

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.START || Minecraft.getInstance().level == null)
            return;

        List<BlockPos> updatePos = new ArrayList<>();
        LIGHTS.entrySet().removeIf(entry -> {
            if (shouldRemove(entry.getKey())) {
                updatePos.add(entry.getValue());
                return true;
            }
            return false;
        });

        updatePos.forEach(pos -> Minecraft.getInstance().level.getLightEngine().checkBlock(pos));
    }


    private static boolean shouldRemove(Entity entity) {
        return entity == null || entity.level != Minecraft.getInstance().level || entity.isRemoved();
    }

    /**
     * Updates the dynamic lighting on an entity
     * if the entity isn't present in the LIGHTS map, this skips the update frequency check
     * @param entity the entity to update
     */
    public static void update(Entity entity) {
        if(Minecraft.getInstance().level == null || (!LIGHTS.containsKey(entity) && Minecraft.getInstance().level.getGameTime() % GCClientConfig.CLIENT.updateFrequency.get() != 0))
            return;
        BlockPos pos = LIGHTS.get(entity);
        LIGHTS.put(entity, entity.blockPosition());
        Minecraft.getInstance().level.getLightEngine().checkBlock(entity.blockPosition());
        if(pos != null)
            Minecraft.getInstance().level.getLightEngine().checkBlock(pos);
    }

}
