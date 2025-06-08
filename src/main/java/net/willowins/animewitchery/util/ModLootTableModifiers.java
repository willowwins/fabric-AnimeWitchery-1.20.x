package net.willowins.animewitchery.util;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.item.ModItems;

public class ModLootTableModifiers {
private static final Identifier NETHER_FORTRESS_ID =
        new Identifier("minecraft","chests/nether_bridge");
    private static final Identifier GL_BAT_ID =
            new Identifier("geckolib","entities/bat");
    private static final Identifier CREEPER_ID =
            new Identifier("minecraft","entities/creeper");
    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, identifier, tableBuilder, lootTableSource) -> {
            if (NETHER_FORTRESS_ID.equals(identifier)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.45f)) // Drops 45% of the time
                        .with(ItemEntry.builder(Items.WITHER_SKELETON_SKULL))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }
            if (GL_BAT_ID.equals(identifier)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.4f)) // Drops 40% of the time
                        .with(ItemEntry.builder(Items.PHANTOM_MEMBRANE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }

            if (CREEPER_ID.equals(identifier)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.01f)) // Drops 1% of the time
                        .with(ItemEntry.builder(ModItems.LEMON_SEEDS))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());

                tableBuilder.pool(poolBuilder.build());

            }
            if (CREEPER_ID.equals(identifier)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.01f)) // Drops 1% of the time
                        .with(ItemEntry.builder(ModItems.STRAWBERRY_SEEDS))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}

