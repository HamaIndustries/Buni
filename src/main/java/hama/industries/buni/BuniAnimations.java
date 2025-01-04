package hama.industries.buni;

import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.DataTicket;
import software.bernie.geckolib.core.object.PlayState;

public class BuniAnimations {
    public static final RawAnimation GUZZLE = RawAnimation.begin().thenPlay("animation.buni.guzzled");
    public static final RawAnimation UNGUZZLE = RawAnimation.begin().thenPlay("animation.buni.unguzzled");
    public static final RawAnimation IDLE = RawAnimation.begin().thenPlay("animation.buni.idle");
    public static final RawAnimation RUN = RawAnimation.begin().thenPlay("animation.buni.runi");
    public static final RawAnimation DANCE = RawAnimation.begin().thenPlay("animation.buni.bopi");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.buni.attack");
    public static final RawAnimation TUMBLE = RawAnimation.begin().thenPlay("animation.buni.tumble");
    public static final RawAnimation GRABBED = RawAnimation.begin().thenPlay("animation.buni.grabbed");
    public static final RawAnimation SWIM = RawAnimation.begin().thenPlay("animation.buni.swim");
    public static final RawAnimation LOAF = RawAnimation.begin().thenPlay("animation.buni.loaf");

    public static final DataTicket<Boolean> LOOK_AROUND = new DataTicket<>("buni_look_around", Boolean.class);

    public static void registerControllers(Buni buni, AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                DefaultAnimations.basicPredicateController(buni, GUZZLE, UNGUZZLE, (a, b) -> buni.hasItem()),
                new AnimationController<>(buni, "main_anim", 10, (state) -> {
                    RawAnimation currentAnim;
                    if (buni.activity() instanceof BuniActivity buniActivity && buniActivity != BuniActivity.LOAF) {
                        state.setData(LOOK_AROUND, false);
                        currentAnim = buniActivity == BuniActivity.TUMBLE ? IDLE : buniActivity.animation;
                    } else {
                        state.setData(LOOK_AROUND, true);
                        currentAnim = buni.isInWater() ? SWIM
                                : state.isMoving() ? RUN
                                : (buni.activity() == BuniActivity.LOAF) ? LOAF
                                : IDLE;
                    }
                    return state.setAndContinue(currentAnim);
                }),
                new AnimationController<>(buni, "attack", 5, state -> {
                    if (buni.swinging)
                        return state.setAndContinue(ATTACK);
                    state.getController().forceAnimationReset();
                    return PlayState.STOP;
                }),
                new AnimationController<>(buni, "tumble", 0, state -> {
                    if (buni.activity() == BuniActivity.TUMBLE) {
                        return state.setAndContinue(TUMBLE);
                    }
                    state.getController().forceAnimationReset();
                    return PlayState.STOP;
                })
        );
    }
}
