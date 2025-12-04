package hama.industries.buni;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BuniSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, BuniMod.MODID);

    private static DeferredHolder<SoundEvent,SoundEvent> register(String id) {
        ResourceLocation loc = BuniMod.id("entity.buni." + id);
        return SOUNDS.register(loc.getPath(), () -> SoundEvent.createVariableRangeEvent(loc));
    }

    public static final DeferredHolder<SoundEvent,SoundEvent> ATTACK = register("attack");
    public static final DeferredHolder<SoundEvent,SoundEvent> DEATH = register("death");
    public static final DeferredHolder<SoundEvent,SoundEvent> REPELLED = register("repelled");
    public static final DeferredHolder<SoundEvent,SoundEvent> GUZZLE = register("guzzle");
    public static final DeferredHolder<SoundEvent,SoundEvent> HIT = register("hit");
    public static final DeferredHolder<SoundEvent,SoundEvent> IDLE = register("idle");

    public static void init(IEventBus bus) { SOUNDS.register(bus); }
}
