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
            new Identifier(AnimeWitchery.MOD_ID,"silver"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.silver"))
                    .icon(() -> new ItemStack(ModItems.SILVERSPOOL)).entries((displayContext, entries) -> {
                        entries.add(ModItems.SILVER);
                        entries.add(ModItems.SILVERNUGGET);
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
                        entries.add(ModBlocks.ALCHEMY_TABLE);
                        entries.add(ModBlocks.AUTO_CRAFTER_BLOCK);
                        entries.add(ModBlocks.INTERACTOR);
                        entries.add(ModBlocks.BLOCK_MINER);
                        entries.add(ModBlocks.BLOCK_PLACER);

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

                        entries.add(ModItems.OBELISK_HELMET);
                        entries.add(ModItems.OBELISK_CHESTPLATE);
                        entries.add(ModItems.OBELISK_LEGGINGS);
                        entries.add(ModItems.OBELISK_BOOTS);
                        entries.add(ModItems.OBELISK_SWORD);
                     } ).build());

    public static void registerItemGroups(){
        AnimeWitchery.LOGGER.info("registering Item Groups For " +AnimeWitchery.MOD_ID);
    }
}
