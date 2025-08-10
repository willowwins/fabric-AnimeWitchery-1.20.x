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
import net.willowins.animewitchery.item.custom.*;

public class ModItems {
    public static final Item SILVER =registerItem("silveringot", new Item(new FabricItemSettings()));
    public static final Item SILVER_TEMPLATE =registerItem("silver_template", new Item(new FabricItemSettings()));
    public static final Item STAFF_HEAD =registerItem("staff_head", new Item(new FabricItemSettings()));
    public static final Item BLAZE_SACK =registerItem("blaze_powder_bag", new Item(new FabricItemSettings()));
    public static final Item ALCHEMICAL_CATALYST =registerItem("alchemical_catalyst", new Item(new FabricItemSettings()));
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

    public static final Item NEEDLE =registerItem("needle",
            new NeedleItem(ModToolMaterial.SILVER,4,-2.2f,new FabricItemSettings().maxCount(1)));

    public static final Item SILVER_PICKAXE =registerItem("silver_pickaxe",
            new PickaxeItem(ModToolMaterial.SILVER,0, -3f,new FabricItemSettings()));

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


    public static final Item LEMON =registerItem("lemon", new Item(new FabricItemSettings().food(ModFoodComponents.LEMON)));

    public static final Item FIRE_RES_CRYSTAL =registerItem("fire_res_crystal",
            new FireResCrystalItem(new FabricItemSettings()));
 public static final Item SPEED_CRYSTAL =registerItem("speed_crystal",
            new SpeedCrystalItem(new FabricItemSettings()));

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

    // New alchemy and magic items
    public static final Item MORTAR_AND_PESTLE = registerItem("mortar_and_pestle", new Item(new FabricItemSettings()));
    public static final Item CHALK = registerItem("chalk", new NormalChalkItem(new FabricItemSettings()));
    public static final Item MAGIC_CHALK = registerItem("magic_chalk", new MagicChalkItem(new FabricItemSettings().rarity(Rarity.UNCOMMON)));
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
    public static final Item REPAIR_ESSENCE = registerItem("repair_essence", new Item(new FabricItemSettings().rarity(Rarity.RARE)));


    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries){
        entries.add(SILVER);
        entries.add(SILVERNUGGET);
        entries.add(RAWSILVER);
        entries.add(SPOOL);
        entries.add(SILVERSPOOL);
        
        // Add new alchemy and magic items
        entries.add(MORTAR_AND_PESTLE);
        entries.add(CHALK);
        entries.add(MAGIC_CHALK);
        entries.add(ENCHANTED_CRYSTAL);
        
        // Add dust items
        entries.add(BONE_DUST);
        entries.add(AMETHYST_DUST);
        
        // Add rune stones
        entries.add(FIRE_RUNE_STONE);
        entries.add(WATER_RUNE_STONE);
        entries.add(EARTH_RUNE_STONE);
        entries.add(AIR_RUNE_STONE);
        entries.add(LIFE_RUNE_STONE);
        entries.add(DEATH_RUNE_STONE);
        entries.add(LIGHT_RUNE_STONE);
        entries.add(DARKNESS_RUNE_STONE);
        
        // Add barrier items
        entries.add(BARRIER_CATALYST);
        entries.add(REPAIR_ESSENCE);
    }


    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(AnimeWitchery.MOD_ID, name ), item);

    }

    public static void registerModItems( ){
        AnimeWitchery.LOGGER.info("registering Mod Items for"+AnimeWitchery.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addItemsToCombatItemGroup);
    }
    
    private static void addItemsToCombatItemGroup(FabricItemGroupEntries entries) {
        entries.add(OBELISK_HELMET);
        entries.add(OBELISK_CHESTPLATE);
        entries.add(OBELISK_LEGGINGS);
        entries.add(OBELISK_BOOTS);
        entries.add(OBELISK_SWORD);
    }
}
