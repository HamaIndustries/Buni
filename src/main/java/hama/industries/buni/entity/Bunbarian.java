package hama.industries.buni.entity;

import com.mojang.serialization.Dynamic;
import hama.industries.buni.BuniAi;
import hama.industries.buni.BuniSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;

public class Bunbarian extends Buni {

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.ARMOR, 2)
                .add(Attributes.MOVEMENT_SPEED, 0.3d)
                .add(Attributes.ATTACK_DAMAGE, 6);
    }

    public Bunbarian(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        // lets us take damage, prevents picking up
    }

    @Override
    protected Brain.Provider<Bunbarian> brainProvider() {
        return BuniAi.Hostile.brainProvider();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> data) {
        return BuniAi.Hostile.makeBrain(this.brainProvider().makeBrain(data));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<Buni> getBrain() {
        return (Brain<Buni>) super.getBrain();
    }

    @Override
    public boolean isEvil() {
        return true;
    }

    @Override
    public boolean isNoPickup() {
        return true;
    }

    @Override
    public SoundEvent hitSound() {
        return SoundEvents.PLAYER_ATTACK_STRONG;
    }

    long ticksSinceLastChuckle = 0;
    @Override
    public void tick() {
        super.tick();
        getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(entity -> {
            if (ticksSinceLastChuckle < 0) {
                ticksSinceLastChuckle = this.random.nextInt(200) + 200;
                this.playSound(BuniSounds.MISCHIEF.get());
            }
        });
        ticksSinceLastChuckle--;
    }
}
