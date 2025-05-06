package net.willowins.animewitchery;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.item.ModItemGroups;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.sound.ModSounds;
import net.willowins.animewitchery.util.ModCustomTrades;
import net.willowins.animewitchery.util.ModLootTableModifiers;
import net.willowins.animewitchery.villager.ModVillagers;
import net.willowins.animewitchery.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimeWitchery implements ModInitializer {
	public static final String MOD_ID = "animewitchery";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ModLootTableModifiers.modifyLootTables();
		ModCustomTrades.registerCustomTrades();

		ModVillagers.registerVillagers();
		ModSounds.registerSounds();

		ModBlockEntities.registerBlockEntities();

		LOGGER.info("Hello Fabric world!");

		FuelRegistry.INSTANCE.add(ModBlocks.CHARCOAL_BLOCK, 16000);

		ModWorldGeneration.generateModWorldGen();



		CustomPortalBuilder.beginPortal()
				.frameBlock(Blocks.REINFORCED_DEEPSLATE)
				.lightWithItem(ModItems.NEEDLE)
				.destDimID(new Identifier(AnimeWitchery.MOD_ID, "paradiselostdim"))
				.tintColor(0x94ecff)
				.forcedSize(3 , 3)
				.registerPortal();
	}
}