package net.willowins.animewitchery.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;

public class ModRecipes {
    public static final RecipeType<AlchemyRecipe> ALCHEMY_RECIPE_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return "animewitchery:alchemy";
        }
    };

    public static final RecipeSerializer<AlchemyRecipe> ALCHEMY_RECIPE_SERIALIZER = new AlchemyRecipe.Serializer();

    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_TYPE, new Identifier(AnimeWitchery.MOD_ID, "alchemy"), ALCHEMY_RECIPE_TYPE);
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(AnimeWitchery.MOD_ID, "alchemy"), ALCHEMY_RECIPE_SERIALIZER);
    }
}
