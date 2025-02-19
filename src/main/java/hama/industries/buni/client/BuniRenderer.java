package hama.industries.buni.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hama.industries.buni.Buni;
import hama.industries.buni.BuniMod;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.FastBoneFilterGeoLayer;

import java.util.List;

public class BuniRenderer extends GeoEntityRenderer<Buni> {

    private static class OptionalGlowingGeoLayer extends AutoGlowingGeoLayer<Buni> {
        public OptionalGlowingGeoLayer(GeoRenderer<Buni> renderer) {
            super(renderer);
        }

        public void render(PoseStack poseStack, Buni buni, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            // disabled for now; emissive has an issue either due to the model or minecraft or both
//            if (buni.hasEmissive()) {
//                RenderType emissiveRenderType = this.getRenderType(buni);
//                this.getRenderer().reRender(bakedModel, poseStack, bufferSource, buni, emissiveRenderType, bufferSource.getBuffer(emissiveRenderType), partialTick, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
//            }
        }
    }

    public BuniRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BuniModel());
        addRenderLayer(new OptionalGlowingGeoLayer(this));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("mallet"), this::updateMalletVisibility));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("tube"), this::updateTubeVisibility));
        addRenderLayer(new FastBoneFilterGeoLayer<>(this, () -> List.of("glasses"),
                (bone, buni, ticks) -> bone.setHidden(!buni.getName().getString().equals("yuki"))
        ));
        this.scaleWidth = 0.7f;
        this.scaleHeight = 0.7f;
    }

    public void updateMalletVisibility(GeoBone bone, Buni buni, float partialTicks) {
        bone.setHidden(!buni.swinging);
    }

    public void updateTubeVisibility(GeoBone bone, Buni buni, float partialTicks) {
        bone.setHidden(!buni.isInWater());
    }

    @Override
    public ResourceLocation getTextureLocation(Buni buni) {
        if (buni.variant().equals(Buni.Variant.WHITE)) {
            return super.getTextureLocation(buni);
        } else {
            return BuniMod.id("textures/entity/buni_" + buni.variant().id() + ".png");
        }
    }
}
