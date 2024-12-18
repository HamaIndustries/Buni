package hama.industries.buni;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Set;

public class BuniAi {
    public static final SensorType<TemptingSensor> BUNI_TEMPTATIONS = new SensorType<>(() -> new TemptingSensor(Ingredient.of(BuniTags.Items.BUNI_TEMPTATIONS)));

    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Buni>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            BUNI_TEMPTATIONS
    );
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.IS_PANICKING,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            MemoryModuleType.IS_TEMPTED,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES
    );

    public static Brain<?> makeBrain(Brain<Buni> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(brain);
        initDanceActivity(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(BuniActivity.NONE);
        brain.useDefaultActivity();
        return brain;
    }

    public static Brain.Provider<Buni> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    public static void initCoreActivity(Brain<Buni> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
//                new Swim(0.8f),
                new MoveToTargetSink(10000, 15000),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
            )
        );
    }

    public static void initIdleActivity(Brain<Buni> brain) {
        brain.addActivity(BuniActivity.NONE, ImmutableList.of(
//                Pair.of(0, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                Pair.of(1, new FollowTemptation(e -> 1f))
        ));
    }

    public static void initFightActivity(Brain<Buni> brain) {

    }

    public static void initDanceActivity(Brain<Buni> brain) {

    }

    public static void updateActivity(Buni buni) {
        buni.getBrain().setActiveActivityToFirstValid(
                ImmutableList.of(BuniActivity.TUMBLE, BuniActivity.ATTACK, BuniActivity.DANCE, BuniActivity.NONE)
        );
    }

    public static void registerSensorsAndMemories(RegisterEvent event) {
        event.register(ForgeRegistries.SENSOR_TYPES.getRegistryKey(), BuniMod.id("buni_temptations"), () -> BUNI_TEMPTATIONS);
    }
}
