package hama.industries.buni;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
