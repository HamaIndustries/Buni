package hama.industries.buni.client;

import hama.industries.buni.BuniMod;
import hama.industries.buni.entity.Buni;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.FastBoneFilterGeoLayer;

import java.util.List;

public class BuniRenderer<T extends Buni> extends GeoEntityRenderer<T> {
    protected final String type;
    public BuniRenderer(EntityRendererProvider.Context renderManager, String type) {
        super(renderManager, new BuniModel<>(type));
        this.type = type;
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
    public ResourceLocation getTextureLocation(T buni) {
        if (buni.variant().equals(Buni.Variant.WHITE)) {
            return super.getTextureLocation(buni);
        } else {
            return BuniMod.id("textures/entity/" + type + "_" + buni.variant().id() + ".png");
        }
    }
}
