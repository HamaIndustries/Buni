package hama.industries.buni;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BuniSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BuniMod.MODID);

    private static RegistryObject<SoundEvent> register(String id) {
        ResourceLocation loc = BuniMod.id("entity.buni." + id);
        return SOUNDS.register(loc.getPath(), () -> SoundEvent.createVariableRangeEvent(loc));
    }

    public static final RegistryObject<SoundEvent> ATTACK = register("attack");
    public static final RegistryObject<SoundEvent> DEATH = register("death");
    public static final RegistryObject<SoundEvent> GUZZLE = register("guzzle");
    public static final RegistryObject<SoundEvent> HIT = register("hit");
    public static final RegistryObject<SoundEvent> IDLE = register("idle");

    public static void init(IEventBus bus) { SOUNDS.register(bus); }
}
