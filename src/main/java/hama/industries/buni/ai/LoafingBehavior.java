package hama.industries.buni.ai;

import com.google.common.collect.ImmutableMap;
import hama.industries.buni.Buni;
import hama.industries.buni.BuniAi;
import hama.industries.buni.BuniRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class LoafingBehavior extends Behavior<Buni> {
    public LoafingBehavior(int minDurationTicks, int maxDurationTicks) {
        super(
                ImmutableMap.of(
                        MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.REGISTERED,
                        MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.REGISTERED
                ), minDurationTicks, maxDurationTicks
        );
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Buni buni, long p_22547_) {
        return !buni.getBrain().checkMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryStatus.VALUE_PRESENT);
    }

    @Override
    protected void stop(ServerLevel level, Buni buni, long p_22550_) {
        // wake up neighbors
        buni.getBrain().eraseMemory(BuniAi.WANTS_TO_LOAF);
        buni.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).ifPresent(
                entities -> entities.stream().filter(e -> e.getType() == BuniRegistry.BUNI.get())
                        .forEach(e -> e.getBrain().eraseMemory(BuniAi.WANTS_TO_LOAF))
        );
    }
}
