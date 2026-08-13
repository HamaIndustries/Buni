package hama.industries.buni;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BuniConfig {

    public static final BuniConfig CONFIG;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<BuniConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(BuniConfig::new);
        SERVER_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public final ForgeConfigSpec.DoubleValue SPAWN_CHANCE;

    public final ForgeConfigSpec.IntValue MAX_SPAWN_RADIUS;
    public final ForgeConfigSpec.IntValue MIN_SPAWN_RADIUS;
    public final ForgeConfigSpec.IntValue NATURAL_SPAWN_CAP;
    public final ForgeConfigSpec.DoubleValue PICK_ON_CHANCE;
    public final ForgeConfigSpec.DoubleValue BUNBARIAN_SPAWN_CHANCE;

    public BuniConfig(ForgeConfigSpec.Builder builder) {
        builder.push("general");
        // once every 2 minutes
        SPAWN_CHANCE = builder.defineInRange("spawn_chance",1/1024d,0,1);
        MIN_SPAWN_RADIUS = builder.defineInRange("min_spawn_radius",20,1,128);
        MAX_SPAWN_RADIUS = builder.defineInRange("max_spawn_radius",40,1,128);
        NATURAL_SPAWN_CAP = builder.defineInRange("natural_spawn_cap",50,1,1000);
        PICK_ON_CHANCE = builder.defineInRange("pick_on",1/64d,0,1);
        BUNBARIAN_SPAWN_CHANCE = builder.defineInRange("bunbarian_spawn_chance",1/1000d,0,1);
        builder.pop();
    }

}
