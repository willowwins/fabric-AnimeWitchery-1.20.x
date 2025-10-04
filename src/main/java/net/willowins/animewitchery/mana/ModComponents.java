package net.willowins.animewitchery.mana;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ModComponents implements EntityComponentInitializer {
    public static ComponentKey<IManaComponent> PLAYER_MANA;

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // Create the component key
        PLAYER_MANA = ComponentRegistryV3.INSTANCE.getOrCreate(
                new Identifier("animewitchery", "player_mana"),
                IManaComponent.class
        );

        // Register the component for players, supplying the PlayerEntity to the factory
        registry.registerForPlayers(
                PLAYER_MANA,
                (player) -> new ManaComponent(player),
                RespawnCopyStrategy.INVENTORY
        );
    }
}
