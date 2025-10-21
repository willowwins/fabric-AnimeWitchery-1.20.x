package net.willowins.animewitchery.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.armor.RailGunnerArmorItem;
import net.willowins.animewitchery.item.armor.ObeliskArmorItem;
import net.willowins.animewitchery.item.armor.ResonantArmorItem;
import net.willowins.animewitchery.item.custom.*;

public class ModItems {
    public static final Item SILVER =registerItem("silveringot", new Item(new FabricItemSettings()));
    public static final Item SILVER_TEMPLATE =registerItem("silver_template", new Item(new FabricItemSettings()));
    public static final Item STAFF_HEAD =registerItem("staff_head", new Item(new FabricItemSettings()));
    public static final Item BLAZE_SACK =registerItem("blaze_powder_bag", new Item(new FabricItemSettings()));
    public static final Item VOID_ESSENCE =registerItem("void_essence", new Item(new FabricItemSettings().rarity(Rarity.RARE)));
    public static final Item RUNE_STONE =registerItem("rune_stone", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item BLOOD_RUNE_STONE =registerItem("blood_rune_stone", new Item(new FabricItemSettings().rarity(Rarity.RARE)));
    public static final Item KAMIKAZE_RITUAL_SCROLL =registerItem("kamikaze_ritual_scroll", new KamikazeRitualScroll(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item SILVERNUGGET =registerItem("silvernugget", new Item(new FabricItemSettings()));
        public static final Item RAWSILVER =registerItem("rawsilver", new Item(new FabricItemSettings()));
    public static final Item SPOOL =registerItem("spool", new Item(new FabricItemSettings()));
    public static final Item SILVERSPOOL =registerItem("silverspool", new Item(new FabricItemSettings()));
    public static final Item WEATHERITEM =registerItem("weatheritem", new WeatherItem(new FabricItemSettings().maxCount(1)));
    public static final Item MOD_TOTEM =registerItem("mod_totem", new ModTotemItem(new FabricItemSettings().maxCount(1)));
    public static final Item DIMENSION_HOPPER =registerItem("dimension",new DimensionTeleportItem(new FabricItemSettings().maxCount(1)));
    public static final Item RESPAWN_BEACON =registerItem("respawn_beacon",new RespawnTeleportItem(new FabricItemSettings().maxCount(1)));
    public static final Item DEFIANT_RIFT =registerItem("defiant_rift",new BanHammerItem(new FabricItemSettings().maxCount(1)));
    public static final Item JOB_APPLICATION =registerItem("job_application",new UnbanWandItem(new FabricItemSettings().maxCount(1)));
    public static final Item NBT_TOOL = registerItem("nbt_preserving_tool",
            new NBTPreservingToolItem(new FabricItemSettings().maxCount(1)));

    public static final Item METAL_DETECTOR =registerItem("metal_detector",
            new MetalDetectorItem(new FabricItemSettings().maxDamage(300)));

    public static final Item ALCHEMICAL_CATALYST =registerItem("alchemical_catalyst",
            new AlchemicalCatalystItem(new FabricItemSettings().maxCount(1)));

  public static final Item RESONANT_CATALYST =registerItem("resonant_catalyst",
            new ResonantlCatalystItem(new FabricItemSettings().maxCount(1)));

public static final Item OBELISK_COMPASS =registerItem("obelisk_compass",
            new ObeliskCompassItem(new FabricItemSettings().maxCount(1)));

    public static final Item NEEDLE =registerItem("needle",
            new NeedleItem(ModToolMaterial.SILVER,4,-2.2f,new FabricItemSettings().maxCount(1)));

    public static final Item RESONANT_GREATSWORD =registerItem("resonant_greatword",
            new ResonantGreatSwordItem(ModToolMaterial.RESONANT,7,-2.5f,new FabricItemSettings().maxCount(1)));

    public static final Item SILVER_SWORD =registerItem("silver_sword",
            new SwordItem(ModToolMaterial.SILVER,3, -2.4f,new FabricItemSettings()));

    public static final Item SILVER_PICKAXE =registerItem("silver_pickaxe",
            new PickaxeItem(ModToolMaterial.SILVER,0, -3f,new FabricItemSettings()));

    public static final Item SILVER_AXE =registerItem("silver_axe",
            new AxeItem(ModToolMaterial.SILVER,6, -3.1f,new FabricItemSettings()));

    public static final Item SILVER_SHOVEL =registerItem("silver_shovel",
            new ShovelItem(ModToolMaterial.SILVER,1.5f, -3f,new FabricItemSettings()));

    public static final Item SILVER_HOE =registerItem("silver_hoe",
            new HoeItem(ModToolMaterial.SILVER,-2, 0f,new FabricItemSettings()));

    public static final Item SILVER_HELMET =registerItem("silver_helmet",
            new ModArmorItem(ModArmorMaterials.SILVER,ArmorItem.Type.HELMET,new FabricItemSettings()));
    public static final Item SILVER_CHESTPLATE =registerItem("silver_chestplate",
            new ArmorItem(ModArmorMaterials.SILVER,ArmorItem.Type.CHESTPLATE,new FabricItemSettings()));
    public static final Item SILVER_LEGGINGS =registerItem("silver_leggings",
            new ArmorItem(ModArmorMaterials.SILVER,ArmorItem.Type.LEGGINGS,new FabricItemSettings()));
    public static final Item SILVER_BOOTS =registerItem("silver_boots",
            new ArmorItem(ModArmorMaterials.SILVER,ArmorItem.Type.BOOTS,new FabricItemSettings()));

    public static final Item RAILGUNNER_HELMET =registerItem("railgunner_helmet",
            new RailGunnerArmorItem(ModArmorMaterials.RAILGUNNER,ArmorItem.Type.HELMET,new FabricItemSettings()));
    public static final Item RAILGUNNER_CHESTPLATE =registerItem("railgunner_chestplate",
            new RailGunnerArmorItem(ModArmorMaterials.RAILGUNNER,ArmorItem.Type.CHESTPLATE,new FabricItemSettings()));
    public static final Item RAILGUNNER_LEGGINGS =registerItem("railgunner_leggings",
            new RailGunnerArmorItem(ModArmorMaterials.RAILGUNNER,ArmorItem.Type.LEGGINGS,new FabricItemSettings()));
    public static final Item RAILGUNNER_BOOTS =registerItem("railgunner_boots",
            new RailGunnerArmorItem(ModArmorMaterials.RAILGUNNER,ArmorItem.Type.BOOTS,new FabricItemSettings()));

    // Obelisk Armor Set
    public static final Item OBELISK_HELMET =registerItem("obelisk_helmet",
            new ObeliskArmorItem(ModArmorMaterials.OBELISK,ArmorItem.Type.HELMET,new FabricItemSettings()));
    public static final Item OBELISK_CHESTPLATE =registerItem("obelisk_chestplate",
            new ObeliskArmorItem(ModArmorMaterials.OBELISK,ArmorItem.Type.CHESTPLATE,new FabricItemSettings()));
    public static final Item OBELISK_LEGGINGS =registerItem("obelisk_leggings",
            new ObeliskArmorItem(ModArmorMaterials.OBELISK,ArmorItem.Type.LEGGINGS,new FabricItemSettings()));
    public static final Item OBELISK_BOOTS =registerItem("obelisk_boots",
            new ObeliskArmorItem(ModArmorMaterials.OBELISK,ArmorItem.Type.BOOTS,new FabricItemSettings()));

    //Resonant Armor Set
 public static final Item RESONANT_HELMET =registerItem("resonant_helmet",
            new ResonantArmorItem(ModArmorMaterials.RESONANT,ArmorItem.Type.HELMET,new FabricItemSettings()));
    public static final Item RESONANT_CHESTPLATE =registerItem("resonant_chestplate",
            new ResonantArmorItem(ModArmorMaterials.RESONANT,ArmorItem.Type.CHESTPLATE,new FabricItemSettings()));
    public static final Item RESONANT_LEGGINGS =registerItem("resonant_leggings",
            new ResonantArmorItem(ModArmorMaterials.RESONANT,ArmorItem.Type.LEGGINGS,new FabricItemSettings()));
    public static final Item RESONANT_BOOTS =registerItem("resonant_boots",
            new ResonantArmorItem(ModArmorMaterials.RESONANT,ArmorItem.Type.BOOTS,new FabricItemSettings()));

    public static final Item RESONANT_SWORD =registerItem("resonant_sword",
            new SwordItem(ModToolMaterial.RESONANT,3, -2.4f,new FabricItemSettings()));

    public static final Item RESONANT_PICKAXE =registerItem("resonant_pickaxe",
            new PickaxeItem(ModToolMaterial.RESONANT,1, -2.8f,new FabricItemSettings()));

    public static final Item RESONANT_AXE =registerItem("resonant_axe",
            new AxeItem(ModToolMaterial.RESONANT,6, -3.1f,new FabricItemSettings()));

    public static final Item RESONANT_SHOVEL =registerItem("resonant_shovel",
            new ShovelItem(ModToolMaterial.RESONANT,1.5f, -3f,new FabricItemSettings()));

    public static final Item RESONANT_HOE =registerItem("resonant_hoe",
            new HoeItem(ModToolMaterial.RESONANT,-2, 0f,new FabricItemSettings()));

    public static final Item LEMON =registerItem("lemon", new Item(new FabricItemSettings().food(ModFoodComponents.LEMON)));

    public static final Item FIRE_RES_CRYSTAL =registerItem("fire_res_crystal",
            new FireResCrystalItem(new FabricItemSettings()));

 public static final Item SPEED_CRYSTAL =registerItem("speed_crystal",
            new SpeedCrystalItem(new FabricItemSettings()));

 public static final Item REPAIR_CHARM =registerItem("creeping_prevailance",
            new RepairCharmItem(new FabricItemSettings().maxCount(1)));

public static final Item FULFILLING_TIARA =registerItem("fulfilling_tiara",
            new FulfillingTiaraItem(new FabricItemSettings().maxCount(1)));

public static final Item RESONANT_TIARA =registerItem("resonant_tiara",
            new ResonantTiaraItem(new FabricItemSettings().maxCount(1)));

public static final Item LATENT_MANA_PENDANT =registerItem("latent_mana_pendant",
            new LatentManaPendantItem(new FabricItemSettings().maxCount(1)));

public static final Item MANA_ROCKET =registerItem("mana_rocket",
            new ManaRocketItem(new FabricItemSettings().maxCount(1)));

public static final Item KINETIC_BLADE =registerItem("kinetic_breaker",
            new KineticBladeItem(new FabricItemSettings().maxCount(1)));

    public static final Item OVERHEATED_FUEL_ROD =registerItem("overheated_fuel_rod",
            new OverheatedFuelRodItem(new FabricItemSettings().maxCount(1)));

    public static final Item FUEL_ROD =registerItem("fuel_rod",
            new FuelRodItem(new FabricItemSettings().maxCount(1)));

    public static final Item DEEP_DARK_DEEP_DISH =registerItem("deep_dark_deep_dish",
            new DeepDarkDeepDishItem(new FabricItemSettings().maxCount(1)));

    public static final Item STRAWBERRY =registerItem("strawberry", new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY)));

    public static final Item TART_CRUST =registerItem("tart_crust", new Item(new FabricItemSettings().food(ModFoodComponents.TART_CRUST)));

    public static final Item UNBAKED_LEMON_TART =registerItem("unbaked_lemon_tart", new Item(new FabricItemSettings().food(ModFoodComponents.UNBAKED_TART)));

    public static final Item UNBAKED_STRAWBERRY_TART =registerItem("unbaked_strawberry_tart", new Item(new FabricItemSettings().food(ModFoodComponents.UNBAKED_TART)));

    public static final Item LEMON_TART =registerItem("lemon_tart", new Item(new FabricItemSettings().food(ModFoodComponents.LEMON_TART)));

    public static final Item STRAWBERRY_TART =registerItem("strawberry_tart", new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY_TART)));

    public static final Item STRAWBERRY_SEEDS = registerItem("strawberry_seeds",
    new AliasedBlockItem(ModBlocks.STRAWBERRY_CROP, new FabricItemSettings()));

    public static final Item LEMON_SEEDS = registerItem("lemon_seeds",
            new AliasedBlockItem(ModBlocks.LEMON_CROP, new FabricItemSettings()));

    public static final Item SILVER_PENDANT =registerItem("silver_pendant", new SilverPendant(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item HEALING_STAFF =registerItem("healing_staff", new HealingStaff(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item RAILGUN =registerItem("railgun", new RailgunItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item OBELISK_SWORD = registerItem("obelisk_sword", new ObeliskSwordItem(ModToolMaterial.OBELISK, 7, -1.4f, new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

    public static final Item CHISEL = registerItem("chisel", new ChiselItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)));

    public static final Item OBELISK_SHARD = registerItem("obelisk_shard", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));

    // New alchemy and magic items
    public static final Item MORTAR_AND_PESTLE = registerItem("mortar_and_pestle", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item CHALK = registerItem("chalk", new NormalChalkItem(new FabricItemSettings().maxCount(1)));
    public static final Item MAGIC_CHALK = registerItem("magic_chalk", new MagicChalkItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)));
    public static final Item ENCHANTED_CRYSTAL = registerItem("enchanted_crystal", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    
    // Dust items
    public static final Item BONE_DUST = registerItem("bone_dust", new Item(new FabricItemSettings()));
    public static final Item AMETHYST_DUST = registerItem("amethyst_dust", new Item(new FabricItemSettings()));
    
    // Rune Stones
    public static final Item FIRE_RUNE_STONE = registerItem("fire_rune_stone", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item WATER_RUNE_STONE = registerItem("water_rune_stone", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item EARTH_RUNE_STONE = registerItem("earth_rune_stone", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item AIR_RUNE_STONE = registerItem("air_rune_stone", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item LIFE_RUNE_STONE = registerItem("life_rune_stone", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item DEATH_RUNE_STONE = registerItem("death_rune_stone", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item LIGHT_RUNE_STONE = registerItem("light_rune_stone", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    public static final Item DARKNESS_RUNE_STONE = registerItem("darkness_rune_stone", new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
    
    // Barrier items
    public static final Item BARRIER_CATALYST = registerItem("barrier_catalyst", new Item(new FabricItemSettings().rarity(Rarity.RARE)));
    public static final Item REPAIR_ESSENCE = registerItem("repair_essence", new RepairEssenceItem(new FabricItemSettings().rarity(Rarity.RARE)));

    // Entity spawn eggs
    public static final Item VOID_WISP_SPAWN_EGG = registerItem("void_wisp_spawn_egg", 
            new VoidWispSpawnEggItem(new FabricItemSettings().maxCount(64)));

    // Patchouli book
    public static final Item RITUALS_BOOK = registerItem("rituals_book", 
            new net.willowins.animewitchery.item.custom.PatchouliBookItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "animewitchery:rituals"));

    // Magic wand
    public static final Item WAND = registerItem("wand", 
            new net.willowins.animewitchery.item.custom.WandItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

    // Spellbook
    public static final Item SPELLBOOK = registerItem("spellbook",
            new net.willowins.animewitchery.item.custom.SpellbookItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    
    // Spellbook Page
    public static final Item SPELLBOOK_PAGE = registerItem("spellbook_page",
            new net.willowins.animewitchery.item.custom.SpellbookPageItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

    // Spell Scrolls
    public static final Item FIRE_SPELL_SCROLL = registerItem("fire_spell_scroll",
            new net.willowins.animewitchery.item.custom.SpellScrollItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Fire Blast"));
    public static final Item WATER_SPELL_SCROLL = registerItem("water_spell_scroll",
            new net.willowins.animewitchery.item.custom.SpellScrollItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Water Shield"));
    public static final Item EARTH_SPELL_SCROLL = registerItem("earth_spell_scroll",
            new net.willowins.animewitchery.item.custom.SpellScrollItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Earth Spike"));
    public static final Item AIR_SPELL_SCROLL = registerItem("air_spell_scroll",
            new net.willowins.animewitchery.item.custom.SpellScrollItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Wind Gust"));
    public static final Item LIFE_SPELL_SCROLL = registerItem("life_spell_scroll",
            new net.willowins.animewitchery.item.custom.SpellScrollItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Healing Wave"));
    public static final Item DEATH_SPELL_SCROLL = registerItem("death_spell_scroll",
            new net.willowins.animewitchery.item.custom.SpellScrollItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Wither Touch"));
    public static final Item LIGHT_SPELL_SCROLL = registerItem("light_spell_scroll",
            new net.willowins.animewitchery.item.custom.SpellScrollItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Light Burst"));
    public static final Item DARKNESS_SPELL_SCROLL = registerItem("darkness_spell_scroll",
            new net.willowins.animewitchery.item.custom.SpellScrollItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Shadow Bind"));


    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries){
        // Basic materials
        entries.add(SILVER);
        entries.add(SILVERNUGGET);
        entries.add(RAWSILVER);
        entries.add(SPOOL);
        entries.add(SILVERSPOOL);
        entries.add(SILVER_TEMPLATE);
        entries.add(STAFF_HEAD);
        entries.add(BLAZE_SACK);
        entries.add(VOID_ESSENCE);
        entries.add(RUNE_STONE);
        entries.add(BLOOD_RUNE_STONE);
        entries.add(OBELISK_SHARD);
        
        // Alchemy and magic items
        entries.add(MORTAR_AND_PESTLE);
        entries.add(CHALK);
        entries.add(MAGIC_CHALK);
        entries.add(ENCHANTED_CRYSTAL);
        
        // Dust items
        entries.add(BONE_DUST);
        entries.add(AMETHYST_DUST);
        
        // Rune stones
        entries.add(FIRE_RUNE_STONE);
        entries.add(WATER_RUNE_STONE);
        entries.add(EARTH_RUNE_STONE);
        entries.add(AIR_RUNE_STONE);
        entries.add(LIFE_RUNE_STONE);
        entries.add(DEATH_RUNE_STONE);
        entries.add(LIGHT_RUNE_STONE);
        entries.add(DARKNESS_RUNE_STONE);
        
        // Barrier items
        entries.add(BARRIER_CATALYST);
        entries.add(REPAIR_ESSENCE);
        
        // Food items
        entries.add(LEMON);
        entries.add(STRAWBERRY);
        entries.add(TART_CRUST);
        entries.add(UNBAKED_LEMON_TART);
        entries.add(UNBAKED_STRAWBERRY_TART);
        entries.add(LEMON_TART);
        entries.add(STRAWBERRY_TART);
        entries.add(STRAWBERRY_SEEDS);
        entries.add(LEMON_SEEDS);
        
        // Spawn eggs
        entries.add(VOID_WISP_SPAWN_EGG);
        
        // Spell Scrolls
        entries.add(FIRE_SPELL_SCROLL);
        entries.add(WATER_SPELL_SCROLL);
        entries.add(EARTH_SPELL_SCROLL);
        entries.add(AIR_SPELL_SCROLL);
        entries.add(LIFE_SPELL_SCROLL);
        entries.add(DEATH_SPELL_SCROLL);
        entries.add(LIGHT_SPELL_SCROLL);
        entries.add(DARKNESS_SPELL_SCROLL);
        
        // Books (hidden from JEI)
        // entries.add(RITUALS_BOOK);
    }


    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(AnimeWitchery.MOD_ID, name ), item);

    }

    public static void registerModItems( ){
        AnimeWitchery.LOGGER.info("registering Mod Items for"+AnimeWitchery.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemsToCombatItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ModItems::addItemsToFunctionalItemGroup);
    }
    
    private static void addItemsToCombatItemGroup(FabricItemGroupEntries entries) {
        // Silver Armor Set
        entries.add(SILVER_HELMET);
        entries.add(SILVER_CHESTPLATE);
        entries.add(SILVER_LEGGINGS);
        entries.add(SILVER_BOOTS);
        
        // Railgunner Armor Set
        entries.add(RAILGUNNER_HELMET);
        entries.add(RAILGUNNER_CHESTPLATE);
        entries.add(RAILGUNNER_LEGGINGS);
        entries.add(RAILGUNNER_BOOTS);
        
        // Obelisk Armor Set
        entries.add(OBELISK_HELMET);
        entries.add(OBELISK_CHESTPLATE);
        entries.add(OBELISK_LEGGINGS);
        entries.add(OBELISK_BOOTS);
        
        // Resonant Armor Set
        entries.add(RESONANT_HELMET);
        entries.add(RESONANT_CHESTPLATE);
        entries.add(RESONANT_LEGGINGS);
        entries.add(RESONANT_BOOTS);
        
        // Silver Tools
        entries.add(SILVER_SWORD);
        entries.add(SILVER_PICKAXE);
        entries.add(SILVER_AXE);
        entries.add(SILVER_SHOVEL);
        entries.add(SILVER_HOE);
        
        // Resonant Tools
        entries.add(RESONANT_SWORD);
        entries.add(RESONANT_PICKAXE);
        entries.add(RESONANT_AXE);
        entries.add(RESONANT_SHOVEL);
        entries.add(RESONANT_HOE);
        
        // Other Weapons and Tools
        entries.add(NEEDLE);
        entries.add(OBELISK_SWORD);
        entries.add(CHISEL);
        entries.add(RAILGUN);
        entries.add(KINETIC_BLADE);
        entries.add(HEALING_STAFF);
        entries.add(WAND);
        entries.add(SPELLBOOK);
        entries.add(SPELLBOOK_PAGE);
        
        // Special Items
        entries.add(SILVER_PENDANT);
        entries.add(OVERHEATED_FUEL_ROD);
        entries.add(FUEL_ROD);
        entries.add(KAMIKAZE_RITUAL_SCROLL);
    }
    
    private static void addItemsToFunctionalItemGroup(FabricItemGroupEntries entries) {
        // Grand Shulker Box (only the main one has an item - colored variants are obtained through dyeing)
        entries.add(ModBlocks.GRAND_SHULKER_BOX);
        
        // Functional Blocks
        entries.add(ModBlocks.AUTO_CRAFTER_BLOCK);
        entries.add(ModBlocks.BLOCK_MINER);
        entries.add(ModBlocks.GROWTH_ACCELERATOR);
        entries.add(ModBlocks.BLOCK_PLACER);
        entries.add(ModBlocks.INTERACTOR);
        entries.add(ModBlocks.PARTICLE_BLOCK);
        entries.add(ModBlocks.PARTICLE_SINK_BLOCK);
        entries.add(ModBlocks.PLATE_BLOCK);
        entries.add(ModBlocks.ALCHEMY_TABLE);
        entries.add(ModBlocks.OBELISK);
        entries.add(ModBlocks.ACTIVE_OBELISK);
        entries.add(ModBlocks.BOSS_OBELISK);
        entries.add(ModBlocks.BARRIER_CIRCLE);
        entries.add(ModBlocks.BARRIER_DISTANCE_GLYPH);
        entries.add(ModBlocks.EFFIGY_FOUNTAIN);
        entries.add(ModBlocks.ACTIVE_EFFIGY_FOUNTAIN);
        entries.add(ModBlocks.BINDING_SPELL);
        entries.add(ModBlocks.ACTIVE_BINDING_SPELL);
        entries.add(ModBlocks.GUARDIAN_STATUE);
        entries.add(ModBlocks.PILLAR);
        entries.add(ModBlocks.DECORATIVE_FOUNTAIN);
        entries.add(ModBlocks.SPELL_TRIGGER_BLOCK);
        entries.add(ModBlocks.SOUND_BLOCK);
        entries.add(ModBlocks.SOUND_BLOCK2);
        // Note: FLOAT_BLOCK doesn't have an item - it's excluded from item creation
        
        // Silver Building Blocks
        entries.add(ModBlocks.SILVER_BLOCK);
        entries.add(ModBlocks.SILVER_STAIRS);
        entries.add(ModBlocks.SILVER_SLAB);
        entries.add(ModBlocks.SILVER_BUTTON);
        entries.add(ModBlocks.SILVER_PRESSURE_PLATE);
        entries.add(ModBlocks.SILVER_FENCE);
        entries.add(ModBlocks.SILVER_FENCE_GATE);
        entries.add(ModBlocks.SILVER_WALL);
        entries.add(ModBlocks.SILVER_DOOR);
        entries.add(ModBlocks.SILVER_TRAPDOOR);
        
        // Ores
        entries.add(ModBlocks.SILVER_ORE);
        entries.add(ModBlocks.DEEPSLATE_SILVER_ORE);
        entries.add(ModBlocks.CHARCOAL_BLOCK);
        
        // Note: STRAWBERRY_CROP and LEMON_CROP don't have items - they're accessed through seeds
        
        // Special Items
        entries.add(METAL_DETECTOR);
        entries.add(ALCHEMICAL_CATALYST);
        entries.add(RESONANT_CATALYST);
        entries.add(OBELISK_COMPASS);
        entries.add(WEATHERITEM);
        entries.add(MOD_TOTEM);
        entries.add(DIMENSION_HOPPER);
        entries.add(RESPAWN_BEACON);
        entries.add(DEFIANT_RIFT);
        entries.add(JOB_APPLICATION);
        entries.add(NBT_TOOL);
        entries.add(FIRE_RES_CRYSTAL);
        entries.add(SPEED_CRYSTAL);
        entries.add(REPAIR_CHARM);
        entries.add(FULFILLING_TIARA);
        entries.add(RESONANT_TIARA);
        entries.add(LATENT_MANA_PENDANT);
        entries.add(MANA_ROCKET);
        entries.add(DEEP_DARK_DEEP_DISH);
    }
}
