package hama.industries.buni;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BuniConfig {

    public static final BuniConfig CONFIG;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        final Pair<BuniConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(BuniConfig::new);
        SERVER_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public final ModConfigSpec.DoubleValue SPAWN_CHANCE;

    public final ModConfigSpec.IntValue MAX_SPAWN_RADIUS;
    public final ModConfigSpec.IntValue MIN_SPAWN_RADIUS;
    public final ModConfigSpec.IntValue NATURAL_SPAWN_CAP;
    public final ModConfigSpec.DoubleValue PICK_ON_CHANCE;

    public BuniConfig(ModConfigSpec.Builder builder) {
        builder.push("general");
        // once every 2 minutes
        SPAWN_CHANCE = builder.defineInRange("spawn_chance",1/1024d,0,1);
        MIN_SPAWN_RADIUS = builder.defineInRange("min_spawn_radius",20,1,128);
        MAX_SPAWN_RADIUS = builder.defineInRange("max_spawn_radius",40,1,128);
        NATURAL_SPAWN_CAP = builder.defineInRange("natural_spawn_cap",50,1,1000);
        PICK_ON_CHANCE = builder.defineInRange("pick_on",1/64d,0,1);
        builder.pop();
    }

}
