package hama.industries.buni;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;


public class BuniSpawnModifier {
    public static boolean spawnPredicate(EntityType<Buni> entityType, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource random) {
//        double distanceToOrigin = blockPos.distSqr(Vec3i.ZERO);
//        return distanceToOrigin < 1000;
        return true;
    }

    public static void prioritizeOrigin(SpawnPlacementRegisterEvent event) {
        BuniMod.LOGGER.info("Setting up buni spawn");
        event.register(
                BuniRegistry.BUNI.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BuniSpawnModifier::spawnPredicate,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }
}
