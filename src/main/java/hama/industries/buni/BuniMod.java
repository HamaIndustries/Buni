package hama.industries.buni;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BuniMod.MODID)
public class BuniMod {

  public static final String MODID = "buni";
  public static final Logger LOGGER = LogManager.getLogger();

  public BuniMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    BuniRegistry.ENTITIES.register(eventBus);
    eventBus.addListener(BuniRegistry::registerAttributes);
    eventBus.addListener(BuniActivity::registerActivities);
    eventBus.addListener(BuniAi::registerSensorsAndMemories);
    eventBus.addListener(BuniDatagen::generateData);
    eventBus.addListener(BuniSpawnModifier::prioritizeOrigin);
  }

//  @SubscribeEvent
//  private void commonSetup(final FMLCommonSetupEvent event) {}

  public static ResourceLocation id(String path) {
    return new ResourceLocation(MODID, path);
  }
}
