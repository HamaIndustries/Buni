package hama.industries.buni.ai;

import hama.industries.buni.Buni;
import hama.industries.buni.BuniActivity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Set;

public class EvilTargetingSensor extends Sensor<Buni> {
    @Override
    protected void doTick(ServerLevel level, Buni buni) {
        if (!buni.isEvil()) return;
        Brain<Buni> brain = buni.getBrain();
        if (
                brain.getActiveNonCoreActivity().filter(act -> act == BuniActivity.DANCE).isEmpty()
                && brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()
        ) {
            brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                    .flatMap(nearest -> nearest.findClosest(e -> e.getType().equals(EntityType.PLAYER) && buni.canAttack(e)))
                    .ifPresent(player -> brain.setMemory(MemoryModuleType.ATTACK_TARGET, player));
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.ATTACK_TARGET);
    }
}
