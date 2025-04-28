package net.willowins.animewitchery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.custom.LemonCropBlock;
import net.willowins.animewitchery.block.custom.StrawberryCropBlock;
import net.willowins.animewitchery.item.ModItems;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.SOUND_BLOCK2);
        addDrop(ModBlocks.SOUND_BLOCK);
        addDrop(ModBlocks.SILVER_BLOCK);
        addDrop(ModBlocks.CHARCOAL_BLOCK);

        addDrop(ModBlocks.SILVER_ORE, copperlikeOreDrops(ModBlocks.SILVER_ORE, ModItems.RAWSILVER));
        addDrop(ModBlocks.DEEPSLATE_SILVER_ORE, copperlikeOreDrops(ModBlocks.DEEPSLATE_SILVER_ORE, ModItems.RAWSILVER));

        addDrop(ModBlocks.SILVER_FENCE);
        addDrop(ModBlocks.SILVER_FENCE_GATE);
        addDrop(ModBlocks.SILVER_WALL);
        addDrop(ModBlocks.SILVER_TRAPDOOR);
        addDrop(ModBlocks.SILVER_BUTTON);
        addDrop(ModBlocks.SILVER_PRESSURE_PLATE);
        addDrop(ModBlocks.SILVER_STAIRS);
        addDrop(Blocks.REINFORCED_DEEPSLATE);

        addDrop(ModBlocks.SILVER_DOOR, doorDrops(ModBlocks.SILVER_DOOR));
        addDrop(ModBlocks.SILVER_SLAB, slabDrops(ModBlocks.SILVER_SLAB));

        BlockStatePropertyLootCondition.Builder builder = BlockStatePropertyLootCondition.builder(ModBlocks.STRAWBERRY_CROP).properties(StatePredicate.Builder.create()
                .exactMatch(StrawberryCropBlock.AGE, 3));
        addDrop(ModBlocks.STRAWBERRY_CROP, cropDrops(ModBlocks.STRAWBERRY_CROP, ModItems.STRAWBERRY, ModItems.STRAWBERRY_SEEDS, builder));

        AnyOfLootCondition.Builder builder2 =
                BlockStatePropertyLootCondition.builder(ModBlocks.LEMON_CROP).properties(StatePredicate.Builder.create()
                                .exactMatch(LemonCropBlock.AGE, 7))
                        .or(BlockStatePropertyLootCondition.builder(ModBlocks.LEMON_CROP).properties(StatePredicate.Builder.create()
                                .exactMatch(LemonCropBlock.AGE, 8)));
        addDrop(ModBlocks.LEMON_CROP, cropDrops(ModBlocks.LEMON_CROP, ModItems.LEMON, ModItems.LEMON_SEEDS, builder2));
    }

    public LootTable.Builder copperlikeOreDrops(Block drop, Item item) {
        return dropsWithSilkTouch(
                drop,
                (LootPoolEntry.Builder<?>) this.applyExplosionDecay(
                        drop,
                        ItemEntry.builder(item)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 4.0F)))
                                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
                )
        );
    }
}