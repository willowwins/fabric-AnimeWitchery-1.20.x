package net.willowins.animewitchery.mana;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import net.willowins.animewitchery.component.IVanityComponent;
import net.willowins.animewitchery.component.VanityComponent;

public class ModComponents implements EntityComponentInitializer {
        public static ComponentKey<IManaComponent> PLAYER_MANA;
        public static ComponentKey<IVanityComponent> VANITY;
        public static ComponentKey<net.willowins.animewitchery.component.IClassComponent> CLASS_DATA;

        @Override
        public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
                // Create the component key
                PLAYER_MANA = ComponentRegistryV3.INSTANCE.getOrCreate(
                                new Identifier("animewitchery", "player_mana"),
                                IManaComponent.class);
                VANITY = ComponentRegistryV3.INSTANCE.getOrCreate(
                                new Identifier("animewitchery", "vanity"),
                                IVanityComponent.class);

                // Register the component for players, supplying the PlayerEntity to the factory
                registry.registerForPlayers(
                                PLAYER_MANA,
                                (player) -> new ManaComponent(player),
                                RespawnCopyStrategy.INVENTORY);
                registry.registerForPlayers(
                                VANITY,
                                (player) -> new VanityComponent(player),
                                RespawnCopyStrategy.INVENTORY);

                CLASS_DATA = ComponentRegistryV3.INSTANCE.getOrCreate(
                                new Identifier("animewitchery", "class_data"),
                                net.willowins.animewitchery.component.IClassComponent.class);

                registry.registerForPlayers(
                                CLASS_DATA,
                                (player) -> {
                                        try {
                                                return new net.willowins.animewitchery.component.ClassComponent(player);
                                        } catch (Throwable t) {
                                                System.err.println(
                                                                "CRITICAL ERROR: Failed to create ClassComponent for player: "
                                                                                + player);
                                                t.printStackTrace();
                                                throw new RuntimeException("Failed to create ClassComponent", t);
                                        }
                                },
                                RespawnCopyStrategy.ALWAYS_COPY);
        }
}
