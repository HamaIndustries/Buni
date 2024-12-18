package hama.industries.buni;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
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

import java.util.Set;

@Mod.EventBusSubscriber
public class BuniAi {
    public static final SensorType<TemptingSensor> BUNI_TEMPTATIONS = new SensorType<>(() -> new TemptingSensor(Ingredient.of(BuniTags.Items.BUNI_TEMPTATIONS)));

    public static final ImmutableList<? extends SensorType<? extends Sensor<? super Buni>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            BUNI_TEMPTATIONS
    );

    public static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
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
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.DANCING
    );

    @SubscribeEvent
    public static void updateJukeboxPlayingState(VanillaGameEvent event) {
        int r = event.getVanillaEvent().getNotificationRadius();

        BlockPos bpos = BlockPos.containing(event.getEventPosition());
        if (event.getVanillaEvent() == GameEvent.JUKEBOX_PLAY) {
            for (Buni bun : event.getLevel().getEntitiesOfClass(Buni.class, AABB.ofSize(event.getEventPosition(), r, r, r))) {
                if (bun.getJukeboxPos() == null || bun.getJukeboxPos().distToCenterSqr(bun.position()) > bpos.distToCenterSqr(bun.position())) {
                    bun.setJukeboxPos(bpos);
                    bun.getBrain().setMemoryWithExpiry(MemoryModuleType.DANCING, true, 1000);
                }
            }
        } else if (event.getVanillaEvent() == GameEvent.JUKEBOX_STOP_PLAY) {
            for (Buni bun : event.getLevel().getEntitiesOfClass(Buni.class, AABB.ofSize(event.getEventPosition(), r, r, r))) {
                bun.setJukeboxPos(null);
                bun.getBrain().eraseMemory(MemoryModuleType.DANCING);
            }
        }
    }

    public static Brain<?> makeBrain(Brain<Buni> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(brain);
        initDanceActivity(brain);
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
                new Swim(0.4f),
                new MoveToTargetSink(10000, 15000),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
            )
        );
    }

    public static void initIdleActivity(Brain<Buni> brain) {
        brain.addActivity(BuniActivity.IDLE, ImmutableList.of(
                Pair.of(5, new FollowTemptation(e -> 1f)),
                Pair.of(6, SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6.0F, UniformInt.of(30, 60)))
        ));
    }

    public static void initFightActivity(Brain<Buni> brain) {

    }

    public static void initDanceActivity(Brain<Buni> brain) {
        brain.addActivityWithConditions(BuniActivity.DANCE, ImmutableList.of(Pair.of(0, new DoNothing(20, 30))), Set.of(Pair.of(MemoryModuleType.DANCING, MemoryStatus.VALUE_PRESENT)));
    }

    public static void updateActivity(Buni buni) {
        buni.getBrain().setActiveActivityToFirstValid(
                ImmutableList.of(BuniActivity.TUMBLE, BuniActivity.ATTACK, BuniActivity.DANCE, Activity.IDLE)
        );
    }

    public static void registerSensorsAndMemories(RegisterEvent event) {
        event.register(ForgeRegistries.SENSOR_TYPES.getRegistryKey(), BuniMod.id("buni_temptations"), () -> BUNI_TEMPTATIONS);
    }
}
