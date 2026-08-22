package hama.industries.buni.ai;

import hama.industries.buni.BuniActivity;
import hama.industries.buni.BuniConfig;
import hama.industries.buni.BuniSounds;
import hama.industries.buni.entity.Buni;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.Set;

public class BuniTargetingSensor extends Sensor<Buni> {
    @Override
    protected void doTick(ServerLevel level, Buni buni) {
        Brain<Buni> brain = buni.getBrain();
        boolean evil = buni.isEvil();

        if (!evil) {
            boolean hadRepellent = brain.getMemory(MemoryModuleType.NEAREST_REPELLENT).isPresent();

            brain.setMemory(MemoryModuleType.NEAREST_REPELLENT, findNearestRepellent(level, buni));

            if (!hadRepellent && buni.isRepelled()) {
                buni.playSound(BuniSounds.REPELLED.get());
            }
        }

        if (evil || (!buni.isRepelled() && buni.getRandom().nextDouble() < BuniConfig.CONFIG.PICK_ON_CHANCE.get() &&
                brain.getActiveNonCoreActivity().filter(act -> act == BuniActivity.DANCE).isEmpty()) && brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()
        ) {
            Optional<LivingEntity> target = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                    .flatMap(nearest -> nearest.findClosest(buni::canTargetEntity));
            brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
        }
    }

    private static Optional<BlockPos> findNearestRepellent(ServerLevel pLevel, LivingEntity pLivingEntity) {
        return BlockPos.findClosestMatch(pLivingEntity.blockPosition(), 16, 8, (pos) -> isValidRepellent(pLevel, pos));
    }

    private static boolean isValidRepellent(ServerLevel pLevel, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos);
        boolean flag = blockstate.is(BlockTags.PIGLIN_REPELLENTS);
        return flag && blockstate.is(Blocks.SOUL_CAMPFIRE) ? CampfireBlock.isLitCampfire(blockstate) : flag;
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.ATTACK_TARGET);
    }
}
