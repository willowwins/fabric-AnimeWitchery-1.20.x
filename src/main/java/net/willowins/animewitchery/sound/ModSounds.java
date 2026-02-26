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
    public static final SoundEvent OBELISK_ACTIVATE = registerSoundEvent("obelisk_activate");
    public static final SoundEvent OBELISK_HUM = registerSoundEvent("obelisk_hum");
    public static final SoundEvent OBELISK_MESSAGE1 = registerSoundEvent("obelisk_message1");
    public static final SoundEvent OBELISK_MESSAGE2 = registerSoundEvent("obelisk_message2");
    public static final SoundEvent OBELISK_BREAK_ONCE = registerSoundEvent("obelisk_break_once");
    public static final SoundEvent MONSTER_STATUE_AMBIENT = registerSoundEvent("monster_statue_ambient");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(AnimeWitchery.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        AnimeWitchery.LOGGER.info("Registering Sounds for " + AnimeWitchery.MOD_ID);
    }
}
