package hama.industries.buni.ai;

import hama.industries.buni.Buni;
import hama.industries.buni.BuniAi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;

import java.util.Optional;
import java.util.Set;

public class LoafingSensor extends Sensor<Buni> {
    private static final int SECONDS_TO_LOAF = 30;

    @Override
    protected void doTick(ServerLevel level, Buni buni) {
        Brain<Buni> brain = buni.getBrain();
        Optional<Activity> currentActivity = brain.getActiveNonCoreActivity();
        if (currentActivity.filter(activity -> activity == Activity.IDLE).isPresent()) {
            int time = brain.getMemory(BuniAi.TIME_SINCE_ACTIVITY).orElse(0) + 1;
            brain.setMemory(BuniAi.TIME_SINCE_ACTIVITY, Optional.of(time));
            if (time > SECONDS_TO_LOAF && buni.getRandom().nextFloat() < 0.1) {
                brain.setMemory(BuniAi.WANTS_TO_LOAF, true);
            }
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(
                BuniAi.TIME_SINCE_ACTIVITY,
                BuniAi.WANTS_TO_LOAF,
                MemoryModuleType.INTERACTION_TARGET,
                MemoryModuleType.NEAREST_LIVING_ENTITIES
        );
    }
}
