package hama.industries.buni;

import hama.industries.buni.entity.Bunbarian;
import hama.industries.buni.entity.Buni;
import hama.industries.buni.item.BuniItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class BuniRegistry {

  public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BuniMod.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BuniMod.MODID);

  public static final RegistryObject<EntityType<Buni>> BUNI = ENTITIES.register("buni",
          () -> EntityType.Builder.of(Buni::new, MobCategory.CREATURE)
                  .sized(0.4F, 0.5F)
                  .clientTrackingRange(8)
                  .build("buni")
  );

  public static final RegistryObject<EntityType<Bunbarian>> BUNBARIAN = ENTITIES.register("bunbarian",
          () -> EntityType.Builder.of(Bunbarian::new, MobCategory.CREATURE)
                  .sized(0.4F, 0.5F)
                  .clientTrackingRange(8)
                  .build("bunbarian")
  );

  public static final Map<String, RegistryObject<Item>> BUNI_ITEMS = new HashMap<>();
  static {
    for (Buni.Variant variant : Buni.Variant.allVariants()) {
      BUNI_ITEMS.put(variant.id(), ITEMS.register("buni_" + variant.id(), () -> new BuniItem(variant)));
    }
  }

  public static void registerAttributes(EntityAttributeCreationEvent event) {
    event.put(BUNI.get(), Buni.createAttributes().build());
    event.put(BUNBARIAN.get(), Bunbarian.createAttributes().build());
  }

  public static void init(IEventBus bus) {
    ENTITIES.register(bus);
    ITEMS.register(bus);
  }

}
