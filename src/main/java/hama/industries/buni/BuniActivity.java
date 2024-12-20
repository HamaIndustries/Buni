package hama.industries.buni;

import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import software.bernie.geckolib.core.animation.RawAnimation;

public class BuniActivity extends Activity {
    public static BuniActivity DANCE = new BuniActivity("dance", BuniAnimations.DANCE);
    public static BuniActivity TUMBLE = new BuniActivity("tumble", BuniAnimations.TUMBLE);
    public static BuniActivity GRABBED = new BuniActivity("grabbed", BuniAnimations.GRABBED);
    public static BuniActivity LOAF = new BuniActivity("loaf", BuniAnimations.LOAF);

    public static void registerActivities(RegisterEvent event) {
        event.register(
                ForgeRegistries.ACTIVITIES.getRegistryKey(),
                registry -> {
                    registry.register(BuniMod.id(DANCE.getName()), DANCE);
                    registry.register(BuniMod.id(TUMBLE.getName()), TUMBLE);
                    registry.register(BuniMod.id(GRABBED.getName()), GRABBED);
                    registry.register(BuniMod.id(LOAF.getName()), LOAF);
                }
        );
    }

    public final RawAnimation animation;
    public BuniActivity(String id, RawAnimation animation) {
        super(id);
        this.animation = animation;
    }
}
