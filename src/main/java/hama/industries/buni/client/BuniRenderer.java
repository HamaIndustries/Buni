package hama.industries.buni.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hama.industries.buni.Buni;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BuniRenderer extends GeoEntityRenderer<Buni> {

    private static class OptionalGlowingGeoLayer extends AutoGlowingGeoLayer<Buni> {
        public OptionalGlowingGeoLayer(GeoRenderer<Buni> renderer) {
            super(renderer);
        }

        public void render(PoseStack poseStack, Buni buni, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (buni.hasEmissive()) {
                RenderType emissiveRenderType = this.getRenderType(buni);
                this.getRenderer().reRender(bakedModel, poseStack, bufferSource, buni, emissiveRenderType, bufferSource.getBuffer(emissiveRenderType), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    public BuniRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BuniModel());
        addRenderLayer(new OptionalGlowingGeoLayer(this));
        this.scaleWidth = 0.7f;
        this.scaleHeight = 0.7f;
    }
}
