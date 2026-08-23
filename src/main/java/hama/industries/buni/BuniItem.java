package hama.industries.buni;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import hama.industries.buni.entity.Buni;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class BuniItem extends Item {
    protected Buni.Variant variant;

    public BuniItem(Buni.Variant variant) {
        super(new Item.Properties().stacksTo(1));
        this.variant = variant;
    }

    public static ItemStack of(Buni buni) {
        ItemStack stack = BuniRegistry.BUNI_ITEMS.get(buni.variant().id()).get().getDefaultInstance();

        CustomData customData = buni.createCustomData();
        stack.set(DataComponents.ENTITY_DATA, customData);//stored_buni, does this need a data component?
        if (buni.hasCustomName()) {
            stack.set(DataComponents.CUSTOM_NAME, buni.getCustomName());
        }
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            CustomData tag = stack.get(DataComponents.ENTITY_DATA);
            BuniRegistry.BUNI.get().spawn((ServerLevel) level, e -> {
                if (stack.has(DataComponents.CUSTOM_NAME)) {
                    e.setCustomName(stack.getHoverName());
                }
                e.setVariant(variant);
                if (tag != null) {
                    e.load(tag.copyTag());
                }
                Vec3 look = player.getLookAngle();
                e.setPos(player.getEyePosition().add(look));
                e.knockback(2, -look.x, -look.z);
                e.setThrower(player);
                stack.shrink(1);
            }, player.getOnPos(), MobSpawnType.BUCKET, true, false);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        // @copy vanilla SpawnEggItem
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack stack = pContext.getItemInHand();
            BlockPos blockpos = pContext.getClickedPos();
            Direction direction = pContext.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            BlockPos blockpos1 = blockstate.getCollisionShape(level, blockpos).isEmpty() ? blockpos : blockpos.relative(direction);

            Buni buni = BuniRegistry.BUNI.get().spawn((ServerLevel) level,  stack, player,
                    blockpos1, MobSpawnType.BUCKET, true, !Objects.equals(blockpos, blockpos1) &&
                            direction == Direction.UP);

            if (buni != null) {
                buni.setVariant(variant);
                stack.shrink(1);
                Vec3 pos = Vec3.atBottomCenterOf(blockpos1);
                buni.setPos(pos);
                level.gameEvent(player, GameEvent.ENTITY_PLACE, blockpos);
            }
        }
        return InteractionResult.CONSUME;
    }
}
