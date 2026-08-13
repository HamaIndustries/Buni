package hama.industries.buni;

import hama.industries.buni.entity.Buni;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class BuniSpawner {
    /*
    Forge spawn system is garbage with zero documentation, so we're going to cheat
     */

    public static final boolean DEV = !FMLEnvironment.production;

    public static void tickSpawnBunis(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide ||
                !event.getEntity().getServer().getGameRules().getRule(BuniGameRules.RULE_NATURAL_BUNI_SPAWNS).get())
            return;
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = player.serverLevel();

        double originDistance = level.getSharedSpawnPos().getCenter().distanceTo(player.position());
        // reduce chance linearly with distance
        double spawnChance = BuniConfig.CONFIG.SPAWN_CHANCE.get() / Math.max(1d, (originDistance - 1000) / 100);
        if (player.getRandom().nextDouble() > spawnChance) return;

        int spawnCount = level.getEntitiesOfClass(Buni.class, player.getBoundingBox().inflate(BuniConfig.CONFIG.MAX_SPAWN_RADIUS.get())).size();
        if (spawnCount > BuniConfig.CONFIG.NATURAL_SPAWN_CAP.get()) return;

        float th = (float) (level.random.nextFloat() * 2 * Math.PI);
        int spawnWidth = BuniConfig.CONFIG.MAX_SPAWN_RADIUS.get() - BuniConfig.CONFIG.MIN_SPAWN_RADIUS.get();
        int x = (int) (Mth.cos(th) * spawnWidth);
        int z = (int) (Mth.sin(th) * spawnWidth);
        x = player.getBlockX() + BuniConfig.CONFIG.MIN_SPAWN_RADIUS.get() + x;
        z = player.getBlockZ() + BuniConfig.CONFIG.MIN_SPAWN_RADIUS.get() + z;

        BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
        if (SpawnPlacements.isSpawnPositionOk(BuniRegistry.BUNI.get(),/*SpawnPlacementTypes.ON_GROUND,*/ level, spawnPos)) {
            int bunCount;
            EntityType<? extends Buni> bunType;
            boolean evil = false;
            if (level.random.nextFloat() < 0.001) {
                bunCount = player.getRandom().nextIntBetweenInclusive(3, 4);
                bunType = BuniRegistry.BUNBARIAN.get();
                evil = true;
            } else {
                bunCount = player.getRandom().nextIntBetweenInclusive(1, 2);
                bunType = BuniRegistry.BUNI.get();
            }

            for (int i = 0; i < bunCount; i++) {
                bunType.spawn(level, spawnPos, MobSpawnType.NATURAL);
            }
            logIfDev("spawned buni at {}", spawnPos);
            if (evil) logIfDev("(it is evil)");
            logIfDev("total: {}", spawnCount);
        }


    }
    public static void logIfDev (String s, Object...args){
        if (DEV) {
            BuniMod.LOGGER.info(s, args);
        }
    }
}
