package net.willowins.animewitchery.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.willowins.animewitchery.AnimeWitchery;

public class AlchemyRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack result;
    private final int xpCost;
    private final int processingTime;

    public AlchemyRecipe(Identifier id, DefaultedList<Ingredient> ingredients, 
                        ItemStack result, int xpCost, int processingTime) {
        this.id = id;
        this.ingredients = ingredients;
        this.result = result;
        this.xpCost = xpCost;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        // Check if we have all required ingredients in the input slots (1-10)
        boolean[] foundIngredients = new boolean[ingredients.size()];
        
        // Check all input slots for ingredients
        for (int i = 1; i <= 10; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                // Check if this stack matches any of our required ingredients
                for (int j = 0; j < ingredients.size(); j++) {
                    if (!foundIngredients[j] && ingredients.get(j).test(stack)) {
                        foundIngredients[j] = true;
                        break; // Found this ingredient, move to next slot
                    }
                }
            }
        }
        
        // Check if all ingredients were found
        for (boolean found : foundIngredients) {
            if (!found) return false;
        }
        
        return true;
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        return result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true; // Alchemy table has its own layout
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return result;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALCHEMY_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALCHEMY_RECIPE_TYPE;
    }

    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    public int getXpCost() {
        return xpCost;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public void consumeIngredients(Inventory inventory) {
        // Consume one of each required ingredient
        for (Ingredient ingredient : ingredients) {
            consumeIngredient(inventory, ingredient);
        }
    }

    private void consumeIngredient(Inventory inventory, Ingredient ingredient) {
        for (int i = 1; i <= 10; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && ingredient.test(stack)) {
                stack.decrement(1);
                break;
            }
        }
    }

    public static class Serializer implements RecipeSerializer<AlchemyRecipe> {
        @Override
        public AlchemyRecipe read(Identifier id, JsonObject json) {
            DefaultedList<Ingredient> ingredients = DefaultedList.of();
            
            // Read ingredients array
            var ingredientsJson = JsonHelper.getArray(json, "ingredients");
            for (var element : ingredientsJson) {
                ingredients.add(Ingredient.fromJson(element));
            }
            
            ItemStack result = net.minecraft.recipe.ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
            int xpCost = JsonHelper.getInt(json, "xp_cost", 0);
            int processingTime = JsonHelper.getInt(json, "processing_time", 200);
            
            return new AlchemyRecipe(id, ingredients, result, xpCost, processingTime);
        }

        @Override
        public AlchemyRecipe read(Identifier id, PacketByteBuf buf) {
            int ingredientCount = buf.readInt();
            DefaultedList<Ingredient> ingredients = DefaultedList.of();
            
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.add(Ingredient.fromPacket(buf));
            }
            
            ItemStack result = buf.readItemStack();
            int xpCost = buf.readInt();
            int processingTime = buf.readInt();
            
            return new AlchemyRecipe(id, ingredients, result, xpCost, processingTime);
        }

        @Override
        public void write(PacketByteBuf buf, AlchemyRecipe recipe) {
            buf.writeInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.write(buf);
            }
            buf.writeItemStack(recipe.result);
            buf.writeInt(recipe.xpCost);
            buf.writeInt(recipe.processingTime);
        }
    }
} 