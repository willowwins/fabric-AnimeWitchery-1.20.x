package net.willowins.animewitchery.potion;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;

public class ModBrewingRecipes {
    
    public static void registerBrewingRecipes() {
        // Base: Awkward Potion + Amethyst Shard = Mana Regen Potion
        FabricBrewingRecipeRegistry.registerPotionRecipe(
                Potions.AWKWARD,
                Ingredient.ofItems(Items.AMETHYST_SHARD),
                ModPotions.MANA_REGEN
        );

        // Extend: Mana Regen + Redstone = Long Mana Regen
        FabricBrewingRecipeRegistry.registerPotionRecipe(
                ModPotions.MANA_REGEN,
                Ingredient.ofItems(Items.REDSTONE),
                ModPotions.LONG_MANA_REGEN
        );

        // Amplify: Mana Regen + Glowstone = Strong Mana Regen
        FabricBrewingRecipeRegistry.registerPotionRecipe(
                ModPotions.MANA_REGEN,
                Ingredient.ofItems(Items.GLOWSTONE_DUST),
                ModPotions.STRONG_MANA_REGEN
        );
    }
}

