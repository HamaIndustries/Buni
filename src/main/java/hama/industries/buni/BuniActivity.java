package hama.industries.buni;

import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import software.bernie.geckolib.core.animation.RawAnimation;

public class BuniActivity extends Activity {
    public static BuniActivity NONE = new BuniActivity("none", BuniAnimations.IDLE);
    public static BuniActivity ATTACK = new BuniActivity("attack", BuniAnimations.ATTACK);
    public static BuniActivity DANCE = new BuniActivity("dance", BuniAnimations.DANCE);
    public static BuniActivity TUMBLE = new BuniActivity("tumble", BuniAnimations.TUMBLE);
    public static BuniActivity GRABBED = new BuniActivity("grabbed", BuniAnimations.GRABBED);

    public static void registerActivities(RegisterEvent event) {
        event.register(
                ForgeRegistries.ACTIVITIES.getRegistryKey(),
                registry -> {
                    registry.register(BuniMod.id(NONE.getName()), NONE);
                    registry.register(BuniMod.id(ATTACK.getName()), ATTACK);
                    registry.register(BuniMod.id(DANCE.getName()), DANCE);
                    registry.register(BuniMod.id(TUMBLE.getName()), TUMBLE);
                    registry.register(BuniMod.id(GRABBED.getName()), GRABBED);
                }
        );
    }

    public final RawAnimation animation;
    public BuniActivity(String id, RawAnimation animation) {
        super(id);
        this.animation = animation;
    }
}
