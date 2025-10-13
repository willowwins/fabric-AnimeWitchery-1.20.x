package net.willowins.animewitchery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.custom.LemonCropBlock;
import net.willowins.animewitchery.block.custom.StrawberryCropBlock;
import net.willowins.animewitchery.item.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // Silver building blocks
        BlockStateModelGenerator.BlockTexturePool silverPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.SILVER_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SILVER_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CHARCOAL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_SILVER_ORE);

        silverPool.stairs(ModBlocks.SILVER_STAIRS);
        silverPool.slab(ModBlocks.SILVER_SLAB);
        silverPool.button(ModBlocks.SILVER_BUTTON);
        silverPool.pressurePlate(ModBlocks.SILVER_PRESSURE_PLATE);
        silverPool.fence(ModBlocks.SILVER_FENCE);
        silverPool.fenceGate(ModBlocks.SILVER_FENCE_GATE);
        silverPool.wall(ModBlocks.SILVER_WALL);

        blockStateModelGenerator.registerDoor(ModBlocks.SILVER_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.SILVER_TRAPDOOR);

        // Sound blocks
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SOUND_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SOUND_BLOCK2);

        // Functional blocks - use registerSimpleState for custom models
        blockStateModelGenerator.registerSimpleState(ModBlocks.AUTO_CRAFTER_BLOCK);
        blockStateModelGenerator.registerSimpleState(ModBlocks.BLOCK_MINER);
        blockStateModelGenerator.registerSimpleState(ModBlocks.GROWTH_ACCELERATOR);
        blockStateModelGenerator.registerSimpleState(ModBlocks.BLOCK_PLACER);
        blockStateModelGenerator.registerSimpleState(ModBlocks.INTERACTOR);
        blockStateModelGenerator.registerSimpleState(ModBlocks.PARTICLE_BLOCK);
        blockStateModelGenerator.registerSimpleState(ModBlocks.PARTICLE_SINK_BLOCK);
        blockStateModelGenerator.registerSimpleState(ModBlocks.PLATE_BLOCK);
        
        // Alchemy and ritual blocks
        blockStateModelGenerator.registerSimpleState(ModBlocks.ALCHEMY_TABLE);
        blockStateModelGenerator.registerSimpleState(ModBlocks.EFFIGY_FOUNTAIN);
        blockStateModelGenerator.registerSimpleState(ModBlocks.DECORATIVE_FOUNTAIN);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ACTIVE_EFFIGY_FOUNTAIN);
        blockStateModelGenerator.registerSimpleState(ModBlocks.BINDING_SPELL);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ACTIVE_BINDING_SPELL);
        
        // Obelisk blocks
        blockStateModelGenerator.registerSimpleState(ModBlocks.OBELISK);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ACTIVE_OBELISK);
        blockStateModelGenerator.registerSimpleState(ModBlocks.BOSS_OBELISK);
        blockStateModelGenerator.registerSimpleState(ModBlocks.PILLAR);
        
        // Barrier blocks
        blockStateModelGenerator.registerSimpleState(ModBlocks.BARRIER_CIRCLE);
        blockStateModelGenerator.registerSimpleState(ModBlocks.BARRIER_DISTANCE_GLYPH);
        
        // Statue blocks
        blockStateModelGenerator.registerSimpleState(ModBlocks.GUARDIAN_STATUE);
        
        // Special blocks
        blockStateModelGenerator.registerSimpleState(ModBlocks.GRAND_SHULKER_BOX);
        blockStateModelGenerator.registerSimpleState(ModBlocks.SPELL_TRIGGER_BLOCK);

        // Crops
        blockStateModelGenerator.registerCrop(ModBlocks.STRAWBERRY_CROP, StrawberryCropBlock.AGE, 0, 1, 2, 3);
        blockStateModelGenerator.registerCrop(ModBlocks.LEMON_CROP, LemonCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // Food items
        itemModelGenerator.register(ModItems.LEMON, Models.GENERATED);
        itemModelGenerator.register(ModItems.STRAWBERRY, Models.GENERATED);
        itemModelGenerator.register(ModItems.TART_CRUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNBAKED_LEMON_TART, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNBAKED_STRAWBERRY_TART, Models.GENERATED);
        itemModelGenerator.register(ModItems.STRAWBERRY_TART, Models.GENERATED);
        itemModelGenerator.register(ModItems.LEMON_TART, Models.GENERATED);
        itemModelGenerator.register(ModItems.STRAWBERRY_SEEDS, Models.GENERATED);
        itemModelGenerator.register(ModItems.LEMON_SEEDS, Models.GENERATED);
        
        // Basic materials
        itemModelGenerator.register(ModItems.SILVER, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVERNUGGET, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAWSILVER, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPOOL, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVERSPOOL, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVER_TEMPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.STAFF_HEAD, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLAZE_SACK, Models.GENERATED);
        itemModelGenerator.register(ModItems.VOID_ESSENCE, Models.GENERATED);
        itemModelGenerator.register(ModItems.RUNE_STONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLOOD_RUNE_STONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.OBELISK_SHARD, Models.GENERATED);
        
        // Alchemy and magic items
        itemModelGenerator.register(ModItems.MORTAR_AND_PESTLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHALK, Models.GENERATED);
        itemModelGenerator.register(ModItems.MAGIC_CHALK, Models.GENERATED);
        itemModelGenerator.register(ModItems.ENCHANTED_CRYSTAL, Models.GENERATED);
        
        // Dust items
        itemModelGenerator.register(ModItems.BONE_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.AMETHYST_DUST, Models.GENERATED);
        
        // Rune stones
        itemModelGenerator.register(ModItems.FIRE_RUNE_STONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.WATER_RUNE_STONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.EARTH_RUNE_STONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.AIR_RUNE_STONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIFE_RUNE_STONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEATH_RUNE_STONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIGHT_RUNE_STONE, Models.GENERATED);
        itemModelGenerator.register(ModItems.DARKNESS_RUNE_STONE, Models.GENERATED);
        
        // Barrier items
        itemModelGenerator.register(ModItems.BARRIER_CATALYST, Models.GENERATED);
        itemModelGenerator.register(ModItems.REPAIR_ESSENCE, Models.GENERATED);
        
        // Spell scrolls
        itemModelGenerator.register(ModItems.FIRE_SPELL_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.WATER_SPELL_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.EARTH_SPELL_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.AIR_SPELL_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIFE_SPELL_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEATH_SPELL_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.LIGHT_SPELL_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.DARKNESS_SPELL_SCROLL, Models.GENERATED);
        
        // Special items and catalysts
        itemModelGenerator.register(ModItems.ALCHEMICAL_CATALYST, Models.GENERATED);
        itemModelGenerator.register(ModItems.RESONANT_CATALYST, Models.GENERATED);
        itemModelGenerator.register(ModItems.OBELISK_COMPASS, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVER_PENDANT, Models.GENERATED);
        itemModelGenerator.register(ModItems.FIRE_RES_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPEED_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.REPAIR_CHARM, Models.GENERATED);
        
        // Special functional items
        itemModelGenerator.register(ModItems.METAL_DETECTOR, Models.GENERATED);
        itemModelGenerator.register(ModItems.WEATHERITEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.MOD_TOTEM, Models.GENERATED);
        itemModelGenerator.register(ModItems.DIMENSION_HOPPER, Models.GENERATED);
        itemModelGenerator.register(ModItems.RESPAWN_BEACON, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEFIANT_RIFT, Models.GENERATED);
        itemModelGenerator.register(ModItems.JOB_APPLICATION, Models.GENERATED);
        itemModelGenerator.register(ModItems.NBT_TOOL, Models.GENERATED);
        itemModelGenerator.register(ModItems.KAMIKAZE_RITUAL_SCROLL, Models.GENERATED);
        itemModelGenerator.register(ModItems.OVERHEATED_FUEL_ROD, Models.GENERATED);
        itemModelGenerator.register(ModItems.FUEL_ROD, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEEP_DARK_DEEP_DISH, Models.GENERATED);
        itemModelGenerator.register(ModItems.MANA_ROCKET, Models.GENERATED);
        
        // Books and scrolls
        itemModelGenerator.register(ModItems.RITUALS_BOOK, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPELLBOOK, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPELLBOOK_PAGE, Models.GENERATED);
        
        // Spawn eggs
        itemModelGenerator.register(ModItems.VOID_WISP_SPAWN_EGG, Models.GENERATED);
        
        // Silver tools (HANDHELD model for tools)
        itemModelGenerator.register(ModItems.SILVER_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.SILVER_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.SILVER_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.SILVER_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.SILVER_HOE, Models.HANDHELD);
        
        // Resonant tools
        itemModelGenerator.register(ModItems.RESONANT_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RESONANT_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RESONANT_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RESONANT_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RESONANT_HOE, Models.HANDHELD);
        
        // Special weapons and tools
        itemModelGenerator.register(ModItems.NEEDLE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.OBELISK_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.CHISEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RAILGUN, Models.HANDHELD);
        itemModelGenerator.register(ModItems.KINETIC_BLADE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.HEALING_STAFF, Models.HANDHELD);
        itemModelGenerator.register(ModItems.WAND, Models.HANDHELD);

        // Silver armor set
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SILVER_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SILVER_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SILVER_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SILVER_BOOTS));
        
        // Railgunner armor set
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RAILGUNNER_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RAILGUNNER_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RAILGUNNER_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RAILGUNNER_BOOTS));
        
        // Obelisk armor set
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.OBELISK_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.OBELISK_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.OBELISK_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.OBELISK_BOOTS));
        
        // Resonant armor set
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RESONANT_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RESONANT_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RESONANT_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RESONANT_BOOTS));
    }
}
