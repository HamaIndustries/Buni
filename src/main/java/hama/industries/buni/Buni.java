package hama.industries.buni;

import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Buni extends PathfinderMob implements GeoEntity, InventoryCarrier {
    public static final EntityDataAccessor<OptionalInt> ACTIVITY = SynchedEntityData.defineId(Buni.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    public static final EntityDataAccessor<Boolean> GUZZLING = SynchedEntityData.defineId(Buni.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> VARIANT_ID = SynchedEntityData.defineId(Buni.class,  EntityDataSerializers.INT);

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
    }

    public record Variant(String id, DyeColor color, boolean emissive) {
        private static final List<Variant> types = new ObjectArrayList<>();
        public static final Variant WHITE = new Variant("white", DyeColor.WHITE);
        public static final Variant BLACK = new Variant("black", DyeColor.BLACK);
        public static final Variant BLUE = new Variant("blue", DyeColor.BLUE);
        public static final Variant CYAN = new Variant("cyan", DyeColor.CYAN);
        public static final Variant GRAY = new Variant("gray", DyeColor.GRAY);
        public static final Variant GREEN = new Variant("green", DyeColor.GREEN);
        public static final Variant LIGHT_BLUE = new Variant("light_blue", DyeColor.LIGHT_BLUE);
        public static final Variant LIGHT_GRAY = new Variant("light_gray", DyeColor.LIGHT_GRAY);
        public static final Variant LIME = new Variant("lime", DyeColor.LIME);
        public static final Variant MAGENTA = new Variant("magenta", DyeColor.MAGENTA);
        public static final Variant ORANGE = new Variant("orange", DyeColor.ORANGE);
        public static final Variant PINK = new Variant("pink", DyeColor.PINK);
        public static final Variant PURPLE = new Variant("purple", DyeColor.PURPLE);
        public static final Variant RED = new Variant("red", DyeColor.RED);
        public static final Variant YELLOW = new Variant("yellow", DyeColor.YELLOW);
        public static final Variant DIAMOND = new Variant("diamond", null, true);
        public static final Variant ENDER = new Variant("ender", null, true);
        public static final Variant NETHER = new Variant("nether", null, true);

        public Variant(String id, DyeColor color, boolean emissive) {
            this.id = id; this.color = color; this.emissive = emissive;
            types.add(this);
        }

        private Variant(String id, DyeColor color) {
            this(id, color, false);
        }

        public int index() {
            return Variant.getID(this);
        }

        public static Variant get(int id) {
            return types.get(id);
        }

        public static int getID(Variant v) {
            return Objects.requireNonNull(types.indexOf(v));
        }

        @Nullable  public static Variant get(DyeColor color) {
            for (var v : types) if (v.color.equals(color)) return v;
            return null;
        }
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
        entityData.define(VARIANT_ID, Variant.getID(Variant.WHITE));
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
        if (level().isClientSide) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DyeItem dye ) {
            entityData.set(VARIANT_ID, Variant.getID(Objects.requireNonNull(Variant.get(dye.getDyeColor()))));
            return InteractionResult.CONSUME;
        }
        entityData.set(VARIANT_ID, variant().equals(Variant.WHITE) ? player.getRandom().nextIntBetweenInclusive(1, 15) : Variant.getID(Variant.WHITE));
        return InteractionResult.SUCCESS;
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
        if (source.getDirectEntity() instanceof LivingEntity attacker && attacker.getMainHandItem().is(ItemTags.AXES)) {
            if (level() instanceof ServerLevel serverLevel) {
                BuniRegistry.BUNI.get().spawn(serverLevel, null, clone -> {
                    clone.deserializeNBT(serializeNBT());
                    clone.setUUID(UUID.randomUUID());
                    ItemStack stack = getInventory().getItem(0);
                    int amt = stack.getCount();
                    if (amt / 2 > 0) {
                        getInventory().setItem(0, stack.copyWithCount(amt / 2));
                        clone.getInventory().setItem(0, stack.copyWithCount(amt - amt / 2));
                        clone.entityData.set(GUZZLING, true);
                    } else {
                        clone.getInventory().clearContent();
                        clone.entityData.set(GUZZLING, false);
                    }

                    Vec3 norm = attacker.position().cross(position());
                    this.knockback(0.5, norm.x, norm.z);
                    clone.knockback(0.5, -norm.x, -norm.z);
                }, blockPosition(), MobSpawnType.TRIGGERED, true, false);
            }
        } else {
            if (!getInventory().isEmpty()) {
                getInventory().removeAllItems().forEach(this::spawnAtLocation);
                this.entityData.set(GUZZLING, false);
            }
            if (pos != null) this.knockback(2, pos.x - this.getX(), pos.z - this.getZ());
        }
        return false;
    }

    public void setVariant(Variant v) {
        this.entityData.set(VARIANT_ID, Variant.getID(v));
    }

    public Variant variant() {
        return Variant.get(this.entityData.get(VARIANT_ID));
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
        tag.putInt("buni_variant", variant().index());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        readInventoryFromTag(tag);
        entityData.set(GUZZLING, !getInventory().isEmpty());
        setVariant(Variant.get(tag.getInt("buni_variant")));
    }
}
