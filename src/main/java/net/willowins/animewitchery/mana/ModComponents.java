package net.willowins.animewitchery.mana;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ModComponents implements EntityComponentInitializer {
    public static ComponentKey<IManaComponent> PLAYER_MANA;

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // SAFE: only runs when CCA loads your entrypoint
        PLAYER_MANA = ComponentRegistryV3.INSTANCE.getOrCreate(
                new Identifier("animewitchery", "player_mana"),
                IManaComponent.class
        );

        registry.registerFor(PlayerEntity.class, PLAYER_MANA, player -> new ManaComponent());
    }
}
