package net.willowins.animewitchery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
        private static final List<ItemConvertible> SILVER_SMELTABLES = List.of(ModItems.RAWSILVER,
                        ModBlocks.SILVER_ORE, ModBlocks.DEEPSLATE_SILVER_ORE);

        private static final List<ItemConvertible> LEMON_COOKABLES = List.of(ModItems.UNBAKED_LEMON_TART);

        private static final List<ItemConvertible> STRAWBERRY_COOKABLES = List.of(ModItems.UNBAKED_STRAWBERRY_TART);

        public ModRecipeProvider(FabricDataOutput output) {
                super(output);
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
                offerSmelting(exporter, SILVER_SMELTABLES, RecipeCategory.MISC, ModItems.SILVER,
                                0.7f, 200, "silver");
                offerBlasting(exporter, SILVER_SMELTABLES, RecipeCategory.MISC, ModItems.SILVER,
                                0.7f, 100, "silver");

                offerSmelting(exporter, LEMON_COOKABLES, RecipeCategory.FOOD, ModItems.LEMON_TART,
                                0.7f, 200, "tart");

                offerSmelting(exporter, STRAWBERRY_COOKABLES, RecipeCategory.FOOD, ModItems.STRAWBERRY_TART,
                                0.7f, 200, "tart");

                offerShapelessRecipe(exporter, ModItems.SILVERNUGGET, ModItems.SILVER, "MISC", 9);

                offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItems.SILVER,
                                RecipeCategory.DECORATIONS, ModBlocks.SILVER_BLOCK);

                offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Items.CHARCOAL,
                                RecipeCategory.MISC, ModBlocks.CHARCOAL_BLOCK);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SOUND_BLOCK, 1)
                                .pattern("nnn")
                                .pattern("non")
                                .pattern("nnn")
                                .input('n', Items.NOTE_BLOCK)
                                .input('o', Items.MUSIC_DISC_OTHERSIDE)
                                .criterion(hasItem(Items.NOTE_BLOCK), conditionsFromItem(Items.NOTE_BLOCK))
                                .criterion(hasItem(Items.MUSIC_DISC_OTHERSIDE),
                                                conditionsFromItem(Items.MUSIC_DISC_OTHERSIDE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SOUND_BLOCK)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BLAZE_SACK, 1)
                                .pattern("nnn")
                                .pattern("non")
                                .pattern("nnn")
                                .input('n', Items.BLAZE_POWDER)
                                .input('o', Items.BUNDLE)
                                .criterion(hasItem(Items.BLAZE_POWDER), conditionsFromItem(Items.BLAZE_POWDER))
                                .criterion(hasItem(Items.BUNDLE), conditionsFromItem(Items.BUNDLE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.BLAZE_SACK)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SOUND_BLOCK2, 1)
                                .pattern("nnn")
                                .pattern("non")
                                .pattern("nnn")
                                .input('n', Items.NOTE_BLOCK)
                                .input('o', ModItems.LEMON)
                                .criterion(hasItem(Items.NOTE_BLOCK), conditionsFromItem(Items.NOTE_BLOCK))
                                .criterion(hasItem(ModItems.LEMON), conditionsFromItem(ModItems.LEMON))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SOUND_BLOCK2)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.BUDDING_AMETHYST, 1)
                                .pattern("nnn")
                                .pattern("non")
                                .pattern("nnn")
                                .input('n', Blocks.AMETHYST_BLOCK)
                                .input('o', Items.AMETHYST_SHARD)
                                .criterion(hasItem(Blocks.AMETHYST_BLOCK), conditionsFromItem(Blocks.AMETHYST_BLOCK))
                                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                                .offerTo(exporter, new Identifier(getRecipeName(Blocks.BUDDING_AMETHYST)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SILVERSPOOL, 1)
                                .pattern("nnn")
                                .pattern("non")
                                .pattern("nnn")
                                .input('n', ModItems.SILVERNUGGET)
                                .input('o', ModItems.SPOOL)
                                .criterion(hasItem(ModItems.SPOOL), conditionsFromItem(ModItems.SPOOL))
                                .criterion(hasItem(ModItems.SILVERNUGGET), conditionsFromItem(ModItems.SILVERNUGGET))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SILVERSPOOL)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SPOOL, 9)
                                .pattern("nnn")
                                .pattern(" o ")
                                .pattern("nnn")
                                .input('n', Blocks.SPRUCE_SLAB)
                                .input('o', Items.STICK)
                                .criterion(hasItem(Blocks.SPRUCE_SLAB), conditionsFromItem(Blocks.SPRUCE_SLAB))
                                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SPOOL)));

                ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.QUARTZ, 1)
                                .input(Items.AMETHYST_SHARD)
                                .input(Items.WHITE_DYE)
                                .group("Quartz")
                                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                                .criterion(hasItem(Items.WHITE_DYE), conditionsFromItem(Items.WHITE_DYE))
                                .offerTo(exporter, new Identifier(getRecipeName(Items.QUARTZ)));

                ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.AMETHYST_SHARD, 1)
                                .input(Items.QUARTZ)
                                .input(Items.PURPLE_DYE)
                                .group("Quartz")
                                .criterion(hasItem(Items.QUARTZ), conditionsFromItem(Items.QUARTZ))
                                .criterion(hasItem(Items.PURPLE_DYE), conditionsFromItem(Items.PURPLE_DYE))
                                .offerTo(exporter, new Identifier(getRecipeName(Items.AMETHYST_SHARD)));

                offerShapelessRecipe(exporter, Items.GLOW_INK_SAC, Items.GLOW_BERRIES, "MISC", 1);

                offerShapelessRecipe(exporter, ModBlocks.SILVER_BUTTON, ModItems.SILVERNUGGET, "REDSTONE", 2);

                createPressurePlateRecipe(RecipeCategory.REDSTONE, ModBlocks.SILVER_PRESSURE_PLATE,
                                Ingredient.ofItems(ModItems.SILVER))
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SILVER_PRESSURE_PLATE)));

                createTrapdoorRecipe(ModBlocks.SILVER_TRAPDOOR, Ingredient.ofItems(ModItems.SILVER))
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SILVER_TRAPDOOR)));

                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SILVER_SLAB,
                                Ingredient.ofItems(ModBlocks.SILVER_BLOCK))
                                .criterion(hasItem(ModBlocks.SILVER_BLOCK), conditionsFromItem(ModBlocks.SILVER_BLOCK))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SILVER_SLAB)));

                createDoorRecipe(ModBlocks.SILVER_DOOR, Ingredient.ofItems(ModItems.SILVER))
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SILVER_DOOR)));

                createFenceRecipe(ModBlocks.SILVER_FENCE, Ingredient.ofItems(ModItems.SILVER))
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SILVER_FENCE)));

                createFenceGateRecipe(ModBlocks.SILVER_FENCE_GATE, Ingredient.ofItems(ModItems.SILVER))
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SILVER_FENCE_GATE)));

                offerWallRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SILVER_WALL,
                                ModBlocks.SILVER_BLOCK);

                createStairsRecipe(ModBlocks.SILVER_STAIRS, Ingredient.ofItems(ModBlocks.SILVER_BLOCK))
                                .criterion(hasItem(ModBlocks.SILVER_BLOCK), conditionsFromItem(ModBlocks.SILVER_BLOCK))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.SILVER_STAIRS)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SILVER_PICKAXE, 1)
                                .pattern("nnn")
                                .pattern(" o ")
                                .pattern(" o ")
                                .input('n', ModItems.SILVER)
                                .input('o', Items.STICK)
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SILVER_PICKAXE)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.NEEDLE, 1)
                                .pattern("  n")
                                .pattern("os ")
                                .pattern("oo ")
                                .input('n', ModItems.SILVER)
                                .input('o', ModItems.SILVERNUGGET)
                                .input('s', Items.NETHERITE_SWORD)
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .criterion(hasItem(ModItems.SILVERNUGGET), conditionsFromItem(ModItems.SILVERNUGGET))
                                .criterion(hasItem(Items.NETHERITE_SWORD), conditionsFromItem(Items.NETHERITE_SWORD))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.NEEDLE)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SILVER_HELMET, 1)
                                .pattern("   ")
                                .pattern("ndn")
                                .pattern("n n")
                                .input('n', ModItems.SILVER)
                                .input('d', Items.DIAMOND_HELMET)
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .criterion(hasItem(Items.DIAMOND_HELMET), conditionsFromItem(Items.DIAMOND_HELMET))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SILVER_HELMET)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SILVER_CHESTPLATE, 1)
                                .pattern("n n")
                                .pattern("ndn")
                                .pattern("nnn")
                                .input('n', ModItems.SILVER)
                                .input('d', Items.DIAMOND_CHESTPLATE)
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .criterion(hasItem(Items.DIAMOND_CHESTPLATE),
                                                conditionsFromItem(Items.DIAMOND_CHESTPLATE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SILVER_CHESTPLATE)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SILVER_LEGGINGS, 1)
                                .pattern("ndn")
                                .pattern("n n")
                                .pattern("n n")
                                .input('n', ModItems.SILVER)
                                .input('d', Items.DIAMOND_LEGGINGS)
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .criterion(hasItem(Items.DIAMOND_LEGGINGS), conditionsFromItem(Items.DIAMOND_LEGGINGS))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SILVER_LEGGINGS)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SILVER_BOOTS, 1)
                                .pattern("   ")
                                .pattern("ndn")
                                .pattern("n n")
                                .input('n', ModItems.SILVER)
                                .input('d', Items.DIAMOND_BOOTS)
                                .criterion(hasItem(ModItems.SILVER), conditionsFromItem(ModItems.SILVER))
                                .criterion(hasItem(Items.DIAMOND_BOOTS), conditionsFromItem(Items.DIAMOND_BOOTS))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SILVER_BOOTS)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TART_CRUST, 1)
                                .pattern("nnn")
                                .pattern("non")
                                .pattern("nnn")
                                .input('n', Items.WHEAT)
                                .input('o', Items.WATER_BUCKET)
                                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                                .criterion(hasItem(Items.WATER_BUCKET), conditionsFromItem(Items.WATER_BUCKET))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.TART_CRUST)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.UNBAKED_LEMON_TART, 1)
                                .pattern("nnn")
                                .pattern("non")
                                .pattern("nnn")
                                .input('n', ModItems.LEMON)
                                .input('o', ModItems.TART_CRUST)
                                .criterion(hasItem(ModItems.LEMON), conditionsFromItem(ModItems.LEMON))
                                .criterion(hasItem(ModItems.TART_CRUST), conditionsFromItem(ModItems.TART_CRUST))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.UNBAKED_LEMON_TART)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.UNBAKED_STRAWBERRY_TART, 1)
                                .pattern("nnn")
                                .pattern("non")
                                .pattern("nnn")
                                .input('n', ModItems.STRAWBERRY)
                                .input('o', ModItems.TART_CRUST)
                                .criterion(hasItem(ModItems.STRAWBERRY), conditionsFromItem(ModItems.STRAWBERRY))
                                .criterion(hasItem(ModItems.TART_CRUST), conditionsFromItem(ModItems.TART_CRUST))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.UNBAKED_STRAWBERRY_TART)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.CRYING_OBSIDIAN, 1)
                                .pattern("nnn")
                                .pattern("non")
                                .pattern("nnn")
                                .input('n', Items.AMETHYST_SHARD)
                                .input('o', Blocks.OBSIDIAN)
                                .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                                .criterion(hasItem(Blocks.OBSIDIAN), conditionsFromItem(Blocks.OBSIDIAN))
                                .offerTo(exporter, new Identifier(getRecipeName(Blocks.CRYING_OBSIDIAN)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BUNDLE, 1)
                                .pattern("  o")
                                .pattern(" n ")
                                .input('n', Items.LEATHER)
                                .input('o', Items.STRING)
                                .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                                .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STRING))
                                .offerTo(exporter, new Identifier(getRecipeName(Items.BUNDLE)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.HEALING_STAFF, 1)
                                .pattern("  n")
                                .pattern(" o ")
                                .pattern("o  ")
                                .input('n', ModItems.STAFF_HEAD)
                                .input('o', Items.STICK)
                                .criterion(hasItem(ModItems.STAFF_HEAD), conditionsFromItem(ModItems.STAFF_HEAD))
                                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                                .offerTo(exporter, new Identifier(getRecipeName(ModItems.HEALING_STAFF)));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DECORATIVE_FOUNTAIN.asItem(), 1)
                                .pattern(" s ")
                                .pattern("wsw")
                                .pattern("oco")
                                .input('w', Items.WATER_BUCKET)
                                .input('o', Blocks.STONE_BRICK_SLAB)
                                .input('s', Blocks.STONE_BRICK_WALL)
                                .input('c', Blocks.CHISELED_STONE_BRICKS)
                                .criterion(hasItem(Items.WATER_BUCKET), conditionsFromItem(Items.WATER_BUCKET))
                                .criterion(hasItem(Blocks.STONE_BRICK_SLAB),
                                                conditionsFromItem(Blocks.STONE_BRICK_SLAB))
                                .criterion(hasItem(Blocks.STONE_BRICK_WALL),
                                                conditionsFromItem(Blocks.STONE_BRICK_WALL))
                                .criterion(hasItem(Blocks.CHISELED_STONE_BRICKS),
                                                conditionsFromItem(Blocks.CHISELED_STONE_BRICKS))
                                .offerTo(exporter,
                                                new Identifier(getRecipeName(ModBlocks.DECORATIVE_FOUNTAIN.asItem())));

                // Iron Stairs and Slabs
                createStairsRecipe(ModBlocks.IRON_STAIRS, Ingredient.ofItems(Blocks.IRON_BLOCK))
                                .criterion(hasItem(Blocks.IRON_BLOCK), conditionsFromItem(Blocks.IRON_BLOCK))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.IRON_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.IRON_SLAB,
                                Ingredient.ofItems(Blocks.IRON_BLOCK))
                                .criterion(hasItem(Blocks.IRON_BLOCK), conditionsFromItem(Blocks.IRON_BLOCK))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.IRON_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.IRON_STAIRS,
                                Blocks.IRON_BLOCK);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.IRON_SLAB,
                                Blocks.IRON_BLOCK, 2);

                // Concrete Stairs and Slabs
                // White
                createStairsRecipe(ModBlocks.WHITE_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.WHITE_CONCRETE))
                                .criterion(hasItem(Blocks.WHITE_CONCRETE), conditionsFromItem(Blocks.WHITE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.WHITE_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WHITE_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.WHITE_CONCRETE))
                                .criterion(hasItem(Blocks.WHITE_CONCRETE), conditionsFromItem(Blocks.WHITE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.WHITE_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.WHITE_CONCRETE_STAIRS,
                                Blocks.WHITE_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.WHITE_CONCRETE_SLAB,
                                Blocks.WHITE_CONCRETE, 2);

                // Orange
                createStairsRecipe(ModBlocks.ORANGE_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.ORANGE_CONCRETE))
                                .criterion(hasItem(Blocks.ORANGE_CONCRETE), conditionsFromItem(Blocks.ORANGE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.ORANGE_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ORANGE_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.ORANGE_CONCRETE))
                                .criterion(hasItem(Blocks.ORANGE_CONCRETE), conditionsFromItem(Blocks.ORANGE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.ORANGE_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.ORANGE_CONCRETE_STAIRS,
                                Blocks.ORANGE_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.ORANGE_CONCRETE_SLAB,
                                Blocks.ORANGE_CONCRETE, 2);

                // Magenta
                createStairsRecipe(ModBlocks.MAGENTA_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.MAGENTA_CONCRETE))
                                .criterion(hasItem(Blocks.MAGENTA_CONCRETE),
                                                conditionsFromItem(Blocks.MAGENTA_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MAGENTA_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MAGENTA_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.MAGENTA_CONCRETE))
                                .criterion(hasItem(Blocks.MAGENTA_CONCRETE),
                                                conditionsFromItem(Blocks.MAGENTA_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.MAGENTA_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.MAGENTA_CONCRETE_STAIRS,
                                Blocks.MAGENTA_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.MAGENTA_CONCRETE_SLAB,
                                Blocks.MAGENTA_CONCRETE, 2);

                // Light Blue
                createStairsRecipe(ModBlocks.LIGHT_BLUE_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.LIGHT_BLUE_CONCRETE))
                                .criterion(hasItem(Blocks.LIGHT_BLUE_CONCRETE),
                                                conditionsFromItem(Blocks.LIGHT_BLUE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.LIGHT_BLUE_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGHT_BLUE_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.LIGHT_BLUE_CONCRETE))
                                .criterion(hasItem(Blocks.LIGHT_BLUE_CONCRETE),
                                                conditionsFromItem(Blocks.LIGHT_BLUE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.LIGHT_BLUE_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGHT_BLUE_CONCRETE_STAIRS,
                                Blocks.LIGHT_BLUE_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGHT_BLUE_CONCRETE_SLAB,
                                Blocks.LIGHT_BLUE_CONCRETE, 2);

                // Yellow
                createStairsRecipe(ModBlocks.YELLOW_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.YELLOW_CONCRETE))
                                .criterion(hasItem(Blocks.YELLOW_CONCRETE), conditionsFromItem(Blocks.YELLOW_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.YELLOW_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.YELLOW_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.YELLOW_CONCRETE))
                                .criterion(hasItem(Blocks.YELLOW_CONCRETE), conditionsFromItem(Blocks.YELLOW_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.YELLOW_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.YELLOW_CONCRETE_STAIRS,
                                Blocks.YELLOW_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.YELLOW_CONCRETE_SLAB,
                                Blocks.YELLOW_CONCRETE, 2);

                // Lime
                createStairsRecipe(ModBlocks.LIME_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.LIME_CONCRETE))
                                .criterion(hasItem(Blocks.LIME_CONCRETE), conditionsFromItem(Blocks.LIME_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.LIME_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIME_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.LIME_CONCRETE))
                                .criterion(hasItem(Blocks.LIME_CONCRETE), conditionsFromItem(Blocks.LIME_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.LIME_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIME_CONCRETE_STAIRS,
                                Blocks.LIME_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIME_CONCRETE_SLAB,
                                Blocks.LIME_CONCRETE, 2);

                // Pink
                createStairsRecipe(ModBlocks.PINK_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.PINK_CONCRETE))
                                .criterion(hasItem(Blocks.PINK_CONCRETE), conditionsFromItem(Blocks.PINK_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.PINK_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PINK_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.PINK_CONCRETE))
                                .criterion(hasItem(Blocks.PINK_CONCRETE), conditionsFromItem(Blocks.PINK_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.PINK_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.PINK_CONCRETE_STAIRS,
                                Blocks.PINK_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.PINK_CONCRETE_SLAB,
                                Blocks.PINK_CONCRETE, 2);

                // Gray
                createStairsRecipe(ModBlocks.GRAY_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.GRAY_CONCRETE))
                                .criterion(hasItem(Blocks.GRAY_CONCRETE), conditionsFromItem(Blocks.GRAY_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.GRAY_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.GRAY_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.GRAY_CONCRETE))
                                .criterion(hasItem(Blocks.GRAY_CONCRETE), conditionsFromItem(Blocks.GRAY_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.GRAY_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GRAY_CONCRETE_STAIRS,
                                Blocks.GRAY_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GRAY_CONCRETE_SLAB,
                                Blocks.GRAY_CONCRETE, 2);

                // Light Gray
                createStairsRecipe(ModBlocks.LIGHT_GRAY_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.LIGHT_GRAY_CONCRETE))
                                .criterion(hasItem(Blocks.LIGHT_GRAY_CONCRETE),
                                                conditionsFromItem(Blocks.LIGHT_GRAY_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.LIGHT_GRAY_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGHT_GRAY_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.LIGHT_GRAY_CONCRETE))
                                .criterion(hasItem(Blocks.LIGHT_GRAY_CONCRETE),
                                                conditionsFromItem(Blocks.LIGHT_GRAY_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.LIGHT_GRAY_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGHT_GRAY_CONCRETE_STAIRS,
                                Blocks.LIGHT_GRAY_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.LIGHT_GRAY_CONCRETE_SLAB,
                                Blocks.LIGHT_GRAY_CONCRETE, 2);

                // Cyan
                createStairsRecipe(ModBlocks.CYAN_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.CYAN_CONCRETE))
                                .criterion(hasItem(Blocks.CYAN_CONCRETE), conditionsFromItem(Blocks.CYAN_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.CYAN_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CYAN_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.CYAN_CONCRETE))
                                .criterion(hasItem(Blocks.CYAN_CONCRETE), conditionsFromItem(Blocks.CYAN_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.CYAN_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CYAN_CONCRETE_STAIRS,
                                Blocks.CYAN_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CYAN_CONCRETE_SLAB,
                                Blocks.CYAN_CONCRETE, 2);

                // Purple
                createStairsRecipe(ModBlocks.PURPLE_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.PURPLE_CONCRETE))
                                .criterion(hasItem(Blocks.PURPLE_CONCRETE), conditionsFromItem(Blocks.PURPLE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.PURPLE_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.PURPLE_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.PURPLE_CONCRETE))
                                .criterion(hasItem(Blocks.PURPLE_CONCRETE), conditionsFromItem(Blocks.PURPLE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.PURPLE_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.PURPLE_CONCRETE_STAIRS,
                                Blocks.PURPLE_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.PURPLE_CONCRETE_SLAB,
                                Blocks.PURPLE_CONCRETE, 2);

                // Blue
                createStairsRecipe(ModBlocks.BLUE_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.BLUE_CONCRETE))
                                .criterion(hasItem(Blocks.BLUE_CONCRETE), conditionsFromItem(Blocks.BLUE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLUE_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLUE_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.BLUE_CONCRETE))
                                .criterion(hasItem(Blocks.BLUE_CONCRETE), conditionsFromItem(Blocks.BLUE_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLUE_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLUE_CONCRETE_STAIRS,
                                Blocks.BLUE_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLUE_CONCRETE_SLAB,
                                Blocks.BLUE_CONCRETE, 2);

                // Brown
                createStairsRecipe(ModBlocks.BROWN_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.BROWN_CONCRETE))
                                .criterion(hasItem(Blocks.BROWN_CONCRETE), conditionsFromItem(Blocks.BROWN_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BROWN_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BROWN_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.BROWN_CONCRETE))
                                .criterion(hasItem(Blocks.BROWN_CONCRETE), conditionsFromItem(Blocks.BROWN_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BROWN_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.BROWN_CONCRETE_STAIRS,
                                Blocks.BROWN_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.BROWN_CONCRETE_SLAB,
                                Blocks.BROWN_CONCRETE, 2);

                // Green
                createStairsRecipe(ModBlocks.GREEN_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.GREEN_CONCRETE))
                                .criterion(hasItem(Blocks.GREEN_CONCRETE), conditionsFromItem(Blocks.GREEN_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.GREEN_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.GREEN_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.GREEN_CONCRETE))
                                .criterion(hasItem(Blocks.GREEN_CONCRETE), conditionsFromItem(Blocks.GREEN_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.GREEN_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GREEN_CONCRETE_STAIRS,
                                Blocks.GREEN_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GREEN_CONCRETE_SLAB,
                                Blocks.GREEN_CONCRETE, 2);

                // Red
                createStairsRecipe(ModBlocks.RED_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.RED_CONCRETE))
                                .criterion(hasItem(Blocks.RED_CONCRETE), conditionsFromItem(Blocks.RED_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.RED_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RED_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.RED_CONCRETE))
                                .criterion(hasItem(Blocks.RED_CONCRETE), conditionsFromItem(Blocks.RED_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.RED_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.RED_CONCRETE_STAIRS,
                                Blocks.RED_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.RED_CONCRETE_SLAB,
                                Blocks.RED_CONCRETE, 2);

                // Black
                createStairsRecipe(ModBlocks.BLACK_CONCRETE_STAIRS, Ingredient.ofItems(Blocks.BLACK_CONCRETE))
                                .criterion(hasItem(Blocks.BLACK_CONCRETE), conditionsFromItem(Blocks.BLACK_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLACK_CONCRETE_STAIRS)));
                createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLACK_CONCRETE_SLAB,
                                Ingredient.ofItems(Blocks.BLACK_CONCRETE))
                                .criterion(hasItem(Blocks.BLACK_CONCRETE), conditionsFromItem(Blocks.BLACK_CONCRETE))
                                .offerTo(exporter, new Identifier(getRecipeName(ModBlocks.BLACK_CONCRETE_SLAB)));
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLACK_CONCRETE_STAIRS,
                                Blocks.BLACK_CONCRETE);
                offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.BLACK_CONCRETE_SLAB,
                                Blocks.BLACK_CONCRETE, 2);
        }
}