package hama.industries.buni;

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
    BuniRegistry.BLOCKS.register(eventBus);
    BuniRegistry.ITEMS.register(eventBus);
    BuniRegistry.TILE_ENTITIES.register(eventBus);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
  }

  private void setup(final FMLCommonSetupEvent event) {
    //    MinecraftForge.EVENT_BUS.register(new WhateverEvents()); 
  }

  private void setupClient(final FMLClientSetupEvent event) {
    //for client side only setup
  }
}
