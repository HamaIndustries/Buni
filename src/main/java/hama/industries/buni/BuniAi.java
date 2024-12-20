package hama.industries.buni;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import hama.industries.buni.ai.LoafingBehavior;
import hama.industries.buni.ai.LoafingSensor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mod.EventBusSubscriber
public class BuniAi {
    public static final SensorType<TemptingSensor> BUNI_TEMPTATIONS = new SensorType<>(() -> new TemptingSensor(Ingredient.of(BuniTags.Items.BUNI_TEMPTATIONS)));
    public static final SensorType<LoafingSensor> LOAFING_SENSOR = new SensorType<>(LoafingSensor::new);
    public static final MemoryModuleType<Integer> TIME_SINCE_ACTIVITY = new MemoryModuleType<>(Optional.of(Codec.INT));
    public static final MemoryModuleType<Boolean> WANTS_TO_LOAF = new MemoryModuleType<>(Optional.of(Codec.BOOL));
    public static final MemoryModuleType<Boolean> TUMBLING = new MemoryModuleType<>(Optional.of(Codec.BOOL));

    public static final ImmutableList<? extends SensorType<? extends Sensor<? super Buni>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_ITEMS,
            BUNI_TEMPTATIONS,
            LOAFING_SENSOR
    );

    public static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.IS_PANICKING,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            MemoryModuleType.IS_TEMPTED,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.DANCING,
            MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
            TIME_SINCE_ACTIVITY,
            WANTS_TO_LOAF,
            TUMBLING
    );

    @SubscribeEvent
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

    public static Brain<?> makeBrain(Brain<Buni> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(brain);
        initDanceActivity(brain);
        initLoafActivity(brain);
        initTumbleActivity(brain);
        brain.setCoreActivities(Set.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    public static Brain.Provider<Buni> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    public static void initCoreActivity(Brain<Buni> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new LookAtTargetSink(45, 90),
                new Swim(0.1f),
                new MoveToTargetSink(10000, 15000),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
            )
        );
    }

    public static void initIdleActivity(Brain<Buni> brain) {
        brain.addActivityAndRemoveMemoriesWhenStopped(
                BuniActivity.IDLE, ImmutableList.of(
                        Pair.of(0, new FollowTemptation(e -> 1f)),
                        Pair.of(1, GoToWantedItem.create(1.5f, true, 32)),
                        Pair.of(2, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60))),
                        Pair.of(3, new RunOne<>(List.of(
                                Pair.of(new DoNothing(30, 60), 2),
                                Pair.of(StartAttacking.create(BuniAi::getRandomTarget), 1),
                                Pair.of(RandomStroll.stroll(1, false), 4),
                                Pair.of(SetWalkTargetFromLookTarget.create(1f, 3), 1)
                        )))
                ),
                Set.of(Pair.of(TIME_SINCE_ACTIVITY, MemoryStatus.REGISTERED)),
                Set.of(TIME_SINCE_ACTIVITY)
        );
    }

    public static void initTumbleActivity(Brain<Buni> brain) {
        brain.addActivityAndRemoveMemoriesWhenStopped(
                BuniActivity.TUMBLE, ImmutableList.of(
                        Pair.of(0, new DoNothing(10, 10)),
                        Pair.of(1, EraseMemoryIf.create(e -> e.onGround() && e.tumblingTicks > 3, TUMBLING))
                ),
                Set.of(Pair.of(TUMBLING, MemoryStatus.VALUE_PRESENT)),
                Set.of(TUMBLING)
        );
    }

    public static void initFightActivity(Brain<Buni> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, ImmutableList.of(
                StopAttackingIfTargetInvalid.create(),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(e -> 1f),
                MeleeAttack.create(20),
                stopAttackingAfterFirstHit()
            ), MemoryModuleType.ATTACK_TARGET);
    }

    public static void initLoafActivity(Brain<Buni> brain) {
        brain.addActivityAndRemoveMemoriesWhenStopped(
            BuniActivity.LOAF,
            ImmutableList.of(
                Pair.of(0, SetLookAndInteract.create(BuniRegistry.BUNI.get(), 20)),
                Pair.of(1, SetWalkTargetFromLookTarget.create(1, 1)),
                Pair.of(1, new LoafingBehavior(20 * 30, 20 * 60))
            ),
            Set.of(Pair.of(WANTS_TO_LOAF, MemoryStatus.VALUE_PRESENT)),
            Set.of(WANTS_TO_LOAF, TIME_SINCE_ACTIVITY)
        );
    }

    public static void initDanceActivity(Brain<Buni> brain) {
        brain.addActivityWithConditions(BuniActivity.DANCE, ImmutableList.of(
                Pair.of(0, new DoNothing(20, 30))
            ),
            Set.of(Pair.of(MemoryModuleType.DANCING, MemoryStatus.VALUE_PRESENT)));
    }

    public static void updateActivity(Buni buni) {
        buni.getBrain().setActiveActivityToFirstValid(
                ImmutableList.of(BuniActivity.TUMBLE, Activity.FIGHT, BuniActivity.DANCE, BuniActivity.LOAF, Activity.IDLE)
        );
    }

    public static Optional<LivingEntity> getRandomTarget(Buni buni) {
        return buni.getRandom().nextFloat() > 0.01 ? Optional.empty() :
                buni.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap(es -> es.findClosest(e -> buni.canAttack(e)));
    }

    private static BehaviorControl<Buni> stopAttackingAfterFirstHit() {
        return BehaviorBuilder.triggerIf(
                e -> e.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_COOLING_DOWN),
                new OneShot<>() {
                    @Override
                    public boolean trigger(ServerLevel p_259730_, Buni buni, long p_259489_) {
                        buni.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                        return false;
                    }
                }
        );
    }

    public static void registerSensorsAndMemories(RegisterEvent event) {
        event.register(ForgeRegistries.SENSOR_TYPES.getRegistryKey(), BuniMod.id("buni_temptations"), () -> BUNI_TEMPTATIONS);
        event.register(ForgeRegistries.SENSOR_TYPES.getRegistryKey(), BuniMod.id("loafing"), () -> LOAFING_SENSOR);
        event.register(ForgeRegistries.MEMORY_MODULE_TYPES.getRegistryKey(), BuniMod.id("time_since_activity"), () -> TIME_SINCE_ACTIVITY);
        event.register(ForgeRegistries.MEMORY_MODULE_TYPES.getRegistryKey(), BuniMod.id("wants_to_loaf"), () -> WANTS_TO_LOAF);
        event.register(ForgeRegistries.MEMORY_MODULE_TYPES.getRegistryKey(), BuniMod.id("tumbling"), () -> TUMBLING);
    }
}
