package hama.industries.buni;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

public class BuniAi {
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Buni>>> SENSOR_TYPES = ImmutableList.of();
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of();

    public static Brain<?> makeBrain(Brain<Buni> brain) {
        return brain;
    }

    public static Brain.Provider<Buni> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }
}
