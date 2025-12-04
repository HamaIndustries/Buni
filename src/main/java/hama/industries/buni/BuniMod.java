package hama.industries.buni;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BuniMod.MODID)
public class BuniMod {

  public static final String MODID = "buni";
  public static final Logger LOGGER = LogManager.getLogger();

  public BuniMod(IEventBus eventBus,ModContainer modContainer) {
    modContainer.registerConfig(ModConfig.Type.SERVER,BuniConfig.SERVER_SPEC);
    eventBus.addListener(BuniRegistry::registerAttributes);
    eventBus.addListener(BuniActivity::registerActivities);
    eventBus.addListener(BuniAi::registerSensorsAndMemories);
    eventBus.addListener(BuniDatagen::generateData);
    BuniRegistry.init(eventBus);
    BuniSounds.init(eventBus);
    NeoForge.EVENT_BUS.addListener(BuniSpawner::tickSpawnBunis);
    BuniGameRules.init();
    BuniEvents.init();
  }

//  @SubscribeEvent
//  private void commonSetup(final FMLCommonSetupEvent event) {}

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }
}
