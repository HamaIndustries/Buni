package hama.industries.buni.client;

import hama.industries.buni.Buni;
import hama.industries.buni.BuniMod;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BuniModel extends DefaultedEntityGeoModel<Buni> {
    public BuniModel() {
        super(BuniMod.id("buni"));
    }
}
