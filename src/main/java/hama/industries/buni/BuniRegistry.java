package hama.industries.buni;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BuniRegistry {

  public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, BuniMod.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, BuniMod.MODID);

  public static final DeferredHolder<EntityType<?>,EntityType<Buni>> BUNI = ENTITIES.register("buni",
          () -> EntityType.Builder.of(Buni::new, MobCategory.CREATURE)
                  .sized(0.4F, 0.5F)
                  .clientTrackingRange(8)
                  .build("buni")
  );

  public static final DeferredHolder<Item,Item> BUNI_ITEM = ITEMS.register("buni", BuniItem::new);

  public static void registerAttributes(EntityAttributeCreationEvent event) {
    event.put(BUNI.get(), Buni.createAttributes().build());
  }

  public static void init(IEventBus bus) {
    ENTITIES.register(bus);
    ITEMS.register(bus);

  }
}
