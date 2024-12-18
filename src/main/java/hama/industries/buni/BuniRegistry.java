package hama.industries.buni;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BuniRegistry {

  public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BuniMod.MODID);

  public static final RegistryObject<EntityType<Buni>> BUNI = ENTITIES.register("buni",
          () -> EntityType.Builder.of(Buni::new, MobCategory.CREATURE)
                  .sized(0.4F, 0.5F)
                  .clientTrackingRange(8)
                  .build("buni")
  );

  public static void registerAttributes(EntityAttributeCreationEvent event) {
    event.put(BUNI.get(), Buni.createAttributes().build());
  }
}
