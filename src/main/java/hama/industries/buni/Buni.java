package hama.industries.buni;

import com.mojang.serialization.Dynamic;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.OptionalInt;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Buni extends PathfinderMob implements GeoEntity {
    public static final EntityDataAccessor<OptionalInt> ACTIVITY = SynchedEntityData.defineId(Buni.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ItemStack storedItem = ItemStack.EMPTY;
    @Nullable private BlockPos jukeboxPos = null;

    protected Buni(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ACTIVITY, OptionalInt.of(BuiltInRegistries.ACTIVITY.getId(Activity.IDLE)));
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
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
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
        return BuiltInRegistries.ACTIVITY.getHolder(entityData.get(ACTIVITY).orElse(-1)).map(Holder::get).orElse(Activity.IDLE);
    }

    public void setJukeboxPos(@Nullable BlockPos pos) {
        jukeboxPos = pos;
    }

    @Nullable
    public BlockPos getJukeboxPos() {
        return jukeboxPos;
    }

    @Override
    protected void customServerAiStep() {
        this.getBrain().tick((ServerLevel) this.level(), this);
        BuniAi.updateActivity(this);
        this.entityData.set(ACTIVITY,
                getBrain().getActiveNonCoreActivity().map(act -> OptionalInt.of(BuiltInRegistries.ACTIVITY.getId(act))).orElse(OptionalInt.empty())
        );
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Vec3 pos = source.getSourcePosition();
        if (pos != null) this.knockback(2, pos.x - this.getX(), pos.z - this.getZ());
        return false;
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
