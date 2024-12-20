package hama.industries.buni;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BuniRegistry {

  public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BuniMod.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BuniMod.MODID);

  public static final RegistryObject<EntityType<Buni>> BUNI = ENTITIES.register("buni",
          () -> EntityType.Builder.of(Buni::new, MobCategory.CREATURE)
                  .sized(0.4F, 0.5F)
                  .clientTrackingRange(8)
                  .build("buni")
  );

  public static final RegistryObject<Item> BUNI_ITEM = ITEMS.register("buni", BuniItem::new);

  public static void registerAttributes(EntityAttributeCreationEvent event) {
    event.put(BUNI.get(), Buni.createAttributes().build());
  }

  public static void init(IEventBus bus) {
    ITEMS.register(bus);
  }
}
