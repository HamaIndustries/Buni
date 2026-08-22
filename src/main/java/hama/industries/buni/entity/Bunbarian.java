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

/*
//Player-Hostile Bunis that can be confused for wild bunis,
but spawn EXTREMELY rarely, in clusters of 3 - 4. Innocuous
seeming, but they have “war paint” on their face that gives
 them angry eyebrows.

Mostly identical in sounds, but they have a unique mischievous
 chuckling when aggro’d, as a warning to the player that
  something is wrong.

//They do higher damage than regular bunis (6 HP / 3 Hearts),
 modelled to swing makeshift axes instead. Zombie-like damage,
 but only with 10 HP and 2 Armor. Still immune to fire and
 fall damage, though, and still gets sent flying by being struck.

///Cannot be duplicated by axe hits or picked up in a hand.

 ///Still afraid of soul lights.

 //They are also NOT attracted
 ///by flowers or dropped items, and do not munch items off the floor.

Mostly meant to be a funny occasional troll for players
 out in the wild thinking they’re approaching a regular buni,
 only for the buni to whip around and look at them with
 angry eyes and attack. Also meant to be spawned by Ops
  for “raid” events and RP scenarios.

Their loot upon death should have some early-game
valuables when killed, like random berries/fruit/vegetables,
a random sapling or iron/copper/gold ingots.
 */

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
