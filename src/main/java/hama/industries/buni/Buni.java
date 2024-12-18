package hama.industries.buni;

import com.mojang.serialization.Dynamic;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Buni extends PathfinderMob implements GeoEntity {

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private BuniActivity activity = BuniActivity.NONE;
    private ItemStack storedItem = ItemStack.EMPTY;

    protected Buni(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
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
//        if (level().isClientSide) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching()) {
            player.setItemInHand(hand, storedItem);
            storedItem = stack;
            return InteractionResult.CONSUME;
        } else {
            this.activity = this.activity == BuniActivity.NONE ? BuniActivity.DANCE : BuniActivity.NONE;
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected Brain.Provider<Buni> brainProvider() {
        return BuniAi.brainProvider();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> data) {
        return BuniAi.makeBrain(this.brainProvider().makeBrain(data));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<Buni> getBrain() {
        return (Brain<Buni>) super.getBrain();
    }

    public boolean hasEmissive() {
        return false;
    }

    public boolean hasItem() {
        return !storedItem.isEmpty();
    }

    public Activity activity() {
        return activity;
    }

    @Override
    protected void customServerAiStep() {
        this.getBrain().tick((ServerLevel) this.level(), this);

    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.put(BuniMod.MODID + ":guzzled_item", storedItem.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.storedItem = ItemStack.of(nbt.getCompound(BuniMod.MODID + ":guzzled_item"));
    }
}
