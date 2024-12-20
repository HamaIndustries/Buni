package hama.industries.buni;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionHelper {
    public static boolean compare(RegistryAccess registryAccess, DimensionType type, ResourceKey<DimensionType> key) {
        return registryAccess.registry(Registries.DIMENSION_TYPE).get().getKey(type).equals(key);
    }
}
