package net.willowins.animewitchery;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.custom.EffigyFountainBlock;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.enchantments.ModEnchantments;
import net.willowins.animewitchery.entity.ModEntities;
import net.willowins.animewitchery.potion.ModBrewingRecipes;
import net.willowins.animewitchery.potion.ModPotions;
import net.willowins.animewitchery.entity.VoidWispEntity;
import net.willowins.animewitchery.events.*;
import net.willowins.animewitchery.item.ModItemGroups;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.item.custom.ObeliskSwordItem;
import net.willowins.animewitchery.mana.ManaComponent;
import net.willowins.animewitchery.mana.ManaStorageRegistry;
import net.willowins.animewitchery.mana.ManaTicker;
import net.willowins.animewitchery.particle.ModParticles;
import net.willowins.animewitchery.sound.ModSounds;
import net.willowins.animewitchery.util.ModCustomTrades;
import net.willowins.animewitchery.util.ModExplosionManager;
import net.willowins.animewitchery.util.ModLootTableModifiers;
import net.willowins.animewitchery.util.ServerScheduler;
import net.willowins.animewitchery.villager.ModVillagers;
import net.willowins.animewitchery.recipe.ModRecipes;
import net.willowins.animewitchery.world.ObeliskWorldListener;
import net.willowins.animewitchery.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vazkii.patchouli.api.PatchouliAPI;

public class AnimeWitchery implements ModInitializer {
	public static final String MOD_ID = "animewitchery";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier SWING_MISS_PACKET = new Identifier(MOD_ID, "swing_miss");

	public class AnimeWitcheryComponents implements EntityComponentInitializer {
		public static final ComponentKey<ManaComponent> MANA = ComponentRegistryV3.INSTANCE.getOrCreate(
				new Identifier("animewitchery", "mana"),
				ManaComponent.class);

		@Override
		public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
			registry.registerFor(
					PlayerEntity.class,
					MANA,
					player -> new ManaComponent(player));

		}
	}

	@Override
	public void onInitialize() {

		ModBlocks.registerModBlocks();
		ModItems.registerModItems();

		// Register custom game rules
		net.willowins.animewitchery.util.ModGameRules.register();

		ManaStorageRegistry.register(ModItems.RESONANT_CATALYST);
		ManaStorageRegistry.register(ModItems.ALCHEMICAL_CATALYST);

		ServerTickEvents.END_WORLD_TICK.register(ServerScheduler::tick);

		ObeliskWorldListener.register();

		// Register spellbook packet receivers
		net.willowins.animewitchery.networking.SpellbookPackets.registerServerReceivers();

		// Register C2S packets (Client-to-Server)
		net.willowins.animewitchery.networking.ModPackets.registerC2SPackets();

		ModItemGroups.registerItemGroups();

		ModScreenHandlers.registerAll();

		// Register Patchouli book
		PatchouliAPI.get().setConfigFlag("animewitchery:rituals", true);

		// Register book interaction handler
		net.willowins.animewitchery.util.BookInteractionHandler.register();

		ManaTicker.register();

		ModLootTableModifiers.modifyLootTables();
		ModCustomTrades.registerCustomTrades();

		ModVillagers.registerVillagers();
		ModSounds.registerSounds();

		ModBlockEntities.registerBlockEntities();

		ModEnchantments.init();

		ModRecipes.registerRecipes();

		ModParticles.registerParticles();

		ModEffect.registerEffects();

		ModPotions.registerPotions();
		ModBrewingRecipes.registerBrewingRecipes();

		ModEntities.registerModEntities();

		// Register entity attributes
		FabricDefaultAttributeRegistry.register(ModEntities.VOID_WISP, VoidWispEntity.createVoidWispAttributes());

		// Register void wisp spawning
		net.willowins.animewitchery.world.spawn.VoidWispSpawnHandler.registerSpawns();

		ExcavationBreakHandler.register();
		BarrierBlockProtectionHandler.register();

		ExpBoostHandler.register();

		BlastingBreakHandler.register();

		ChestplateElytraFlight.register();

		NetherrackTransformHandler.register();

		SpawnerNbtHandler.register();

		net.willowins.animewitchery.events.SoulRecoveryHandler.register();
		net.willowins.animewitchery.events.SoulJarInteractionHandler.register();
		net.willowins.animewitchery.events.MobEquipmentHandler.register();

		LOGGER.info("Hello Fabric world!");

		FuelRegistry.INSTANCE.add(ModBlocks.CHARCOAL_BLOCK, 16000);

		// Initialize world generation (ore generation)
		try {
			ModWorldGeneration.generateModWorldGen();
			LOGGER.info("✅ World generation initialized successfully");
		} catch (Exception e) {
			LOGGER.error("❌ Failed to initialize world generation: " + e.getMessage());
			e.printStackTrace();
		}
		// Register explosion absorber hook
		BarrierExplosionHandler.register();

		// Register barrier interaction handler (allows unauthorized players to interact
		// with barrier circles)
		BarrierInteractionHandler.register();

		// Register barrier collision handler (prevents unauthorized entry)
		// BarrierCollisionHandler.register();

		// Register barrier protection handler (prevents unauthorized block
		// breaking/interaction)
		BarrierProtectionHandler.register();

		// Register spellbook enchanting table handlers
		SpellbookEnchantingHandler.register();
		SpellbookInteractionHandler.register();

		ModExplosionManager.init();

		CustomPortalBuilder.beginPortal()
				.frameBlock(Blocks.REINFORCED_DEEPSLATE)
				.lightWithItem(ModItems.NEEDLE)
				.destDimID(new Identifier(AnimeWitchery.MOD_ID, "paradiselostdim"))
				.returnDim(new Identifier("minecraft", "overworld"), false)
				.tintColor(0x94ecff)
				.registerPortal();

		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
			if (EffigyFountainBlock.active) {
				EffigyFountainBlock.ticks++;
				// Fountain now acts infinitely - deactivation disabled
				/*
				 * if (EffigyFountainBlock.ticks == 20*36000) {
				 * World world = EffigyFountainBlock.effigyworld;
				 * BlockPos blockPos = EffigyFountainBlock.lastEffigyPos;
				 * world.setBlockState(blockPos, ModBlocks.EFFIGY_FOUNTAIN.getDefaultState());
				 * EffigyFountainBlock.active = false;
				 * }
				 */
			}
		});

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
			if (!world.isClient && player.getStackInHand(hand).getItem() instanceof ObeliskSwordItem item) {
				item.playSwing((ServerPlayerEntity) player, player.getStackInHand(hand));
			}
			return ActionResult.PASS;
		});

		// Left-click BLOCK
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			if (!world.isClient && player.getStackInHand(hand).getItem() instanceof ObeliskSwordItem item) {
				item.playSwing((ServerPlayerEntity) player, player.getStackInHand(hand));
			}
			return ActionResult.PASS;
		});

		// Left-click AIR (MISS) — packet from client
		ServerPlayNetworking.registerGlobalReceiver(SWING_MISS_PACKET, (server, player, handler, buf, sender) -> {
			Hand hand = buf.readEnumConstant(Hand.class);
			server.execute(() -> {
				if (player.getStackInHand(hand).getItem() instanceof ObeliskSwordItem item) {
					item.playSwing((ServerPlayerEntity) player, player.getStackInHand(hand));
				}
			});
		});

	}
}
