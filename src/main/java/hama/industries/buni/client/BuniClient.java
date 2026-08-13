package hama.industries.buni.client;

import hama.industries.buni.BuniMod;
import hama.industries.buni.BuniRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = BuniMod.MODID, value = Dist.CLIENT)
public class BuniClient {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BuniRegistry.BUNI.get(), manager -> new BuniRenderer<>(manager, "buni"));
        event.registerEntityRenderer(BuniRegistry.BUNBARIAN.get(), manager -> new BuniRenderer<>(manager, "bunbarian"));
    }
}
