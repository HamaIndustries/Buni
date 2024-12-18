package hama.industries.buni;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BuniTags {
    public static class Items {
        public static TagKey<Item> BUNI_TEMPTATIONS = TagKey.create(Registries.ITEM, BuniMod.id("buni_temptations"));
    }
}
