package hama.industries.buni;

import net.minecraft.core.component.DataComponents;
import hama.industries.buni.entity.Buni;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
                if (tag != null && !tag.isEmpty()) {
                    e.load(tag.copyTag());
                    Vec3 look = player.getLookAngle();
                    e.setPos(player.getEyePosition().add(look));
                    e.knockback(2, -look.x, -look.z);
                    e.setThrower(player);
                }
            }, player.getOnPos(), MobSpawnType.BUCKET, true, false);
        }
        return InteractionResultHolder.sidedSuccess(ItemStack.EMPTY, level.isClientSide);
    }
}
