package hama.industries.buni;

import net.minecraft.world.level.GameRules;

public class BuniGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> RULE_NATURAL_BUNI_SPAWNS =
            GameRules.register("naturalBuniSpawns", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));

    public static void init() {}
}
