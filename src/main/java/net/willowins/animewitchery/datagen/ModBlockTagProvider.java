package net.willowins.animewitchery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
        public ModBlockTagProvider(FabricDataOutput output,
                        CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
                super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
                getOrCreateTagBuilder(ModTags.Blocks.METAL_DETECTOR_DETECTABLE_BLOCKS)
                                .add(ModBlocks.SILVER_ORE)
                                .add(ModBlocks.DEEPSLATE_SILVER_ORE)
                                .forceAddTag(BlockTags.GOLD_ORES)
                                .forceAddTag(BlockTags.EMERALD_ORES)
                                .forceAddTag(BlockTags.IRON_ORES)
                                .forceAddTag(BlockTags.DIAMOND_ORES)
                                .forceAddTag(BlockTags.LAPIS_ORES)
                                .forceAddTag(BlockTags.COAL_ORES)
                                .forceAddTag(BlockTags.COPPER_ORES)
                                .forceAddTag(BlockTags.REDSTONE_ORES);

                getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                                .add(ModBlocks.SOUND_BLOCK2)
                                .add(ModBlocks.SOUND_BLOCK);

                getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                                .add(ModBlocks.CHARCOAL_BLOCK)
                                .add(ModBlocks.EFFIGY_FOUNTAIN)
                                .add(ModBlocks.ACTIVE_EFFIGY_FOUNTAIN)
                                .add(ModBlocks.SILVER_BLOCK)
                                .add(ModBlocks.SILVER_ORE)
                                .add(ModBlocks.DEEPSLATE_SILVER_ORE)
                                .add(Blocks.REINFORCED_DEEPSLATE)
                                .add(ModBlocks.SILVER_BUTTON)
                                .add(ModBlocks.SILVER_TRAPDOOR)
                                .add(ModBlocks.SILVER_DOOR)
                                .add(ModBlocks.SILVER_FENCE)
                                .add(ModBlocks.SILVER_FENCE_GATE)
                                .add(ModBlocks.SILVER_WALL)
                                .add(ModBlocks.SILVER_STAIRS)
                                .add(ModBlocks.SILVER_STAIRS)
                                .add(ModBlocks.SILVER_SLAB)
                                .add(ModBlocks.BLOCK_MINER);

                getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                                .add(ModBlocks.SILVER_BLOCK)
                                .add(ModBlocks.SILVER_ORE)
                                .add(ModBlocks.DEEPSLATE_SILVER_ORE)
                                .add(ModBlocks.SILVER_BUTTON)
                                .add(ModBlocks.SILVER_TRAPDOOR)
                                .add(ModBlocks.SILVER_DOOR)
                                .add(ModBlocks.SILVER_FENCE)
                                .add(ModBlocks.SILVER_FENCE_GATE)
                                .add(ModBlocks.SILVER_WALL)
                                .add(ModBlocks.SILVER_STAIRS)
                                .add(ModBlocks.SILVER_SLAB)
                                .add(ModBlocks.BLOCK_MINER);

                getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                                .add(ModBlocks.CHARCOAL_BLOCK);

                getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("fabric", "needs_tool_level_4")));

                getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("fabric", "needs_tool_level_5")))
                                .add(Blocks.REINFORCED_DEEPSLATE)
                                .add(ModBlocks.ACTIVE_EFFIGY_FOUNTAIN)
                                .add(ModBlocks.EFFIGY_FOUNTAIN);

                getOrCreateTagBuilder(BlockTags.FENCES)
                                .add(ModBlocks.SILVER_FENCE);
                getOrCreateTagBuilder(BlockTags.FENCE_GATES)
                                .add(ModBlocks.SILVER_FENCE_GATE);
                getOrCreateTagBuilder(BlockTags.WALLS)
                                .add(ModBlocks.SILVER_WALL);

        }
}
