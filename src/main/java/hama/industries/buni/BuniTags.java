package hama.industries.buni;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class BuniTags {
    public static class Items {
        public static TagKey<Item> BUNI_TEMPTATIONS = TagKey.create(Registries.ITEM, BuniMod.id("buni_temptations"));
    }

    public static class  EntityTypes {
        public static final TagKey<EntityType<?>> BUNI_ATTACK = TagKey.create(Registries.ENTITY_TYPE,BuniMod.id("buni_attack"));
        public static final TagKey<EntityType<?>> KILL_ON_SIGHT = TagKey.create(Registries.ENTITY_TYPE,BuniMod.id("kill_on_sight"));
    }

    public static class DamageTypes {
        public static final TagKey<DamageType> BUNI_IMMUNE = TagKey.create(Registries.DAMAGE_TYPE, BuniMod.id("buni_immune"));
    }
}
