package net.willowins.animewitchery.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.ModBlocks;

public class ModItemGroups {
    public static final ItemGroup SILVER_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnimeWitchery.MOD_ID, "silver"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.silver"))
                    .icon(() -> new ItemStack(ModItems.SILVERSPOOL)).entries((displayContext, entries) -> {
                        entries.add(ModItems.SILVER);
                        entries.add(ModItems.SILVERNUGGET);
                        entries.add(ModItems.STAR_SHARD);
                        entries.add(ModItems.SILVERSPOOL);
                        entries.add(ModItems.SPOOL);
                        entries.add(ModItems.RAWSILVER);
                        entries.add(ModItems.WEATHERITEM);

                        entries.add(ModItems.LEMON);
                        entries.add(ModItems.STRAWBERRY);
                        entries.add(ModItems.TART_CRUST);
                        entries.add(ModItems.UNBAKED_LEMON_TART);
                        entries.add(ModItems.UNBAKED_STRAWBERRY_TART);
                        entries.add(ModItems.STRAWBERRY_TART);
                        entries.add(ModItems.LEMON_TART);
                        entries.add(ModItems.REPAIR_CHARM);
                        entries.add(ModItems.KINETIC_BLADE);

                        entries.add(ModItems.STRAWBERRY_SEEDS);
                        entries.add(ModItems.LEMON_SEEDS);
                        entries.add(ModItems.ROSEWILLOW_BLOSSOM);
                        // entries.add(ModBlocks.ROSEWILLOW_VINES_TIP); // Duplicate of
                        // ModItems.ROSEWILLOW_VINES
                        entries.add(ModBlocks.ROSEWILLOW_ROOTS);
                        entries.add(ModBlocks.ROSEWILLOW_BULB);
                        entries.add(ModItems.ROSEWILLOW_VINES);

                        entries.add(ModItems.NEEDLE);

                        entries.add(ModItems.SILVER_PICKAXE);

                        entries.add(ModItems.RAILGUNNER_HELMET);
                        entries.add(ModItems.RAILGUNNER_CHESTPLATE);
                        entries.add(ModItems.RAILGUNNER_LEGGINGS);
                        entries.add(ModItems.RAILGUNNER_BOOTS);

                        entries.add(ModItems.BLAZE_SACK);
                        entries.add(ModItems.ALCHEMICAL_CATALYST);
                        entries.add(ModItems.RESONANT_CATALYST);
                        entries.add(ModItems.VOID_ESSENCE);
                        entries.add(ModItems.RUNE_STONE);
                        entries.add(ModItems.BLOOD_RUNE_STONE);
                        entries.add(ModItems.KAMIKAZE_RITUAL_SCROLL);
                        entries.add(ModItems.DEEP_DARK_DEEP_DISH);
                        entries.add(ModItems.FIRE_RES_CRYSTAL);
                        entries.add(ModItems.SPEED_CRYSTAL);
                        entries.add(ModItems.MANA_ROCKET);
                        entries.add(ModItems.OVERHEATED_FUEL_ROD);
                        entries.add(ModItems.FUEL_ROD);

                        entries.add(ModBlocks.BINDING_SPELL);

                        entries.add(ModItems.SILVER_HELMET);
                        entries.add(ModItems.SILVER_CHESTPLATE);
                        entries.add(ModItems.SILVER_LEGGINGS);
                        entries.add(ModItems.SILVER_BOOTS);
                        entries.add(ModItems.SILVER_TEMPLATE);

                        entries.add(ModItems.RESONANT_HELMET);
                        entries.add(ModItems.RESONANT_CHESTPLATE);
                        entries.add(ModItems.RESONANT_LEGGINGS);
                        entries.add(ModItems.RESONANT_SPARK);
                        entries.add(ModBlocks.MONSTER_STATUE);
                        entries.add(ModItems.RESONANT_GREATSWORD);

                        entries.add(ModItems.METAL_DETECTOR);
                        entries.add(ModItems.RESPAWN_BEACON);
                        entries.add(ModItems.DIMENSION_HOPPER);
                        entries.add(ModItems.MOD_TOTEM);
                        entries.add(ModItems.DEFIANT_RIFT);
                        entries.add(ModItems.NBT_TOOL);
                        entries.add(ModItems.JOB_APPLICATION);

                        entries.add(ModBlocks.CHARCOAL_BLOCK);
                        entries.add(ModBlocks.SOUND_BLOCK);
                        entries.add(ModBlocks.SOUND_BLOCK2);
                        entries.add(ModBlocks.SILVER_BLOCK);
                        entries.add(ModBlocks.PARTICLE_BLOCK);
                        entries.add(ModBlocks.PARTICLE_SINK_BLOCK);
                        entries.add(ModBlocks.SILVER_ORE);
                        entries.add(ModBlocks.EFFIGY_FOUNTAIN);
                        entries.add(ModBlocks.DECORATIVE_FOUNTAIN);
                        entries.add(ModBlocks.GUARDIAN_STATUE);
                        entries.add(ModBlocks.PILLAR);
                        entries.add(ModBlocks.DEEPSLATE_SILVER_ORE);
                        entries.add(ModBlocks.DEEPSLATE_THRESHOLD);
                        entries.add(ModBlocks.ALCHEMY_TABLE);
                        entries.add(ModBlocks.ALCHEMICAL_ENCHANTER);
                        entries.add(ModBlocks.GACHA_ALTAR);
                        entries.add(ModBlocks.AUTO_CRAFTER_BLOCK);
                        entries.add(ModBlocks.INTERACTOR);
                        entries.add(ModBlocks.BLOCK_MINER);
                        entries.add(ModBlocks.BLOCK_PLACER);
                        entries.add(ModBlocks.LANDING_PLATFORM);
                        entries.add(ModBlocks.ENEMY_LANDING_PLATFORM);

                        entries.add(ModBlocks.SILVER_BUTTON);
                        entries.add(ModBlocks.SILVER_PRESSURE_PLATE);

                        entries.add(ModBlocks.SILVER_DOOR);
                        entries.add(ModBlocks.SILVER_TRAPDOOR);

                        entries.add(ModBlocks.SILVER_FENCE);
                        entries.add(ModBlocks.SILVER_FENCE_GATE);
                        entries.add(ModBlocks.SILVER_WALL);

                        entries.add(ModBlocks.SILVER_SLAB);
                        entries.add(ModBlocks.SILVER_STAIRS);

                        entries.add(ModBlocks.OBELISK);
                        entries.add(ModBlocks.GRAND_SHULKER_BOX);
                        entries.add(ModBlocks.PROTECTED_CHEST);
                        entries.add(ModItems.KEY);
                        entries.add(ModItems.MASTER_KEY);

                        entries.add(ModItems.SILVER_PENDANT);
                        entries.add(ModItems.HEALING_STAFF);
                        entries.add(ModItems.RAILGUN);

                        entries.add(ModItems.CHISEL);
                        entries.add(ModItems.MORTAR_AND_PESTLE);
                        entries.add(ModItems.CHALK);
                        entries.add(ModItems.MAGIC_CHALK);
                        entries.add(ModItems.ENCHANTED_CRYSTAL);
                        entries.add(ModItems.BONE_DUST);
                        entries.add(ModItems.AMETHYST_DUST);
                        entries.add(ModItems.FIRE_RUNE_STONE);
                        entries.add(ModItems.WATER_RUNE_STONE);
                        entries.add(ModItems.EARTH_RUNE_STONE);
                        entries.add(ModItems.AIR_RUNE_STONE);
                        entries.add(ModItems.LIFE_RUNE_STONE);
                        entries.add(ModItems.DEATH_RUNE_STONE);
                        entries.add(ModItems.LIGHT_RUNE_STONE);
                        entries.add(ModItems.DARKNESS_RUNE_STONE);
                        entries.add(ModItems.BARRIER_CATALYST);
                        entries.add(ModItems.REPAIR_ESSENCE);

                        entries.add(ModItems.MINOR_SKILL_ORB);
                        entries.add(ModItems.MAJOR_SKILL_ORB);
                        entries.add(ModItems.GRAND_SKILL_ORB);
                        entries.add(ModItems.ASCENDANT_SKILL_ORB);

                        entries.add(ModItems.OBELISK_HELMET);
                        entries.add(ModItems.OBELISK_CHESTPLATE);
                        entries.add(ModItems.OBELISK_LEGGINGS);
                        entries.add(ModItems.OBELISK_BOOTS);
                        entries.add(ModItems.OBELISK_SWORD);

                        entries.add(ModItems.SOUL_JAR);
                        entries.add(ModItems.SUMMONER_STAFF);
                        entries.add(ModItems.SOUL_SCYTHE);
                    }).build());

    public static final ItemGroup RPG_CLASSES = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnimeWitchery.MOD_ID, "rpg_classes"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.rpg_classes"))
                    .icon(() -> new ItemStack(ModItems.ASSASSIN_HOOD)).entries((displayContext, entries) -> {
                        // Assassin
                        entries.add(ModItems.ASSASSIN_DAGGER);
                        entries.add(ModItems.ASSASSIN_HOOD);
                        entries.add(ModItems.ASSASSIN_CHESTPLATE);
                        entries.add(ModItems.ASSASSIN_LEGGINGS);
                        entries.add(ModItems.ASSASSIN_BOOTS);

                        // Butcher
                        entries.add(ModItems.BUTCHER_KNIFE);
                        entries.add(ModItems.BUTCHER_HELMET);
                        entries.add(ModItems.BUTCHER_APRON);
                        entries.add(ModItems.BUTCHER_LEGGINGS);
                        entries.add(ModItems.BUTCHER_BOOTS);

                        // Brute
                        entries.add(ModItems.HEAVY_MACE);
                        entries.add(ModItems.HEAVY_PLATE_HELMET);
                        entries.add(ModItems.HEAVY_PLATE_CHESTPLATE);
                        entries.add(ModItems.HEAVY_PLATE_LEGGINGS);
                        entries.add(ModItems.HEAVY_PLATE_BOOTS);

                        // Monk
                        entries.add(ModItems.IRON_SHACKLE);
                        entries.add(ModItems.MONK_HEAD);
                        entries.add(ModItems.SPARSE_TUNIC);
                        entries.add(ModItems.MONK_LEGGINGS);
                        entries.add(ModItems.MONK_BOOTS);

                        // Healer
                        entries.add(ModItems.HEALING_STAFF);
                        entries.add(ModItems.SILVER_HELMET);
                        entries.add(ModItems.SILVER_CHESTPLATE);
                        entries.add(ModItems.SILVER_LEGGINGS);
                        entries.add(ModItems.SILVER_BOOTS);

                        // Sanguine
                        entries.add(ModItems.BLOODLETTER);
                        entries.add(ModItems.SANGUINE_HOOD);
                        entries.add(ModItems.HOODED_CAPE);
                        entries.add(ModItems.SANGUINE_LEGGINGS);
                        entries.add(ModItems.SANGUINE_BOOTS);

                        // Druid
                        entries.add(ModItems.NATURE_STAFF);
                        entries.add(ModItems.DRUID_HOOD);
                        entries.add(ModItems.COTTON_GOWN);
                        entries.add(ModItems.DRUID_LEGGINGS);
                        entries.add(ModItems.DRUID_BOOTS);

                        // Alchemist
                        entries.add(ModItems.POTION_FLASK);
                        entries.add(ModItems.ALCHEMIST_HOOD);
                        entries.add(ModItems.STAINED_ROBES);
                        entries.add(ModItems.ALCHEMIST_LEGGINGS);
                        entries.add(ModItems.ALCHEMIST_BOOTS);

                        // Gunslinger
                        entries.add(ModItems.BULLET);
                        entries.add(ModItems.REVOLVER);
                        entries.add(ModItems.COWBOY_HAT);
                        entries.add(ModItems.GUNSLINGER_VEST);
                        entries.add(ModItems.CHAPS);
                        entries.add(ModItems.GUNSLINGER_BOOTS);

                        // Railgunner
                        entries.add(ModItems.RAILGUN);
                        entries.add(ModItems.RAILGUNNER_HELMET);
                        entries.add(ModItems.RAILGUNNER_CHESTPLATE);
                        entries.add(ModItems.RAILGUNNER_LEGGINGS);
                        entries.add(ModItems.RAILGUNNER_BOOTS);

                        // Femboy
                        entries.add(ModItems.STUFFED_ANIMAL);
                        entries.add(ModItems.CAT_EARS);
                        entries.add(ModItems.FEMBOY_HOODIE);
                        entries.add(ModItems.THIGH_HIGHS);
                        entries.add(ModItems.PLATFORM_BOOTS);

                        // Gambler
                        entries.add(ModItems.DECK_OF_CARDS);
                        entries.add(ModItems.CHIPPED_DICE);
                        entries.add(ModItems.GAMBLER_VISOR);
                        entries.add(ModItems.SOILED_JACKET);
                        entries.add(ModItems.GAMBLER_PANTS);
                        entries.add(ModItems.GAMBLER_SHOES);

                        // Knight
                        entries.add(ModItems.LONGSWORD);
                        entries.add(ModItems.CROWN);
                        entries.add(ModItems.PLATE_CHESTPLATE);
                        entries.add(ModItems.PLATE_LEGGINGS);
                        entries.add(ModItems.PLATE_BOOTS);

                        // Paladin
                        entries.add(ModItems.GREAT_HAMMER);
                        entries.add(ModItems.PALADIN_HELMET);
                        entries.add(ModItems.PALADIN_CHESTPLATE);
                        entries.add(ModItems.PALADIN_LEGGINGS);
                        entries.add(ModItems.PALADIN_BOOTS);

                        // Deathbringer
                        entries.add(ModItems.SCYTHE);
                        entries.add(ModItems.DEATHBRINGER_HOOD);
                        entries.add(ModItems.DEATHBRINGER_ROBES);
                        entries.add(ModItems.DEATHBRINGER_LEGGINGS);
                        entries.add(ModItems.DEATHBRINGER_BOOTS);

                        // Summoner
                        entries.add(ModItems.SUMMONER_STAFF);
                        entries.add(ModItems.SUMMONER_HOOD);
                        entries.add(ModItems.SUMMONER_ROBES);
                        entries.add(ModItems.SUMMONER_LEGGINGS);
                        entries.add(ModItems.SUMMONER_BOOTS);

                        // Mage
                        entries.add(ModItems.MAGE_STAFF);
                        entries.add(ModItems.MAGE_HAT);
                        entries.add(ModItems.MAGE_ROBES);
                        entries.add(ModItems.MAGE_LEGGINGS);
                        entries.add(ModItems.MAGE_BOOTS);

                        // Farmer
                        entries.add(ModItems.FARMING_HOE);
                        entries.add(ModItems.STRAW_HAT);
                        entries.add(ModItems.FARMER_OVERALLS);
                        entries.add(ModItems.FARMER_LEGGINGS);
                        entries.add(ModItems.FARMER_BOOTS);

                        // Prospector
                        entries.add(ModItems.PROSPECTOR_SHOVEL);
                        entries.add(ModItems.DIVINING_ROD);
                        entries.add(ModItems.PROSPECTOR_HAT);
                        entries.add(ModItems.PROSPECTOR_TUNIC);
                        entries.add(ModItems.PROSPECTOR_LEGGINGS);
                        entries.add(ModItems.PROSPECTOR_BOOTS);

                        // Hoarder
                        entries.add(ModItems.SWORD_OF_MIDAS);
                        entries.add(ModItems.HOARDER_HELMET);
                        entries.add(ModItems.HOARDER_CHESTPLATE);
                        entries.add(ModItems.HOARDER_LEGGINGS);
                        entries.add(ModItems.HOARDER_BOOTS);
                    }).build());

    public static void registerItemGroups() {
        AnimeWitchery.LOGGER.info("registering Item Groups For " + AnimeWitchery.MOD_ID);
    }
}
