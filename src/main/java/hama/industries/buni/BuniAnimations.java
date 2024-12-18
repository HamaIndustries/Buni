package hama.industries.buni;

import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.HashSet;
import java.util.Set;

public class BuniAnimations {
    public static final RawAnimation GUZZLE = RawAnimation.begin().thenPlay("animation.buni.guzzled");
    public static final RawAnimation UNGUZZLE = RawAnimation.begin().thenPlay("animation.buni.unguzzled");
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("animation.buni.idle");
    public static final RawAnimation RUN = RawAnimation.begin().thenPlay("animation.buni.runi");
    public static final RawAnimation DANCE = RawAnimation.begin().thenPlay("animation.buni.bopi");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.buni.attack");
    public static final RawAnimation TUMBLE = RawAnimation.begin().thenPlay("animation.buni.tumble");
    public static final RawAnimation GRABBED = RawAnimation.begin().thenPlay("animation.buni.grabbed");
    public static final RawAnimation LOAF = RawAnimation.begin().thenPlay("animation.buni.loaf");

    public static void registerControllers(Buni buni, AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                DefaultAnimations.basicPredicateController(buni, GUZZLE, UNGUZZLE, (a, b) -> buni.hasItem()),
                new AnimationController<>(buni, "main_anim", 10, (state) -> {
                    Buni.Activity activity = buni.activity();
                    if (activity == Buni.Activity.NONE) {
                        return state.setAndContinue(state.isMoving() ? RUN : IDLE);
                    }
                    return state.setAndContinue(activity.animation());
                })
        );
    }
}
