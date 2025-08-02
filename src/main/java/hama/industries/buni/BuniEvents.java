package hama.industries.buni;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.Event;

public class BuniEvents {

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(BuniEvents::overrideMobGriefing);
        MinecraftForge.EVENT_BUS.addListener(BuniEvents::updateJukeboxPlayingState);
        MinecraftForge.EVENT_BUS.addListener(BuniEvents::protecc);
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

    public static void protecc(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.level().isClientSide) return;
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && !(attacker instanceof Buni)){
                player.level().getEntitiesOfClass(Buni.class, player.getBoundingBox().inflate(20)).stream()
                        .findAny()
                        .ifPresent(b -> b.killThisGuy(attacker));
            }
        }
    }
}
