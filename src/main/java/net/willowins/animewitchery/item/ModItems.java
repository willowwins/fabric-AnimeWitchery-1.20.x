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
import net.willowins.animewitchery.item.custom.ResonantGreatSwordItem;
import net.willowins.animewitchery.item.custom.RevolverItem;
import net.willowins.animewitchery.item.custom.*;

import net.willowins.animewitchery.fluid.ModFluids;
import net.willowins.animewitchery.item.custom.StarlightBucketItem;
import net.minecraft.item.Items;

public class ModItems {
        public static final Item STARLIGHT_BUCKET = registerItem("starlight_bucket",
                        new StarlightBucketItem(ModFluids.STILL_STARLIGHT,
                                        new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)));
        public static final Item SILVER = registerItem("silveringot", new Item(new FabricItemSettings()));
        public static final Item SILVER_TEMPLATE = registerItem("silver_template", new Item(new FabricItemSettings()));
        public static final Item STAFF_HEAD = registerItem("staff_head", new Item(new FabricItemSettings()));
        public static final Item BLAZE_SACK = registerItem("blaze_powder_bag", new Item(new FabricItemSettings()));
        public static final Item VOID_ESSENCE = registerItem("void_essence",
                        new Item(new FabricItemSettings().rarity(Rarity.RARE)));
        public static final Item RUNE_STONE = registerItem("rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        public static final Item BLOOD_RUNE_STONE = registerItem("blood_rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.RARE)));
        public static final Item KAMIKAZE_RITUAL_SCROLL = registerItem("kamikaze_ritual_scroll",
                        new KamikazeRitualScroll(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
        public static final Item SILVERNUGGET = registerItem("silvernugget", new Item(new FabricItemSettings()));
        public static final Item RAWSILVER = registerItem("rawsilver", new Item(new FabricItemSettings()));
        public static final Item SPOOL = registerItem("spool", new Item(new FabricItemSettings()));
        public static final Item SILVERSPOOL = registerItem("silverspool", new Item(new FabricItemSettings()));
        public static final Item STAR_SHARD = registerItem("star_shard",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        public static final Item WEATHERITEM = registerItem("weatheritem",
                        new WeatherItem(new FabricItemSettings().maxCount(1)));
        public static final Item MOD_TOTEM = registerItem("mod_totem",
                        new ModTotemItem(new FabricItemSettings().maxCount(1)));
        public static final Item DIMENSION_HOPPER = registerItem("dimension",
                        new DimensionTeleportItem(new FabricItemSettings().maxCount(1)));
        public static final Item RESPAWN_BEACON = registerItem("respawn_beacon",
                        new RespawnTeleportItem(new FabricItemSettings().maxCount(1)));
        public static final Item DEFIANT_RIFT = registerItem("defiant_rift",
                        new BanHammerItem(new FabricItemSettings().maxCount(1)));
        public static final Item JOB_APPLICATION = registerItem("job_application",
                        new UnbanWandItem(new FabricItemSettings().maxCount(1)));
        public static final Item EMPTY_FLASK = registerItem("empty_flask",
                        new EmptyFlaskItem(new FabricItemSettings().maxCount(16)));
        public static final Item NBT_TOOL = registerItem("nbt_preserving_tool",
                        new NBTPreservingToolItem(new FabricItemSettings().maxCount(1)));

        public static final Item METAL_DETECTOR = registerItem("metal_detector",
                        new MetalDetectorItem(new FabricItemSettings().maxDamage(300)));

        public static final Item ALCHEMICAL_CATALYST = registerItem("alchemical_catalyst",
                        new AlchemicalCatalystItem(new FabricItemSettings().maxCount(1)));

        public static final Item RESONANT_CATALYST = registerItem("resonant_catalyst",
                        new ResonantlCatalystItem(new FabricItemSettings().maxCount(1)));

        public static final Item NEEDLE = registerItem("needle",
                        new NeedleItem(ModToolMaterial.SILVER, 4, -2.2f, new FabricItemSettings().maxCount(1)));

        public static final Item RESONANT_GREATSWORD = registerItem("resonant_greatsword",
                        new ResonantGreatSwordItem(ModToolMaterial.RESONANT, 7, -2.5f,
                                        new FabricItemSettings().maxCount(1)));

        public static final Item STAR_TEAR = registerItem("star_tear",
                        new StarTearItem(new FabricItemSettings()));

        public static final Item SILVER_SWORD = registerItem("silver_sword",
                        new SwordItem(ModToolMaterial.SILVER, 3, -2.4f, new FabricItemSettings()));

        public static final Item SILVER_PICKAXE = registerItem("silver_pickaxe",
                        new PickaxeItem(ModToolMaterial.SILVER, 0, -3f, new FabricItemSettings()));

        public static final Item OBELISK_PICKAXE = registerItem("obelisk_pickaxe",
                        new PickaxeItem(ModToolMaterial.OBELISK, 0, -2.8f, new FabricItemSettings()));

        public static final Item RESONANT_PICKAXE = registerItem("resonant_pickaxe",
                        new PickaxeItem(ModToolMaterial.RESONANT, 1, -2.8f, new FabricItemSettings()));

        public static final Item SILVER_AXE = registerItem("silver_axe",
                        new AxeItem(ModToolMaterial.SILVER, 6, -3.1f, new FabricItemSettings()));

        public static final Item SILVER_SHOVEL = registerItem("silver_shovel",
                        new ShovelItem(ModToolMaterial.SILVER, 1.5f, -3f, new FabricItemSettings()));

        public static final Item SILVER_HOE = registerItem("silver_hoe",
                        new HoeItem(ModToolMaterial.SILVER, -2, 0f, new FabricItemSettings()));

        public static final Item SILVER_HELMET = registerItem("silver_helmet",
                        new ModArmorItem(ModArmorMaterials.SILVER, ArmorItem.Type.HELMET, new FabricItemSettings()));
        public static final Item SILVER_CHESTPLATE = registerItem("silver_chestplate",
                        new ArmorItem(ModArmorMaterials.SILVER, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
        public static final Item SILVER_LEGGINGS = registerItem("silver_leggings",
                        new ArmorItem(ModArmorMaterials.SILVER, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
        public static final Item SILVER_BOOTS = registerItem("silver_boots",
                        new ArmorItem(ModArmorMaterials.SILVER, ArmorItem.Type.BOOTS, new FabricItemSettings()));

        public static final Item RAILGUNNER_HELMET = registerItem("railgunner_helmet",
                        new RailGunnerArmorItem(ModArmorMaterials.RAILGUNNER, ArmorItem.Type.HELMET,
                                        new FabricItemSettings()));
        public static final Item RAILGUNNER_CHESTPLATE = registerItem("railgunner_chestplate",
                        new RailGunnerArmorItem(ModArmorMaterials.RAILGUNNER, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings()));
        public static final Item RAILGUNNER_LEGGINGS = registerItem("railgunner_leggings",
                        new RailGunnerArmorItem(ModArmorMaterials.RAILGUNNER, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings()));
        public static final Item RAILGUNNER_BOOTS = registerItem("railgunner_boots",
                        new RailGunnerArmorItem(ModArmorMaterials.RAILGUNNER, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings()));

        // Obelisk Armor Set
        public static final Item OBELISK_HELMET = registerItem("obelisk_helmet",
                        new ObeliskArmorItem(ModArmorMaterials.OBELISK, ArmorItem.Type.HELMET,
                                        new FabricItemSettings()));
        public static final Item OBELISK_CHESTPLATE = registerItem("obelisk_chestplate",
                        new ObeliskArmorItem(ModArmorMaterials.OBELISK, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings()));
        public static final Item OBELISK_LEGGINGS = registerItem("obelisk_leggings",
                        new ObeliskArmorItem(ModArmorMaterials.OBELISK, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings()));
        public static final Item OBELISK_BOOTS = registerItem("obelisk_boots",
                        new ObeliskArmorItem(ModArmorMaterials.OBELISK, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings()));

        // Resonant Armor Set
        public static final Item RESONANT_HELMET = registerItem("resonant_helmet",
                        new ResonantArmorItem(ModArmorMaterials.RESONANT, ArmorItem.Type.HELMET,
                                        new FabricItemSettings()));
        public static final Item RESONANT_CHESTPLATE = registerItem("resonant_chestplate",
                        new ResonantArmorItem(ModArmorMaterials.RESONANT, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings()));
        public static final Item RESONANT_LEGGINGS = registerItem("resonant_leggings",
                        new ResonantArmorItem(ModArmorMaterials.RESONANT, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings()));
        public static final Item RESONANT_BOOTS = registerItem("resonant_boots",
                        new ResonantArmorItem(ModArmorMaterials.RESONANT, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings()));

        public static final Item OATHBREAKER = registerItem("oathbreaker",
                        new OathbreakerItem(ModToolMaterial.RESONANT, 7, -2.4f, new FabricItemSettings().fireproof()));

        public static final Item RESONANT_SWORD = registerItem("resonant_sword",
                        new SwordItem(ModToolMaterial.RESONANT, 3, -2.4f, new FabricItemSettings()));

        public static final Item KEY = registerItem("key",
                        new net.willowins.animewitchery.item.custom.KeyItem(new FabricItemSettings().maxCount(1)));

        public static final Item MASTER_KEY = registerItem("master_key",
                        new Item(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));

        public static final Item RESONANT_AXE = registerItem("resonant_axe",
                        new AxeItem(ModToolMaterial.RESONANT, 6, -3.1f, new FabricItemSettings()));

        public static final Item RESONANT_SHOVEL = registerItem("resonant_shovel",
                        new ShovelItem(ModToolMaterial.RESONANT, 1.5f, -3f, new FabricItemSettings()));

        public static final Item RESONANT_HOE = registerItem("resonant_hoe",
                        new HoeItem(ModToolMaterial.RESONANT, -2, 0f, new FabricItemSettings()));

        public static final Item LEMON = registerItem("lemon",
                        new Item(new FabricItemSettings().food(ModFoodComponents.LEMON)));

        public static final Item FIRE_RES_CRYSTAL = registerItem("fire_res_crystal",
                        new FireResCrystalItem(new FabricItemSettings()));

        public static final Item SPEED_CRYSTAL = registerItem("speed_crystal",
                        new SpeedCrystalItem(new FabricItemSettings()));

        public static final Item REPAIR_CHARM = registerItem("creeping_prevailance",
                        new RepairCharmItem(new FabricItemSettings().maxCount(1)));

        public static final Item FULFILLING_TIARA = registerItem("fulfilling_tiara",
                        new FulfillingTiaraItem(new FabricItemSettings().maxCount(1)));

        public static final Item RESONANT_TIARA = registerItem("resonant_tiara",
                        new ResonantTiaraItem(new FabricItemSettings().maxCount(1)));

        public static final Item LATENT_MANA_PENDANT = registerItem("latent_mana_pendant",
                        new LatentManaPendantItem(new FabricItemSettings().maxCount(1)));

        public static final Item KEEP_INVENTORY_CHARM = registerItem("keep_inventory_charm",
                        new KeepInventoryCharmItem(new FabricItemSettings().maxCount(1)));

        public static final Item EMERGENCY_RECALL = registerItem("emergency_recall",
                        new EmergencyRecallItem(new FabricItemSettings().maxCount(1)));

        public static final Item MANA_ROCKET = registerItem("mana_rocket",
                        new ManaRocketItem(new FabricItemSettings().maxCount(1)));

        public static final Item KINETIC_BLADE = registerItem("kinetic_breaker",
                        new KineticBladeItem(new FabricItemSettings().maxCount(1)));

        public static final Item OVERHEATED_FUEL_ROD = registerItem("overheated_fuel_rod",
                        new OverheatedFuelRodItem(new FabricItemSettings().maxCount(1)));

        public static final Item FUEL_ROD = registerItem("fuel_rod",
                        new FuelRodItem(new FabricItemSettings().maxCount(1)));

        public static final Item CRYSTALLINE_SPATIAL_FOLD = registerItem("crystalline_spatial_fold",
                        new CrystallineSpatialFoldItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));

        public static final Item DEEP_DARK_DEEP_DISH = registerItem("deep_dark_deep_dish",
                        new DeepDarkDeepDishItem(new FabricItemSettings().maxCount(1)));

        public static final Item STRAWBERRY = registerItem("strawberry",
                        new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY)));

        public static final Item TART_CRUST = registerItem("tart_crust",
                        new Item(new FabricItemSettings().food(ModFoodComponents.TART_CRUST)));

        public static final Item UNBAKED_LEMON_TART = registerItem("unbaked_lemon_tart",
                        new Item(new FabricItemSettings().food(ModFoodComponents.UNBAKED_TART)));

        public static final Item UNBAKED_STRAWBERRY_TART = registerItem("unbaked_strawberry_tart",
                        new Item(new FabricItemSettings().food(ModFoodComponents.UNBAKED_TART)));

        public static final Item LEMON_TART = registerItem("lemon_tart",
                        new Item(new FabricItemSettings().food(ModFoodComponents.LEMON_TART)));

        public static final Item STRAWBERRY_TART = registerItem("strawberry_tart",
                        new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY_TART)));

        public static final Item STRAWBERRY_SEEDS = registerItem("strawberry_seeds",
                        new AliasedBlockItem(ModBlocks.STRAWBERRY_CROP, new FabricItemSettings()));

        public static final Item LEMON_SEEDS = registerItem("lemon_seeds",
                        new AliasedBlockItem(ModBlocks.LEMON_CROP, new FabricItemSettings()));

        public static final Item ROSEWILLOW_VINES = registerItem("rosewillow_vines",
                        new AliasedBlockItem(ModBlocks.ROSEWILLOW_VINES_TIP, new FabricItemSettings()));

        public static final Item ROSEWILLOW_BLOSSOM = registerItem("rosewillow_blossom",
                        new RosewillowBlossomItem(new FabricItemSettings().food(ModFoodComponents.ROSEWILLOW_BLOSSOM)));

        public static final Item COSMETIC_BAG = registerItem("cosmetic_bag",
                        new CosmeticBagItem(new FabricItemSettings().maxCount(1)));

        // Haloic Equipment
        // Armor
        public static final Item HALOIC_HELMET = registerItem("haloic_helmet",
                        new ModArmorItem(ModArmorMaterials.HALOIC, ArmorItem.Type.HELMET, new FabricItemSettings()));
        public static final Item HALOIC_CHESTPLATE = registerItem("haloic_chestplate",
                        new ModArmorItem(ModArmorMaterials.HALOIC, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings()));
        public static final Item HALOIC_LEGGINGS = registerItem("haloic_leggings",
                        new ModArmorItem(ModArmorMaterials.HALOIC, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
        public static final Item HALOIC_BOOTS = registerItem("haloic_boots",
                        new ModArmorItem(ModArmorMaterials.HALOIC, ArmorItem.Type.BOOTS, new FabricItemSettings()));

        // Tools
        public static final Item HALOIC_SWORD = registerItem("haloic_sword",
                        new HaloicSwordItem(ModToolMaterial.HALOIC, 3, -2.4f, new FabricItemSettings()));
        public static final Item HALOIC_PICKAXE = registerItem("haloic_pickaxe",
                        new PickaxeItem(ModToolMaterial.HALOIC, 1, -2.8f, new FabricItemSettings()));
        public static final Item HALOIC_AXE = registerItem("haloic_axe",
                        new AxeItem(ModToolMaterial.HALOIC, 5, -3.0f, new FabricItemSettings()));
        public static final Item HALOIC_SHOVEL = registerItem("haloic_shovel",
                        new ShovelItem(ModToolMaterial.HALOIC, 1.5f, -3.0f, new FabricItemSettings()));
        public static final Item HALOIC_HOE = registerItem("haloic_hoe",
                        new HoeItem(ModToolMaterial.HALOIC, -4, 0.0f, new FabricItemSettings()));

        public static final Item SILVER_PENDANT = registerItem("silver_pendant",
                        new SilverPendant(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item HEALING_STAFF = registerItem("healing_staff",
                        new HealingStaff(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item RAILGUN = registerItem("railgun",
                        new RailgunItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item OBELISK_SWORD = registerItem("obelisk_sword", new ObeliskSwordItem(
                        ModToolMaterial.OBELISK, 7, -1.4f, new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        public static final Item RESONANT_SHIELD = registerItem("resonant_shield",
                        new ResonantShieldItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));

        public static final Item RESONANT_FOCUS = registerItem("resonant_focus",
                        new ResonantBeamItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        public static final Item RESONANT_SPARK = registerItem("resonant_spark",
                        new ResonantSparkItem(new FabricItemSettings().maxCount(16).rarity(Rarity.RARE)));

        public static final Item DOMAIN_SPARK = registerItem("domain_spark",
                        new DomainSparkItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));

        // Hidden item for entity rendering
        public static final Item RESONANT_SHIELD_ENTITY_MODEL = registerItem("resonant_shield_3d",
                        new Item(new FabricItemSettings()));

        public static final Item CHISEL = registerItem("chisel",
                        new ChiselItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)));

        public static final Item OBELISK_SHARD = registerItem("obelisk_shard",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));

        public static final Item COPPER_WARHAMMER = registerItem("copper_warhammer", new CopperWarhammerItem(
                        ModToolMaterial.COPPER, 9, -3.2f, new FabricItemSettings().maxCount(1)));

        // New alchemy and magic items
        public static final Item MORTAR_AND_PESTLE = registerItem("mortar_and_pestle",
                        new Item(new FabricItemSettings().maxCount(1)));
        public static final Item CHALK = registerItem("chalk",
                        new NormalChalkItem(new FabricItemSettings().maxCount(1)));
        public static final Item MAGIC_CHALK = registerItem("magic_chalk",
                        new MagicChalkItem(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1)));
        public static final Item ENCHANTED_CRYSTAL = registerItem("enchanted_crystal",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));

        public static final Item SOUL_JAR = registerItem("soul_jar",
                        new SoulJarItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        public static final Item SOUL_SCYTHE = registerItem("soul_scythe",
                        new SoulScytheItem(ModToolMaterial.HALOIC, 8, -3.0f,
                                        new FabricItemSettings().rarity(Rarity.EPIC)));

        public static final Item SUMMONER_STAFF = registerItem("summoner_staff",
                        new SummonerStaffItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));

        public static final Item SOUL = registerItem("soul",
                        new SoulItem(new FabricItemSettings().maxCount(1)));

        // Dust items
        public static final Item BONE_DUST = registerItem("bone_dust", new Item(new FabricItemSettings()));
        public static final Item AMETHYST_DUST = registerItem("amethyst_dust", new Item(new FabricItemSettings()));
        public static final Item STARDUST = registerItem("stardust", new Item(new FabricItemSettings()));

        // Rune Stones
        public static final Item FIRE_RUNE_STONE = registerItem("fire_rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        public static final Item WATER_RUNE_STONE = registerItem("water_rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        public static final Item EARTH_RUNE_STONE = registerItem("earth_rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        public static final Item AIR_RUNE_STONE = registerItem("air_rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        public static final Item LIFE_RUNE_STONE = registerItem("life_rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        public static final Item DEATH_RUNE_STONE = registerItem("death_rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        public static final Item LIGHT_RUNE_STONE = registerItem("light_rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        public static final Item DARKNESS_RUNE_STONE = registerItem("darkness_rune_stone",
                        new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON)));

        // Barrier items
        public static final Item BARRIER_CATALYST = registerItem("barrier_catalyst",
                        new Item(new FabricItemSettings().rarity(Rarity.RARE)));
        public static final Item REPAIR_ESSENCE = registerItem("repair_essence",
                        new RepairEssenceItem(new FabricItemSettings().rarity(Rarity.RARE)));

        public static final Item HALOIC_SCRAP = registerItem("haloic_scrap",
                        new Item(new FabricItemSettings().rarity(Rarity.RARE)));
        public static final Item HALOIC_INGOT = registerItem("haloic_ingot",
                        new Item(new FabricItemSettings().rarity(Rarity.EPIC)));

        // Entity spawn eggs
        public static final Item VOID_WISP_SPAWN_EGG = registerItem("void_wisp_spawn_egg",
                        new VoidWispSpawnEggItem(new FabricItemSettings().maxCount(64)));

        // Patchouli book
        public static final Item RITUALS_BOOK = registerItem("rituals_book",
                        new net.willowins.animewitchery.item.custom.PatchouliBookItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON),
                                        "animewitchery:rituals"));

        // Magic wand
        public static final Item WAND = registerItem("wand",
                        new net.willowins.animewitchery.item.custom.WandItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Spellbook
        public static final Item SPELLBOOK = registerItem("spellbook",
                        new net.willowins.animewitchery.item.custom.SpellbookItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));

        // Spellbook Page
        public static final Item SPELLBOOK_PAGE = registerItem("spellbook_page",
                        new net.willowins.animewitchery.item.custom.SpellbookPageItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Spell Scrolls
        public static final Item FIRE_SPELL_SCROLL = registerItem("fire_spell_scroll",
                        new net.willowins.animewitchery.item.custom.SpellScrollItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Fire Blast"));
        public static final Item WATER_SPELL_SCROLL = registerItem("water_spell_scroll",
                        new net.willowins.animewitchery.item.custom.SpellScrollItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Water Shield"));
        public static final Item EARTH_SPELL_SCROLL = registerItem("earth_spell_scroll",
                        new net.willowins.animewitchery.item.custom.SpellScrollItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Earth Spike"));
        public static final Item AIR_SPELL_SCROLL = registerItem("air_spell_scroll",
                        new net.willowins.animewitchery.item.custom.SpellScrollItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Wind Gust"));
        public static final Item LIFE_SPELL_SCROLL = registerItem("life_spell_scroll",
                        new net.willowins.animewitchery.item.custom.SpellScrollItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Healing Wave"));
        public static final Item DEATH_SPELL_SCROLL = registerItem("death_spell_scroll",
                        new net.willowins.animewitchery.item.custom.SpellScrollItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Wither Touch"));
        public static final Item LIGHT_SPELL_SCROLL = registerItem("light_spell_scroll",
                        new net.willowins.animewitchery.item.custom.SpellScrollItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Light Burst"));
        public static final Item DARKNESS_SPELL_SCROLL = registerItem("darkness_spell_scroll",
                        new net.willowins.animewitchery.item.custom.SpellScrollItem(
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), "Shadow Bind"));

        // --- Class System Items: Phase 1 ---

        // Assassin
        public static final Item ASSASSIN_DAGGER = registerItem("assassin_dagger",
                        new AssassinDaggerItem(ModToolMaterial.SILVER, 5, -1.6f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item ASSASSIN_HOOD = registerItem("assassin_hood",
                        new AssassinHoodItem(ModArmorMaterials.ASSASSIN, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item ASSASSIN_CHESTPLATE = registerItem("assassin_chestplate",
                        new ArmorItem(ModArmorMaterials.ASSASSIN, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item ASSASSIN_LEGGINGS = registerItem("assassin_leggings",
                        new ArmorItem(ModArmorMaterials.ASSASSIN, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item ASSASSIN_BOOTS = registerItem("assassin_boots",
                        new ArmorItem(ModArmorMaterials.ASSASSIN, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Butcher
        public static final Item BUTCHER_KNIFE = registerItem("butcher_knife",
                        new ButcherKnifeItem(ModToolMaterial.SILVER, 6, -2.4f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item BUTCHER_HELMET = registerItem("butcher_helmet",
                        new ArmorItem(ModArmorMaterials.BUTCHER, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item BUTCHER_APRON = registerItem("butcher_apron",
                        new ButcherApronItem(ModArmorMaterials.BUTCHER, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item BUTCHER_LEGGINGS = registerItem("butcher_leggings",
                        new ArmorItem(ModArmorMaterials.BUTCHER, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item BUTCHER_BOOTS = registerItem("butcher_boots",
                        new ArmorItem(ModArmorMaterials.BUTCHER, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Brute
        public static final Item HEAVY_MACE = registerItem("heavy_mace",
                        new HeavyMaceItem(ModToolMaterial.SILVER, 9, -3.2f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item HEAVY_PLATE_HELMET = registerItem("heavy_plate_helmet",
                        new ArmorItem(ModArmorMaterials.BRUTE, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item HEAVY_PLATE_CHESTPLATE = registerItem("heavy_plate_chestplate",
                        new HeavyPlateArmorItem(ModArmorMaterials.BRUTE, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item HEAVY_PLATE_LEGGINGS = registerItem("heavy_plate_leggings",
                        new ArmorItem(ModArmorMaterials.BRUTE, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item HEAVY_PLATE_BOOTS = registerItem("heavy_plate_boots",
                        new ArmorItem(ModArmorMaterials.BRUTE, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Monk
        public static final Item IRON_SHACKLE = registerItem("iron_shackle",
                        new IronShackleItem(ModToolMaterial.SILVER, 4, 0.0f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE))); // Fast speed
        public static final Item MONK_HEAD = registerItem("monk_head",
                        new ArmorItem(ModArmorMaterials.MONK, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item SPARSE_TUNIC = registerItem("sparse_tunic",
                        new SparseTunicItem(ModArmorMaterials.MONK, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item MONK_LEGGINGS = registerItem("monk_leggings",
                        new ArmorItem(ModArmorMaterials.MONK, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item MONK_BOOTS = registerItem("monk_boots",
                        new ArmorItem(ModArmorMaterials.MONK, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // --- Phase 2 Items (Magic & Healing) ---
        // Skill Orbs
        public static final Item MINOR_SKILL_ORB = registerItem("minor_skill_orb",
                        new SkillOrbItem(new FabricItemSettings().maxCount(64).rarity(Rarity.COMMON), 1, 20, 1));
        public static final Item MAJOR_SKILL_ORB = registerItem("major_skill_orb",
                        new SkillOrbItem(new FabricItemSettings().maxCount(64).rarity(Rarity.UNCOMMON), 21, 50, 1));
        public static final Item GRAND_SKILL_ORB = registerItem("grand_skill_orb",
                        new SkillOrbItem(new FabricItemSettings().maxCount(64).rarity(Rarity.RARE), 51, 80, 1));
        public static final Item ASCENDANT_SKILL_ORB = registerItem("ascendant_skill_orb",
                        new SkillOrbItem(new FabricItemSettings().maxCount(64).rarity(Rarity.EPIC), 81, 100, 1));

        // Healer
        // Armor (Silver Set is used for Healer now)

        // Paladin
        public static final Item GREAT_HAMMER = registerItem("great_hammer",
                        new GreatHammerItem(ModToolMaterial.SILVER, 9, -3.4f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PALADIN_HELMET = registerItem("paladin_helmet",
                        new PaladinArmorItem(ModArmorMaterials.PALADIN, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PALADIN_CHESTPLATE = registerItem("paladin_chestplate",
                        new PaladinArmorItem(ModArmorMaterials.PALADIN, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PALADIN_LEGGINGS = registerItem("paladin_leggings",
                        new PaladinArmorItem(ModArmorMaterials.PALADIN, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PALADIN_BOOTS = registerItem("paladin_boots",
                        new PaladinArmorItem(ModArmorMaterials.PALADIN, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        // Deathbringer (Already full set)

        // Sanguine (Moved here to group Phase 2)
        public static final Item BLOODLETTER = registerItem("bloodletter",
                        new BloodletterItem(ModToolMaterial.SILVER, 4, 1.0f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item SANGUINE_HOOD = registerItem("sanguine_hood",
                        new ArmorItem(ModArmorMaterials.SANGUINE, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item HOODED_CAPE = registerItem("hooded_cape",
                        new HoodedCapeItem(ModArmorMaterials.SANGUINE, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item SANGUINE_LEGGINGS = registerItem("sanguine_leggings",
                        new ArmorItem(ModArmorMaterials.SANGUINE, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item SANGUINE_BOOTS = registerItem("sanguine_boots",
                        new ArmorItem(ModArmorMaterials.SANGUINE, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        public static final Item SCYTHE = registerItem("scythe",
                        new ScytheItem(ModToolMaterial.SILVER, 6, -2.5f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item DEATHBRINGER_HOOD = registerItem("deathbringer_hood",
                        new DeathbringerRobesItem(ModArmorMaterials.DEATHBRINGER, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item DEATHBRINGER_ROBES = registerItem("deathbringer_robes",
                        new DeathbringerRobesItem(ModArmorMaterials.DEATHBRINGER, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item DEATHBRINGER_LEGGINGS = registerItem("deathbringer_leggings",
                        new DeathbringerRobesItem(ModArmorMaterials.DEATHBRINGER, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item DEATHBRINGER_BOOTS = registerItem("deathbringer_boots",
                        new DeathbringerRobesItem(ModArmorMaterials.DEATHBRINGER, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Summoner
        public static final Item SUMMONER_HOOD = registerItem("summoner_hood",
                        new SummonerRobesItem(ModArmorMaterials.SUMMONER, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item SUMMONER_ROBES = registerItem("summoner_robes",
                        new SummonerRobesItem(ModArmorMaterials.SUMMONER, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item SUMMONER_LEGGINGS = registerItem("summoner_leggings",
                        new SummonerRobesItem(ModArmorMaterials.SUMMONER, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item SUMMONER_BOOTS = registerItem("summoner_boots",
                        new SummonerRobesItem(ModArmorMaterials.SUMMONER, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Mage
        public static final Item MAGE_STAFF = registerItem("mage_staff",
                        new MageStaffItem(ModToolMaterial.SILVER, 6, -2.4f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item MAGE_HAT = registerItem("mage_hat",
                        new MageRobesItem(ModArmorMaterials.MAGE, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item MAGE_ROBES = registerItem("mage_robes",
                        new MageRobesItem(ModArmorMaterials.MAGE, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item MAGE_LEGGINGS = registerItem("mage_leggings",
                        new MageRobesItem(ModArmorMaterials.MAGE, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item MAGE_BOOTS = registerItem("mage_boots",
                        new MageRobesItem(ModArmorMaterials.MAGE, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Phase 5
        // Farmer
        public static final Item FARMING_HOE = registerItem("farming_hoe",
                        new FarmingHoeItem(ModToolMaterial.SILVER, 5, -1.0f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item STRAW_HAT = registerItem("straw_hat",
                        new FarmerArmorItem(ModArmorMaterials.FARMER, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item FARMER_OVERALLS = registerItem("farmer_overalls",
                        new FarmerArmorItem(ModArmorMaterials.FARMER, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item FARMER_LEGGINGS = registerItem("farmer_leggings",
                        new FarmerArmorItem(ModArmorMaterials.FARMER, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item FARMER_BOOTS = registerItem("farmer_boots",
                        new FarmerArmorItem(ModArmorMaterials.FARMER, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Prospector
        public static final Item PROSPECTOR_SHOVEL = registerItem("prospector_shovel",
                        new ProspectorShovelItem(ModToolMaterial.SILVER, 4.5f, -3.0f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item DIVINING_ROD = registerItem("divining_rod",
                        new DiviningRodItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item TORCH_FLOWER_ESSENCE = registerItem("torch_flower_essence",
                        new TorchFlowerEssenceItem(new FabricItemSettings()));
        public static final Item PROSPECTOR_HAT = registerItem("prospector_hat",
                        new ArmorItem(ModArmorMaterials.PROSPECTOR, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PROSPECTOR_TUNIC = registerItem("prospector_tunic",
                        new ArmorItem(ModArmorMaterials.PROSPECTOR, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PROSPECTOR_LEGGINGS = registerItem("prospector_leggings",
                        new ArmorItem(ModArmorMaterials.PROSPECTOR, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PROSPECTOR_BOOTS = registerItem("prospector_boots",
                        new ArmorItem(ModArmorMaterials.PROSPECTOR, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Hoarder
        public static final Item SWORD_OF_MIDAS = registerItem("sword_of_midas",
                        new SwordOfMidasItem(net.minecraft.item.ToolMaterials.GOLD, 6, -2.4f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
        public static final Item HOARDER_HELMET = registerItem("hoarder_helmet",
                        new HoarderArmorItem(ModArmorMaterials.HOARDER, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item HOARDER_CHESTPLATE = registerItem("hoarder_chestplate",
                        new HoarderArmorItem(ModArmorMaterials.HOARDER, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item HOARDER_LEGGINGS = registerItem("hoarder_leggings",
                        new HoarderArmorItem(ModArmorMaterials.HOARDER, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item HOARDER_BOOTS = registerItem("hoarder_boots",
                        new HoarderArmorItem(ModArmorMaterials.HOARDER, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Druid
        public static final Item NATURE_STAFF = registerItem("nature_staff",
                        new NatureStaffItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item DRUID_HOOD = registerItem("druid_hood",
                        new ArmorItem(ModArmorMaterials.DRUID, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item COTTON_GOWN = registerItem("cotton_gown",
                        new CottonGownItem(ModArmorMaterials.DRUID, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item DRUID_LEGGINGS = registerItem("druid_leggings",
                        new ArmorItem(ModArmorMaterials.DRUID, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item DRUID_BOOTS = registerItem("druid_boots",
                        new ArmorItem(ModArmorMaterials.DRUID, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Alchemist
        public static final Item POTION_FLASK = registerItem("potion_flask",
                        new PotionFlaskItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item ALCHEMIST_HOOD = registerItem("alchemist_hood",
                        new ArmorItem(ModArmorMaterials.ALCHEMIST, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item STAINED_ROBES = registerItem("stained_robes",
                        new StainedRobesItem(ModArmorMaterials.ALCHEMIST, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item ALCHEMIST_LEGGINGS = registerItem("alchemist_leggings",
                        new ArmorItem(ModArmorMaterials.ALCHEMIST, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item ALCHEMIST_BOOTS = registerItem("alchemist_boots",
                        new ArmorItem(ModArmorMaterials.ALCHEMIST, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Gunslinger
        public static final Item BULLET = registerItem("bullet", new BulletItem(new FabricItemSettings().maxCount(64)));
        public static final Item REVOLVER = registerItem("revolver",
                        new RevolverItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE).maxDamage(1000)));
        public static final Item COWBOY_HAT = registerItem("cowboy_hat",
                        new CowboyHatItem(ModArmorMaterials.GUNSLINGER, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item GUNSLINGER_VEST = registerItem("gunslinger_vest",
                        new ArmorItem(ModArmorMaterials.GUNSLINGER, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item CHAPS = registerItem("chaps",
                        new ChapsItem(ModArmorMaterials.GUNSLINGER, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item GUNSLINGER_BOOTS = registerItem("gunslinger_boots",
                        new ArmorItem(ModArmorMaterials.GUNSLINGER, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Gambler
        public static final Item DECK_OF_CARDS = registerItem("deck_of_cards",
                        new DeckOfCardsItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item CHIPPED_DICE = registerItem("chipped_dice",
                        new ChippedDiceItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item GAMBLER_VISOR = registerItem("gambler_visor",
                        new ArmorItem(ModArmorMaterials.GAMBLER, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item SOILED_JACKET = registerItem("soiled_jacket",
                        new SoiledJacketItem(ModArmorMaterials.GAMBLER, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item GAMBLER_PANTS = registerItem("gambler_pants",
                        new ArmorItem(ModArmorMaterials.GAMBLER, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item GAMBLER_SHOES = registerItem("gambler_shoes",
                        new ArmorItem(ModArmorMaterials.GAMBLER, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Femboy
        public static final Item STUFFED_ANIMAL = registerItem("stuffed_animal",
                        new StuffedAnimalItem(ModToolMaterial.SILVER, 0, 0f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item CAT_EARS = registerItem("cat_ears",
                        new ArmorItem(ModArmorMaterials.FEMBOY, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item FEMBOY_HOODIE = registerItem("femboy_hoodie",
                        new ArmorItem(ModArmorMaterials.FEMBOY, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item THIGH_HIGHS = registerItem("thigh_highs",
                        new ThighHighsItem(ModArmorMaterials.FEMBOY, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PLATFORM_BOOTS = registerItem("platform_boots",
                        new ArmorItem(ModArmorMaterials.FEMBOY, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        // Knight
        public static final Item LONGSWORD = registerItem("longsword",
                        new LongswordItem(ModToolMaterial.SILVER, 7, -2.8f,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item CROWN = registerItem("crown",
                        new CrownItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.HELMET,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PLATE_CHESTPLATE = registerItem("plate_chestplate",
                        new PlateArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.CHESTPLATE,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PLATE_LEGGINGS = registerItem("plate_leggings",
                        new PlateArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.LEGGINGS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
        public static final Item PLATE_BOOTS = registerItem("plate_boots",
                        new PlateArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.BOOTS,
                                        new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

        private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
                // Basic materials
                entries.add(RESONANT_SHIELD);
                entries.add(RESONANT_FOCUS);
                entries.add(RESONANT_SPARK);
                entries.add(DOMAIN_SPARK);
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
                entries.add(HALOIC_SCRAP);
                entries.add(HALOIC_INGOT);

                // Alchemy and magic items
                entries.add(MORTAR_AND_PESTLE);
                entries.add(CHALK);
                entries.add(MAGIC_CHALK);
                entries.add(ENCHANTED_CRYSTAL);

                // Dust items
                entries.add(BONE_DUST);
                entries.add(AMETHYST_DUST);
                entries.add(STARDUST);

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

                // Skill Orbs
                entries.add(MINOR_SKILL_ORB);
                entries.add(MAJOR_SKILL_ORB);
                entries.add(GRAND_SKILL_ORB);
                entries.add(ASCENDANT_SKILL_ORB);

                // Books (hidden from JEI)
                // entries.add(RITUALS_BOOK);
        }

        private static Item registerItem(String name, Item item) {
                return Registry.register(Registries.ITEM, new Identifier(AnimeWitchery.MOD_ID, name), item);

        }

        public static void registerModItems() {
                AnimeWitchery.LOGGER.info("registering Mod Items for" + AnimeWitchery.MOD_ID);
                ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                                .register(ModItems::addItemsToIngredientItemGroup);
                ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemsToCombatItemGroup);
                ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                                .register(ModItems::addItemsToFunctionalItemGroup);
                ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS)
                                .register(ModItems::addItemsToBuildingBlocksItemGroup);
        }

        private static void addItemsToBuildingBlocksItemGroup(FabricItemGroupEntries entries) {
                entries.add(ModBlocks.ROSEWILLOW_LOG);
                entries.add(ModBlocks.ROSEWILLOW_LOG_BLOOMING);
                entries.add(ModBlocks.ROSEWILLOW_PLANKS);
                entries.add(ModBlocks.ROSEWILLOW_STAIRS);
                entries.add(ModBlocks.ROSEWILLOW_SLAB);
                entries.add(ModBlocks.ROSEWILLOW_FENCE);
                entries.add(ModBlocks.ROSEWILLOW_FENCE_GATE);
                entries.add(ModBlocks.ROSEWILLOW_LEAVES);
                entries.add(ModBlocks.LARGE_ROSEWILLOW_BLOSSOM);
        }

        private static void addItemsToCombatItemGroup(FabricItemGroupEntries entries) {
                // Phase 1 Classes
                entries.add(ASSASSIN_DAGGER);
                entries.add(ASSASSIN_HOOD);
                entries.add(ASSASSIN_CHESTPLATE);
                entries.add(ASSASSIN_LEGGINGS);
                entries.add(ASSASSIN_BOOTS);

                entries.add(BUTCHER_KNIFE);
                entries.add(BUTCHER_HELMET); // New
                entries.add(BUTCHER_APRON);
                entries.add(BUTCHER_LEGGINGS); // New
                entries.add(BUTCHER_BOOTS); // New

                entries.add(HEAVY_MACE);
                entries.add(HEAVY_PLATE_HELMET); // New
                entries.add(HEAVY_PLATE_CHESTPLATE);
                entries.add(HEAVY_PLATE_LEGGINGS); // New
                entries.add(HEAVY_PLATE_BOOTS); // New

                entries.add(IRON_SHACKLE);
                entries.add(MONK_HEAD); // New
                entries.add(SPARSE_TUNIC);
                entries.add(MONK_LEGGINGS); // New
                entries.add(MONK_BOOTS); // New

                entries.add(HEALING_STAFF);
                // Healer uses Silver Armor now

                entries.add(BLOODLETTER);
                entries.add(SANGUINE_HOOD); // New
                entries.add(HOODED_CAPE);
                entries.add(SANGUINE_LEGGINGS); // New
                entries.add(SANGUINE_BOOTS); // New

                entries.add(NATURE_STAFF);
                entries.add(DRUID_HOOD); // New
                entries.add(COTTON_GOWN);
                entries.add(DRUID_LEGGINGS); // New
                entries.add(DRUID_BOOTS); // New

                entries.add(POTION_FLASK);
                entries.add(ALCHEMIST_HOOD); // New
                entries.add(STAINED_ROBES);
                entries.add(ALCHEMIST_LEGGINGS); // New
                entries.add(ALCHEMIST_BOOTS); // New

                // Phase 3
                entries.add(BULLET);
                entries.add(REVOLVER);
                entries.add(COWBOY_HAT);
                entries.add(GUNSLINGER_VEST); // New
                entries.add(CHAPS);
                entries.add(GUNSLINGER_BOOTS); // New

                entries.add(DECK_OF_CARDS);
                entries.add(CHIPPED_DICE);
                entries.add(GAMBLER_VISOR); // New
                entries.add(SOILED_JACKET);
                entries.add(GAMBLER_PANTS); // New
                entries.add(GAMBLER_SHOES); // New

                entries.add(STUFFED_ANIMAL);
                entries.add(CAT_EARS); // New
                entries.add(FEMBOY_HOODIE); // New
                entries.add(THIGH_HIGHS);
                entries.add(PLATFORM_BOOTS); // New

                // Phase 4
                entries.add(LONGSWORD);
                entries.add(CROWN);
                entries.add(PLATE_CHESTPLATE);
                entries.add(PLATE_LEGGINGS);
                entries.add(PLATE_BOOTS);

                entries.add(GREAT_HAMMER);
                entries.add(PALADIN_HELMET);
                entries.add(PALADIN_CHESTPLATE);
                entries.add(PALADIN_LEGGINGS);
                entries.add(PALADIN_BOOTS);

                entries.add(SCYTHE);
                entries.add(DEATHBRINGER_HOOD);
                entries.add(DEATHBRINGER_ROBES);
                entries.add(DEATHBRINGER_LEGGINGS);
                entries.add(DEATHBRINGER_BOOTS);

                entries.add(SUMMONER_STAFF);
                entries.add(SUMMONER_HOOD);
                entries.add(SUMMONER_ROBES);
                entries.add(SUMMONER_LEGGINGS);
                entries.add(SUMMONER_BOOTS);

                entries.add(MAGE_STAFF);
                entries.add(MAGE_HAT);
                entries.add(MAGE_ROBES);
                entries.add(MAGE_LEGGINGS);
                entries.add(MAGE_BOOTS);

                // Phase 5
                entries.add(FARMING_HOE);
                entries.add(STRAW_HAT);
                entries.add(FARMER_OVERALLS);
                entries.add(FARMER_LEGGINGS); // New
                entries.add(FARMER_BOOTS);

                entries.add(PROSPECTOR_SHOVEL);
                entries.add(DIVINING_ROD);
                entries.add(PROSPECTOR_HAT);
                entries.add(PROSPECTOR_TUNIC);
                entries.add(PROSPECTOR_LEGGINGS); // New
                entries.add(PROSPECTOR_BOOTS); // New

                entries.add(SWORD_OF_MIDAS);
                entries.add(HOARDER_HELMET);
                entries.add(HOARDER_CHESTPLATE);
                entries.add(HOARDER_LEGGINGS);
                entries.add(HOARDER_BOOTS);

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
                entries.add(RESONANT_GREATSWORD);

                // Haloic Armor Set
                entries.add(HALOIC_HELMET);
                entries.add(HALOIC_CHESTPLATE);
                entries.add(HALOIC_LEGGINGS);
                entries.add(HALOIC_BOOTS);

                // Haloic Tools
                entries.add(HALOIC_SWORD);
                entries.add(HALOIC_PICKAXE);
                entries.add(HALOIC_AXE);
                entries.add(HALOIC_SHOVEL);
                entries.add(HALOIC_HOE);

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
                entries.add(SPELLBOOK_PAGE);
                entries.add(COPPER_WARHAMMER);
                entries.add(SOUL_SCYTHE);
                entries.add(SUMMONER_STAFF);
                entries.add(SOUL_JAR);

                // Special Items
                entries.add(SILVER_PENDANT);
                entries.add(OVERHEATED_FUEL_ROD);
                entries.add(FUEL_ROD);
                entries.add(KAMIKAZE_RITUAL_SCROLL);
        }

        private static void addItemsToFunctionalItemGroup(FabricItemGroupEntries entries) {
                // Grand Shulker Box (only the main one has an item - colored variants are
                // obtained through dyeing)
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
                entries.add(ModBlocks.TRANSMUTATION_PYRE_BLOCK);
                entries.add(ModBlocks.CAUTION_BLOCK);
                entries.add(ModBlocks.CAUTION_BLOCK_SLAB);
                entries.add(ModBlocks.CAUTION_BLOCK_STAIRS);
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

                // Iron Building Blocks
                entries.add(ModBlocks.IRON_STAIRS);
                entries.add(ModBlocks.IRON_SLAB);

                // Concrete Building Blocks
                entries.add(ModBlocks.WHITE_CONCRETE_STAIRS);
                entries.add(ModBlocks.WHITE_CONCRETE_SLAB);
                entries.add(ModBlocks.ORANGE_CONCRETE_STAIRS);
                entries.add(ModBlocks.ORANGE_CONCRETE_SLAB);
                entries.add(ModBlocks.MAGENTA_CONCRETE_STAIRS);
                entries.add(ModBlocks.MAGENTA_CONCRETE_SLAB);
                entries.add(ModBlocks.LIGHT_BLUE_CONCRETE_STAIRS);
                entries.add(ModBlocks.LIGHT_BLUE_CONCRETE_SLAB);
                entries.add(ModBlocks.YELLOW_CONCRETE_STAIRS);
                entries.add(ModBlocks.YELLOW_CONCRETE_SLAB);
                entries.add(ModBlocks.LIME_CONCRETE_STAIRS);
                entries.add(ModBlocks.LIME_CONCRETE_SLAB);
                entries.add(ModBlocks.PINK_CONCRETE_STAIRS);
                entries.add(ModBlocks.PINK_CONCRETE_SLAB);
                entries.add(ModBlocks.GRAY_CONCRETE_STAIRS);
                entries.add(ModBlocks.GRAY_CONCRETE_SLAB);
                entries.add(ModBlocks.LIGHT_GRAY_CONCRETE_STAIRS);
                entries.add(ModBlocks.LIGHT_GRAY_CONCRETE_SLAB);
                entries.add(ModBlocks.CYAN_CONCRETE_STAIRS);
                entries.add(ModBlocks.CYAN_CONCRETE_SLAB);
                entries.add(ModBlocks.PURPLE_CONCRETE_STAIRS);
                entries.add(ModBlocks.PURPLE_CONCRETE_SLAB);
                entries.add(ModBlocks.BLUE_CONCRETE_STAIRS);
                entries.add(ModBlocks.BLUE_CONCRETE_SLAB);
                entries.add(ModBlocks.BROWN_CONCRETE_STAIRS);
                entries.add(ModBlocks.BROWN_CONCRETE_SLAB);
                entries.add(ModBlocks.GREEN_CONCRETE_STAIRS);
                entries.add(ModBlocks.GREEN_CONCRETE_SLAB);
                entries.add(ModBlocks.RED_CONCRETE_STAIRS);
                entries.add(ModBlocks.RED_CONCRETE_SLAB);
                entries.add(ModBlocks.BLACK_CONCRETE_STAIRS);
                entries.add(ModBlocks.BLACK_CONCRETE_SLAB);

                // Ores
                entries.add(ModBlocks.SILVER_ORE);
                entries.add(ModBlocks.DEEPSLATE_SILVER_ORE);
                entries.add(ModBlocks.CHARCOAL_BLOCK);

                // Note: STRAWBERRY_CROP and LEMON_CROP don't have items - they're accessed
                // through seeds

                // Special Items
                entries.add(METAL_DETECTOR);
                entries.add(ALCHEMICAL_CATALYST);
                entries.add(RESONANT_CATALYST);
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
                entries.add(KEEP_INVENTORY_CHARM);
                entries.add(EMERGENCY_RECALL);
                entries.add(MANA_ROCKET);
                entries.add(DEEP_DARK_DEEP_DISH);
        }
}
