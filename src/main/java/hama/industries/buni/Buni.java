package hama.industries.buni;

import com.mojang.serialization.Dynamic;
import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
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
import java.util.OptionalInt;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Buni extends PathfinderMob implements GeoEntity, InventoryCarrier {
    public static final EntityDataAccessor<OptionalInt> ACTIVITY = SynchedEntityData.defineId(Buni.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    public static final EntityDataAccessor<Boolean> GUZZLING = SynchedEntityData.defineId(Buni.class, EntityDataSerializers.BOOLEAN);

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final SimpleContainer inventory = new SimpleContainer(1);

    protected Buni(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        setCanPickUpLoot(canPickUpLoot());
        entityData.set(GUZZLING, !getInventory().isEmpty());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ACTIVITY, OptionalInt.of(BuiltInRegistries.ACTIVITY.getId(Activity.IDLE)));
        entityData.define(GUZZLING, false);
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

    @Override
    @SuppressWarnings("unchecked")
    public Brain<Buni> getBrain() {
        return (Brain<Buni>) super.getBrain();
    }

    public boolean hasEmissive() {
        return false;
    }

    public boolean hasItem() {
        return entityData.get(GUZZLING);
    }

    public Activity activity() {
        return BuiltInRegistries.ACTIVITY.getHolder(entityData.get(ACTIVITY).orElse(-1)).map(Holder::get).orElse(Activity.IDLE);
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
        if (!getInventory().isEmpty()) {
            getInventory().removeAllItems().forEach(this::spawnAtLocation);
            this.entityData.set(GUZZLING, false);
        }
        if (pos != null) this.knockback(2, pos.x - this.getX(), pos.z - this.getZ());
        return false;
    }

    @Override
    protected void pickUpItem(ItemEntity item) {
        InventoryCarrier.pickUpItem(this, this, item);
        if (!level().isClientSide) entityData.set(GUZZLING, !getInventory().isEmpty());
    }

    @Override
    public boolean canPickUpLoot() {
        return getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT);
    }

    @Override
    public SimpleContainer getInventory() {
        return inventory;
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return getInventory().canAddItem(stack);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        writeInventoryToTag(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        readInventoryFromTag(tag);
    }
}
