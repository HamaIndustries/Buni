package hama.industries.buni.client;

import hama.industries.buni.Buni;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BuniRenderer extends GeoEntityRenderer<Buni> {
    public BuniRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BuniModel());
        this.scaleWidth = 0.7f;
        this.scaleHeight = 0.7f;
    }
}
