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
import net.minecraft.client.item.ModelPredicateProviderRegistry;
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

public class AnimeWitchery implements ModInitializer {
	public static final String MOD_ID = "animewitchery";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier SWING_MISS_PACKET = new Identifier(MOD_ID, "swing_miss");
	public class AnimeWitcheryComponents implements EntityComponentInitializer {
		public static final ComponentKey<ManaComponent> MANA =
				ComponentRegistryV3.INSTANCE.getOrCreate(
						new Identifier("animewitchery", "mana"),
						ManaComponent.class
				);

		@Override
		public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
			registry.registerFor(
					PlayerEntity.class,
					MANA,
					player -> new ManaComponent(player)
			);

		}}

	@Override
	public void onInitialize() {

		ManaStorageRegistry.register(ModItems.RESONANT_CATALYST);
		ManaStorageRegistry.register(ModItems.ALCHEMICAL_CATALYST);

		ServerTickEvents.END_WORLD_TICK.register(ServerScheduler::tick);

		ObeliskWorldListener.register();

		ModItemGroups.registerItemGroups();

		ModScreenHandlers.registerAll();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

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

		        ModEntities.registerModEntities();

        // Register entity attributes
        FabricDefaultAttributeRegistry.register(ModEntities.VOID_WISP, VoidWispEntity.createVoidWispAttributes());

        ExcavationBreakHandler.register();

		ExpBoostHandler.register();




		BlastingBreakHandler.register();

		ChestplateElytraFlight.register();

		LOGGER.info("Hello Fabric world!");

		FuelRegistry.INSTANCE.add(ModBlocks.CHARCOAL_BLOCK, 16000);

		ModWorldGeneration.generateModWorldGen();
		// Register explosion absorber hook
		BarrierExplosionHandler.register();

		ModExplosionManager.init();

		CustomPortalBuilder.beginPortal()
				.frameBlock(Blocks.REINFORCED_DEEPSLATE)
				.lightWithItem(ModItems.NEEDLE)
				.destDimID(new Identifier(AnimeWitchery.MOD_ID, "paradiselostdim"))
				.tintColor(0x94ecff)
				.forcedSize(3, 3)
				.registerPortal();

		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
			if (EffigyFountainBlock.active) {
				EffigyFountainBlock.ticks++;
				if (EffigyFountainBlock.ticks == 20*36000) {
					World world = EffigyFountainBlock.effigyworld;
					BlockPos blockPos = EffigyFountainBlock.lastEffigyPos;
					world.setBlockState(blockPos, ModBlocks.EFFIGY_FOUNTAIN.getDefaultState());
					EffigyFountainBlock.active = false;
				}
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
		ModelPredicateProviderRegistry.register(ModItems.OBELISK_COMPASS, new Identifier("angle"),
				(stack, world, entity, seed) -> {
					if (stack.hasNbt()) {
						return stack.getNbt().getFloat("ObeliskCompassAngle") / 360f;
					}
					return 0f;
				});

	}
}

