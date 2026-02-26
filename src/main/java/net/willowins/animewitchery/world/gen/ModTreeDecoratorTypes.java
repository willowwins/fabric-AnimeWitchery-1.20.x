package net.willowins.animewitchery.world.gen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.world.gen.treedecorator.RosewillowVineDecorator;

public class ModTreeDecoratorTypes {
    public static final TreeDecoratorType<RosewillowVineDecorator> ROSEWILLOW_VINE_DECORATOR = register(
            "rosewillow_vine_decorator", RosewillowVineDecorator.CODEC);

    private static <P extends net.minecraft.world.gen.treedecorator.TreeDecorator> TreeDecoratorType<P> register(
            String name, com.mojang.serialization.Codec<P> codec) {
        return Registry.register(Registries.TREE_DECORATOR_TYPE, new Identifier(AnimeWitchery.MOD_ID, name),
                new TreeDecoratorType<>(codec));
    }

    public static void registerModTreeDecoratorTypes() {
        AnimeWitchery.LOGGER.info("Registering Mod Tree Decorator Types for " + AnimeWitchery.MOD_ID);
    }
}
