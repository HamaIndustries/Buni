package hama.industries.buni;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BuniMod.MODID)
public class BuniMod {

  public static final String MODID = "buni";
  public static final Logger LOGGER = LogManager.getLogger();

  public BuniMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    BuniRegistry.ENTITIES.register(eventBus);
    eventBus.addListener(BuniRegistry::registerAttributes);
  }

//  @SubscribeEvent
//  private void commonSetup(final FMLCommonSetupEvent event) {}

  public static ResourceLocation id(String path) {
    return new ResourceLocation(MODID, path);
  }
}
