package hama.industries.buni.client;

import hama.industries.buni.Buni;
import hama.industries.buni.BuniAnimations;
import hama.industries.buni.BuniMod;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BuniModel extends DefaultedEntityGeoModel<Buni> {
    public BuniModel() {
        super(BuniMod.id("buni"));
    }

    @Override
    public void setCustomAnimations(Buni animatable, long instanceId, AnimationState<Buni> animationState) {

        CoreGeoBone head = getAnimationProcessor().getBone("head");

        RawAnimation raw = animationState.getController().getCurrentRawAnimation();
        AnimationController<Buni> controller = animationState.getController();

        if (head != null && animationState.getData(BuniAnimations.LOOK_AROUND)) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
