package net.willowins.animewitchery;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.ModItemGroups;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.sound.ModSounds;
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



		ModSounds.registerSounds();
		LOGGER.info("Hello Fabric world!");

		FuelRegistry.INSTANCE.add(ModBlocks.CHARCOAL_BLOCK, 16000);

		ModWorldGeneration.generateModWorldGen();
	}
}