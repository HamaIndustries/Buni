package hama.industries.buni;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;


public class Buni extends PathfinderMob implements GeoEntity {

    public record Activity(RawAnimation animation) {
        public static Activity NONE = new Activity(BuniAnimations.IDLE);
        public static Activity ATTACK = new Activity(BuniAnimations.ATTACK);
        public static Activity DANCE = new Activity(BuniAnimations.DANCE);
        public static Activity TUMBLE = new Activity(BuniAnimations.TUMBLE);
        public static Activity GRABBED = new Activity(BuniAnimations.GRABBED);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Activity activity = Activity.NONE;

    protected Buni(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        super.registerGoals();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        BuniAnimations.registerControllers(this, controllers);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
//        if (hand == InteractionHand.MAIN_HAND)
//            this.dance = !dance;
        return super.interactAt(player, hitPos, hand);
    }

    @Override
    protected Brain.Provider<Buni> brainProvider() {
        return BuniAi.brainProvider();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> data) {
        return BuniAi.makeBrain(this.brainProvider().makeBrain(data));
    }

    public boolean hasEmissive() {
        return false;
    }

    boolean hasItem() {
        return level().getNearestPlayer(this, 5) != null;
    }

    Activity activity() {
        return activity;
    }
}
