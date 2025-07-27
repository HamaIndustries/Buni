package hama.industries.buni;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.Event;

public class BuniEvents {

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(BuniEvents::protecc);
        MinecraftForge.EVENT_BUS.addListener(BuniEvents::overrideMobGriefing);
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

    public static void overrideMobGriefing(EntityMobGriefingEvent event) {
        if (event.getEntity().getType().equals(BuniRegistry.BUNI.get())) {
            event.setResult(Event.Result.ALLOW);
        }
    }

}
