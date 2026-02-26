package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.world.dimension.ModDimensions;
import net.willowins.animewitchery.world.dimension.PocketManager;
import net.willowins.animewitchery.mana.ModComponents;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.world.TeleportTarget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DomainSparkItem extends Item {
    public DomainSparkItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            // Must have a bound pocket ID
            if (!stack.hasNbt() || !stack.getNbt().contains("BoundPocketId")) {
                user.sendMessage(
                        Text.literal("Spark is unbound. Smith with a bound Spatial Fold.").formatted(Formatting.RED),
                        true);
                return TypedActionResult.fail(stack);
            }

            // Check Mana Cost
            var manaComponent = ModComponents.PLAYER_MANA.get(user);
            if (manaComponent.getMana() < 500) {
                user.sendMessage(Text.literal("Not enough Mana! (Need 500)").formatted(Formatting.RED), true);
                return TypedActionResult.fail(stack);
            }

            int pocketId = stack.getNbt().getInt("BoundPocketId");
            MinecraftServer server = world.getServer();
            ServerWorld pocketWorld = server.getWorld(ModDimensions.POCKET_LEVEL_KEY);

            if (pocketWorld == null)
                return TypedActionResult.fail(stack);

            // Consume Mana
            manaComponent.consume(500);

            // 1. Teleport Caster to Landing Platform
            BlockPos centerPos = PocketManager.getPocketCenter(pocketId);
            PocketManager.ensureSpawnPlatform(pocketWorld, centerPos); // Ensure it exists

            // 2. Teleport Nearby Players and Husks (for testing) to Enemy Landing Platforms
            List<net.minecraft.entity.LivingEntity> nearbyEntities = new ArrayList<>();

            // Add Players
            nearbyEntities.addAll(world.getEntitiesByClass(ServerPlayerEntity.class,
                    new Box(user.getBlockPos()).expand(10),
                    p -> p != user && !p.isSpectator()));

            if (!nearbyEntities.isEmpty()) {
                List<BlockPos> enemyPlatforms = findEnemyPlatforms(pocketWorld, centerPos, 50); // Scan radius 50

                int platformIndex = 0;
                for (net.minecraft.entity.LivingEntity target : nearbyEntities) {
                    BlockPos targetPos;

                    if (!enemyPlatforms.isEmpty()) {
                        // Distribute among available platforms
                        targetPos = enemyPlatforms.get(platformIndex % enemyPlatforms.size()).up();
                        platformIndex++;
                    } else {
                        // Fallback: Teleport to/near center if no enemy platforms
                        targetPos = centerPos.up();
                    }

                    teleportEntity(target, pocketWorld, targetPos);
                    if (target instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.sendMessage(
                                Text.literal("You have been pulled into a Domain!").formatted(Formatting.RED),
                                true);
                    }
                }
            }

            // 3. Teleport Caster to Landing Platform
            // Find specific Landing Platform block for caster
            BlockPos platformPos = PocketManager.findNearestPlatform(pocketWorld, centerPos, 10);
            BlockPos casterTarget = centerPos.up();
            if (platformPos != null) {
                casterTarget = platformPos.up();
            }

            // Teleport Caster
            teleportEntity(player, pocketWorld, casterTarget);

            user.getItemCooldownManager().set(this, 100); // 5 second cooldown
        }

        return TypedActionResult.success(stack);
    }

    private void teleportEntity(net.minecraft.entity.Entity entity, ServerWorld targetWorld, BlockPos pos) {
        // Calculate target parameters
        double destX = pos.getX() + 0.5;
        double destY = pos.getY();
        double destZ = pos.getZ() + 0.5;
        float yaw = entity.getYaw();
        float pitch = entity.getPitch();

        // Use FabricDimensions for robust teleportation (handles both players and
        // entities)
        net.fabricmc.fabric.api.dimension.v1.FabricDimensions.teleport(
                entity,
                targetWorld,
                new net.minecraft.world.TeleportTarget(
                        new net.minecraft.util.math.Vec3d(destX, destY, destZ),
                        net.minecraft.util.math.Vec3d.ZERO,
                        yaw,
                        pitch));

        targetWorld.playSound(null, pos, net.minecraft.sound.SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private List<BlockPos> findEnemyPlatforms(ServerWorld world, BlockPos center, int radius) {
        List<BlockPos> platforms = new ArrayList<>();

        BlockPos min = center.add(-radius, -14, -radius); // roughly Y=50
        BlockPos max = center.add(radius, 36, radius); // roughly Y=100

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            if (world.getBlockState(pos).isOf(ModBlocks.ENEMY_LANDING_PLATFORM)) {
                platforms.add(pos.toImmutable());
            }
        }
        return platforms;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt() && stack.getNbt().contains("BoundPocketId")) {
            int id = stack.getNbt().getInt("BoundPocketId");
            tooltip.add(Text.literal("Pocket ID: " + id).formatted(Formatting.DARK_PURPLE));
        } else {
            tooltip.add(Text.literal("Unbound").formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
