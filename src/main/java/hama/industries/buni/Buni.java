package hama.industries.buni;

import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
@Mod.EventBusSubscriber
public class Buni extends PathfinderMob implements GeoEntity, InventoryCarrier {

    public static final EntityDataAccessor<OptionalInt> ACTIVITY = SynchedEntityData.defineId(Buni.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    public static final EntityDataAccessor<Boolean> GUZZLING = SynchedEntityData.defineId(Buni.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> VARIANT_ID = SynchedEntityData.defineId(Buni.class,  EntityDataSerializers.INT);

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0).add(Attributes.MOVEMENT_SPEED, 0.3d).add(Attributes.ATTACK_DAMAGE, 2);    }

    public record Variant(String id, @Nullable DyeColor color, boolean emissive) {
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
        public static final Variant BROWN = new Variant("brown", DyeColor.BROWN);
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
            for (var v : types) if (v.color != null && v.color.equals(color)) return v;
            return null;
        }
    }

    public record BuniGroupData(Variant variant) implements SpawnGroupData {}

    protected static final int MIN_TICKS_TO_PLAY_SOUND = 10 * 20;

    public int tumblingTicks;
    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final SimpleContainer inventory = new SimpleContainer(1);
    protected int hatred;
    protected int ticksSinceLastSound;
    protected boolean evil;
    @Nullable protected LivingEntity thrower;

    protected Buni(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        setCanPickUpLoot(canPickUpLoot());
        entityData.set(GUZZLING, !getInventory().isEmpty());
        ticksSinceLastSound = (int)(getRandom().nextFloat() *  MIN_TICKS_TO_PLAY_SOUND);
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
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof DyeItem dye ) {
            if (level().isClientSide) return InteractionResult.SUCCESS;
            entityData.set(VARIANT_ID, Variant.getID(Objects.requireNonNull(Variant.get(dye.getDyeColor()))));
            return InteractionResult.CONSUME;
        } else if (stack.isEmpty()) {
            if (level().isClientSide) return InteractionResult.SUCCESS;
            player.setItemInHand(hand, BuniItem.of(this));
            this.discard();
            return InteractionResult.SUCCESS;
        }
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
        return variant().emissive();
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
    }

    public boolean hasItem() {
        return entityData.get(GUZZLING);
    }

    public Activity activity() {
        return BuiltInRegistries.ACTIVITY.getHolder(entityData.get(ACTIVITY).orElse(-1)).map(Holder::get).orElse(Activity.IDLE);
    }

    public boolean isEvil() { return evil; }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= 10) {
                this.swingTime = 0;
                this.swinging = false;
            }
        } else {
            this.swingTime = 0;
        }
    }

    @Override
    protected void customServerAiStep() {
        this.getBrain().tick((ServerLevel) this.level(), this);
        BuniAi.updateActivity(this);
        this.entityData.set(ACTIVITY,
                getBrain().getActiveNonCoreActivity().map(act -> OptionalInt.of(BuiltInRegistries.ACTIVITY.getId(act))).orElse(OptionalInt.empty())
        );
        hatred = Math.max(0, hatred-1);
        tumblingTicks++;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Vec3 pos = source.getSourcePosition();
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || isEvil()) {
            return super.hurt(source, amount);
        }
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
            if (pos != null) {
                this.knockback(2, pos.x - this.getX(), pos.z - this.getZ());
                ticksSinceLastSound = 0;
                playSound(BuniSounds.HIT.get(), 0.8f, varyPitch(1, 0.1f));
            }
        }

        if (source.getDirectEntity() instanceof LivingEntity living) {
            this.annoyedBy(living);
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            ticksSinceLastSound++;
            if (ticksSinceLastSound > MIN_TICKS_TO_PLAY_SOUND && random.nextFloat() < 0.1) {
                ticksSinceLastSound = 0;
                playSound(BuniSounds.IDLE.get(), 0.8f, varyPitch(1, 0.1f));
            }

            getBrain().getMemory(BuniAi.TUMBLING).ifPresent(tumbling -> {
                if (tumbling) {
                    for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox(), entity -> entity != thrower && !(entity instanceof Buni))) {
                        entity.hurt(damageSources().mobProjectile(this, thrower != null && !thrower.isRemoved() ? thrower : null), 1);
                    }
                }
            });
        }
    }

    @Override
    public void knockback(double p_147241_, double x, double z) {
        if (!level().isClientSide) {
            tumblingTicks = 0;
            getBrain().setMemory(BuniAi.TUMBLING, true);
            getBrain().setActiveActivityIfPossible(BuniActivity.TUMBLE);
        }
        super.knockback(p_147241_, x, z);
        this.setYRot((float)Mth.atan2(z, x));
    }

    private static final Variant[] COMMON_BUNS = { Variant.WHITE, Variant.GRAY, Variant.BROWN };
    private static final Variant[] UNCOMMON_BUNS = { Variant.PINK, Variant.RED, Variant.ORANGE, Variant.BLACK };
    private static final Variant[] RARE_BUNS = { Variant.PURPLE, Variant.DIAMOND, Variant.LIME, Variant.BLUE };

    @Override
    public void swing(InteractionHand p_21007_) {
        super.swing(p_21007_);
    }

    @Override
    public boolean doHurtTarget(Entity p_21372_) {
        boolean result = super.doHurtTarget(p_21372_);
        if (result && !level().isClientSide) this.playSound(BuniSounds.ATTACK.get());
        return result;
    }

    public static BuniGroupData makeNaturalGroupData(LevelAccessor levelAccessor) {
        Variant variant;
        var key = levelAccessor.registryAccess().registry(Registries.DIMENSION_TYPE).get().getKey(levelAccessor.dimensionType());
        if (key.equals(BuiltinDimensionTypes.NETHER.location())) {
            variant = Variant.NETHER;
        } else if (key.equals(BuiltinDimensionTypes.END.location())) {
            variant = Variant.ENDER;
        } else {
            float chance = levelAccessor.getRandom().nextFloat();
            if (chance < 0.01) {
                variant = RARE_BUNS[Mth.abs(levelAccessor.getRandom().nextInt()) % RARE_BUNS.length];
            } else if (chance < 0.1) {
                variant = UNCOMMON_BUNS[Mth.abs(levelAccessor.getRandom().nextInt()) % UNCOMMON_BUNS.length];
            } else {
                variant = COMMON_BUNS[Mth.abs(levelAccessor.getRandom().nextInt()) % COMMON_BUNS.length];
            }
        }
        return new BuniGroupData(variant);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag p_21438_) {
        if (!(groupData instanceof BuniGroupData)) {
            groupData = spawnType == MobSpawnType.NATURAL ? makeNaturalGroupData(levelAccessor) : new BuniGroupData(Variant.WHITE);
        }
        this.setVariant(((BuniGroupData)groupData).variant);
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, groupData, p_21438_);
    }

    protected void annoyedBy(LivingEntity attacker) {
        if (level().isClientSide) return;
        hatred += 1000;
        if (hatred > 10000) {
            hatred = 0;
            killThisGuy(attacker);
        }
    }

    private void killThisGuy(LivingEntity target) {
        getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
        getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).ifPresent(
                entities -> entities.stream().forEach(e -> {
                    if (e instanceof Buni) {
                        e.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
                    }
                })
        );
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
        if (!level().isClientSide) {
            entityData.set(GUZZLING, !getInventory().isEmpty());
            playSound(BuniSounds.GUZZLE.get(), 0.8f, varyPitch(1, 0.15f));

            CraftingContainer craftingcontainer = new TransientCraftingContainer(new AbstractContainerMenu((MenuType)null, -1) {
                public ItemStack quickMoveStack(Player p_218264_, int p_218265_) {
                    return ItemStack.EMPTY;
                }
                public boolean stillValid(Player p_29888_) {
                    return false;
                }
            }, 1, 1);

            ItemStack stack = item.getItem();
            craftingcontainer.setItem(0, stack);
            ItemStack result = this.level().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingcontainer, this.level()).map(
                    recipe -> recipe.assemble(craftingcontainer, this.level().registryAccess())
            ).orElse(null);

            if (result != null && result.is(Tags.Items.DYES)) {
                int amount = stack.getCount() * result.getCount() * 2;
                getInventory().setItem(0, result.copyWithCount(amount));
                if (amount > stack.getMaxStackSize()) {
                    spawnAtLocation(result.copyWithCount(amount - stack.getMaxStackSize()));
                }
            }
        };
    }

    @Override
    public boolean canPickUpLoot() {
        return !isEvil() && getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT);
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
        tag.putBoolean("evil", evil);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        readInventoryFromTag(tag);
        entityData.set(GUZZLING, !getInventory().isEmpty());
        setVariant(Variant.get(tag.getInt("buni_variant")));
        evil = tag.getBoolean("evil");
    }

    private float varyPitch(float pitch, float variance) {
        return (random.nextFloat() - 0.5f) * variance + pitch;
    }

    @SubscribeEvent
    public static void protecc(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.level().isClientSide) return;
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && !(attacker instanceof Buni)){
                player.level().getEntitiesOfClass(Buni.class, player.getBoundingBox().inflate(20)).stream()
                        .findAny()
                        .ifPresent(b -> b.killThisGuy(attacker));
            }
        }
    }

    @SubscribeEvent
    public static void overrideMobGriefing(EntityMobGriefingEvent event) {
        if (event.getEntity().getType().equals(BuniRegistry.BUNI.get())) {
            event.setResult(Event.Result.ALLOW);
        }
    }

    @Override
    public double getMyRidingOffset() {
        return super.getMyRidingOffset() + 0.15;
    }
}
