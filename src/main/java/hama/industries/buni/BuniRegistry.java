package hama.industries.buni;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import hama.industries.buni.entity.Bunbarian;
import hama.industries.buni.entity.Buni;

import java.util.HashMap;
import java.util.Map;

public class BuniRegistry {

  public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, BuniMod.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, BuniMod.MODID);

  public static final DeferredHolder<EntityType<?>,EntityType<Buni>> BUNI = ENTITIES.register("buni",
          () -> EntityType.Builder.of(Buni::new, MobCategory.CREATURE)
                  .sized(0.4F, 0.5F)
                  .clientTrackingRange(8)
                  .build("buni")
  );

  public static final DeferredHolder<EntityType<?>,EntityType<Bunbarian>> BUNBARIAN = ENTITIES.register("bunbarian",
          () -> EntityType.Builder.of(Bunbarian::new, MobCategory.CREATURE)
                  .sized(0.4F, 0.5F)
                  .clientTrackingRange(8)
                  .build("bunbarian")
  );

  public static final Map<String, DeferredHolder<Item,Item>> BUNI_ITEMS = new HashMap<>();
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
