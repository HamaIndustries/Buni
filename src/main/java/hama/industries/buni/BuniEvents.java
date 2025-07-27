package hama.industries.buni;

import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.eventbus.api.Event;

public class BuniEvents {

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(BuniEvents::overrideMobGriefing);
        MinecraftForge.EVENT_BUS.addListener(BuniEvents::updateJukeboxPlayingState);
    }

    public static void overrideMobGriefing(EntityMobGriefingEvent event) {
        if (event.getEntity().getType().equals(BuniRegistry.BUNI.get())) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    public static void updateJukeboxPlayingState(VanillaGameEvent event) {
        int r = event.getVanillaEvent().getNotificationRadius();
        if (event.getVanillaEvent() == GameEvent.JUKEBOX_PLAY) {
            for (Buni bun : event.getLevel().getEntitiesOfClass(Buni.class, AABB.ofSize(event.getEventPosition(), r, r, r))) {
                bun.getBrain().setMemoryWithExpiry(MemoryModuleType.DANCING, true, 100);
            }
        } else if (event.getVanillaEvent() == GameEvent.JUKEBOX_STOP_PLAY) {
            for (Buni bun : event.getLevel().getEntitiesOfClass(Buni.class, AABB.ofSize(event.getEventPosition(), r, r, r))) {
                bun.getBrain().eraseMemory(MemoryModuleType.DANCING);
            }
        }
    }
}
