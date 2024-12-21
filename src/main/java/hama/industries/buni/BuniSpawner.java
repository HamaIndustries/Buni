package hama.industries.buni;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;

public class BuniSpawner {
    /*
    Forge spawn system is garbage with zero documentation, so we're going to cheat
     */

    // once every 2 minutes
    public static final double BASE_SPAWN_CHANCE_PER_TICK = 1d/800;
    public static final int MAX_SPAWN_RADIUS = 40;
    public static final int MIN_SPAWN_RADIUS = 20;
    public static final int SPAWN_WIDTH = MAX_SPAWN_RADIUS - MIN_SPAWN_RADIUS;
    private static final int NATURAL_SPAWN_CAP = 100;

    public static void tickSpawnBunis(TickEvent.ServerTickEvent event) {
        MinecraftServer server = event.getServer();

        server.getPlayerList().getPlayers().stream().filter(LivingEntity::isAlive).forEach(player -> {
            ServerLevel level = (ServerLevel) player.level();

            double originDistance = level.getSharedSpawnPos().getCenter().distanceTo(player.position());
            // reduce chance linearly with distance
            double spawnChance = BASE_SPAWN_CHANCE_PER_TICK / Math.max(1d, (originDistance - 1000) / 100);
            if (player.getRandom().nextDouble() > spawnChance) return;

            int spawnCount = level.getEntitiesOfClass(Buni.class, player.getBoundingBox().inflate(MAX_SPAWN_RADIUS)).size();
            if (spawnCount > NATURAL_SPAWN_CAP) return;

            float th = level.random.nextFloat() * 2 * 3.1415f;
            int x = (int)(Mth.cos(th) * SPAWN_WIDTH);
            int z = (int)(Mth.sin(th) * SPAWN_WIDTH);
            x = player.getBlockX() + Mth.sign(x) * MIN_SPAWN_RADIUS + x;
            z = player.getBlockZ() + Mth.sign(z) * MIN_SPAWN_RADIUS + z;

            BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
            if (level.getBlockState(spawnPos.below()).isFaceSturdy(level, spawnPos.below(), Direction.UP)) {
                int bunCount = player.getRandom().nextIntBetweenInclusive(3, 5);
                Buni.BuniGroupData groupData = Buni.makeNaturalGroupData(level);

                for (int i = 0; i < bunCount; i++) {
                    Buni bun = BuniRegistry.BUNI.get().create(level);
                    ForgeEventFactory.onFinalizeSpawn(bun, level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.NATURAL, groupData, null);
                    bun.setPos(spawnPos.above().getCenter());
                    level.addFreshEntity(bun);
                }
//                BuniMod.LOGGER.info("spawned buni at " + spawnPos);
            } else {
//                BuniMod.LOGGER.info("failed to spawn buni at " + spawnPos);
            }
//            BuniMod.LOGGER.info("total: "  + spawnCount);
        });
    }
}
