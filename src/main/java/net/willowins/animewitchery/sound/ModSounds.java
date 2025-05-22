package net.willowins.animewitchery.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;

public class ModSounds {
public static final SoundEvent LEMON_HATSUNE_MIKU = registerSoundEvent("hatsune_miku_lemon");
public static final SoundEvent LASER_CHARGE = registerSoundEvent("laser_charge");
public static final SoundEvent LASER_SHOOT = registerSoundEvent("laser_shoot");




private static SoundEvent registerSoundEvent(String name){
    Identifier id = new Identifier(AnimeWitchery.MOD_ID, name);
    return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
}

    public static void registerSounds() {
        AnimeWitchery.LOGGER.info("Registering Sounds for " + AnimeWitchery.MOD_ID);
    }
}
