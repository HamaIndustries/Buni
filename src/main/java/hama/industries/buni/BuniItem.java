package hama.industries.buni;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class BuniItem extends Item {
    public BuniItem() {
        super(new Item.Properties().stacksTo(1));
    }

    public static ItemStack of(Buni buni) {
        ItemStack stack = BuniRegistry.BUNI_ITEM.get().getDefaultInstance();
        stack.addTagElement("stored_buni", buni.serializeNBT());
        if (buni.hasCustomName()) {
            stack.setHoverName(buni.getCustomName());
        }
        return stack;
    }

    public InteractionResult useOn(UseOnContext pContext) {
        // @copy vanilla SpawnEggItem
        Level level = pContext.getLevel();
        if (pContext.getPlayer() == null) return InteractionResult.FAIL;
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack stack = pContext.getItemInHand();
            BlockPos blockpos = pContext.getClickedPos();
            Direction direction = pContext.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            BlockPos blockpos1;
            if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            CompoundTag tag = stack.getTagElement("stored_buni");

            if (BuniRegistry.BUNI.get().spawn((ServerLevel) level, null, e -> {
                Vec3 pos = e.position();
                e.deserializeNBT(tag);
                e.setPos(pos);
                if (stack.hasCustomHoverName()) {
                    e.setCustomName(stack.getHoverName());
                }
            }, blockpos1, MobSpawnType.BUCKET, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP) != null) {
                pContext.getPlayer().setItemInHand(pContext.getHand(), ItemStack.EMPTY);
                level.gameEvent(pContext.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            CompoundTag tag = stack.getTagElement("stored_buni");
            BuniRegistry.BUNI.get().spawn((ServerLevel) level, null, e -> {
                e.deserializeNBT(tag);
                Vec3 look = player.getLookAngle();
                e.setPos(player.getEyePosition().add(look));
                e.knockback(2, -look.x, -look.z);
                e.thrower = player;
                if (stack.hasCustomHoverName()) {
                    e.setCustomName(stack.getHoverName());
                }
            }, player.getOnPos(), MobSpawnType.BUCKET, true, false);
        }
        return InteractionResultHolder.sidedSuccess(ItemStack.EMPTY, level.isClientSide);
    }
}
