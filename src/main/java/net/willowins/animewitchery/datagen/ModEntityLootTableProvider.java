package net.willowins.animewitchery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.entity.ModEntities;
import net.willowins.animewitchery.item.ModItems;

import java.util.function.BiConsumer;

public class ModEntityLootTableProvider extends SimpleFabricLootTableProvider {
        public ModEntityLootTableProvider(FabricDataOutput dataOutput) {
                super(dataOutput, LootContextTypes.ENTITY);
        }

        @Override
        public void accept(BiConsumer<Identifier, LootTable.Builder> identifierLootTableBuilderBiConsumer) {
                // Void Wisp loot table - use the entity's ID directly
                identifierLootTableBuilderBiConsumer.accept(
                                new Identifier("animewitchery", "entities/void_wisp"),
                                LootTable.builder()
                                                .pool(LootPool.builder()
                                                                .rolls(UniformLootNumberProvider.create(1.0f, 1.0f))
                                                                .with(ItemEntry.builder(ModItems.VOID_ESSENCE)
                                                                                .apply(SetCountLootFunction
                                                                                                .builder(UniformLootNumberProvider
                                                                                                                .create(1.0f, 3.0f))))));

        }
}
