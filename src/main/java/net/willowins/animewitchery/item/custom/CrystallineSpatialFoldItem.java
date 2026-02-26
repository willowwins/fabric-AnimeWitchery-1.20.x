package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.world.dimension.ModDimensions;
import net.willowins.animewitchery.world.dimension.PocketManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class CrystallineSpatialFoldItem extends Item {
    public CrystallineSpatialFoldItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        if (world.getBlockState(pos).getBlock() == net.minecraft.block.Blocks.LODESTONE) {
            if (context.getPlayer().isSneaking()) {
                if (!world.isClient) {
                    // Bind to this pocket
                    int pocketId = PocketManager.getPocketIdFromPos(pos);

                    NbtCompound nbt = context.getStack().getOrCreateNbt();
                    nbt.putInt("BoundPocketId", pocketId);
                    // Also set InstanceUUID so it looks "linked" generally if desired, or skip
                    if (!nbt.containsUuid("InstanceUUID")) {
                        nbt.putUuid("InstanceUUID", UUID.randomUUID());
                    }

                    context.getPlayer().sendMessage(
                            Text.literal("Bound to Pocket ID: " + pocketId).formatted(Formatting.GREEN), true);
                    world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_LODESTONE_COMPASS_LOCK,
                            net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);
                }

                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // Check explicit offhand copy
        ItemStack offhand = user.getStackInHand(Hand.OFF_HAND);
        if (!world.isClient && offhand.getItem() == this && !offhand.hasNbt()) {
            if (stack.hasNbt() && stack.getNbt().contains("BoundPocketId")) {
                NbtCompound nbt = offhand.getOrCreateNbt();
                nbt.putInt("BoundPocketId", stack.getNbt().getInt("BoundPocketId"));
                if (stack.getNbt().containsUuid("InstanceUUID")) {
                    nbt.putUuid("InstanceUUID", stack.getNbt().getUuid("InstanceUUID"));
                }
                user.sendMessage(Text.literal("Copied Crystal Binding").formatted(Formatting.GREEN), true);
                return TypedActionResult.success(stack);
            }
        }

        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            MinecraftServer server = world.getServer();
            ServerWorld pocketWorld = server.getWorld(ModDimensions.POCKET_LEVEL_KEY);

            if (pocketWorld == null)
                return TypedActionResult.fail(stack);

            // Save current pos (Return Point) if not in pocket
            if (!world.getRegistryKey().equals(ModDimensions.POCKET_LEVEL_KEY)) {
                NbtCompound nbt = stack.getOrCreateNbt();
                nbt.putDouble("LastPosX", user.getX());
                nbt.putDouble("LastPosY", user.getY());
                nbt.putDouble("LastPosZ", user.getZ());
                nbt.putString("LastDim", world.getRegistryKey().getValue().toString());
                System.out.println(
                        "Saved Return Point to Item: " + user.getX() + ", " + user.getY() + ", " + user.getZ());
            }

            // Check for bound pocket
            Integer boundId = null;
            if (stack.hasNbt() && stack.getNbt().contains("BoundPocketId")) {
                boundId = stack.getNbt().getInt("BoundPocketId");
            }

            if (world.getRegistryKey().equals(ModDimensions.POCKET_LEVEL_KEY)) {
                // Leaving pocket
                if (stack.getNbt() != null && stack.getNbt().contains("LastPosX")) {
                    double destX = stack.getNbt().getDouble("LastPosX");
                    double destY = stack.getNbt().getDouble("LastPosY");
                    double destZ = stack.getNbt().getDouble("LastPosZ");
                    String dimId = stack.getNbt().getString("LastDim");

                    ServerWorld destWorld = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(dimId)));
                    if (destWorld == null)
                        destWorld = server.getOverworld();

                    player.teleport(destWorld, destX, destY, destZ, player.getYaw(), player.getPitch());
                    playWarpSound(destWorld, new BlockPos((int) destX, (int) destY, (int) destZ));
                } else {
                    ServerWorld overworld = server.getOverworld();
                    BlockPos spawn = overworld.getSpawnPos();
                    player.teleport(overworld, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);
                    playWarpSound(overworld, spawn);
                }
            } else {
                // Entering pocket
                BlockPos spawnPos;

                if (boundId != null) {
                    // Teleport to BOUND pocket
                    spawnPos = PocketManager.getPocketCenter(boundId);

                    // Check custom spawn
                    PocketManager manager = PocketManager.getServerState(pocketWorld);
                    BlockPos custom = manager.getCustomSpawn(boundId);
                    if (custom != null)
                        spawnPos = custom;
                    player.sendMessage(Text.literal("Warping to Bound Pocket: " + boundId).formatted(Formatting.AQUA),
                            true);
                } else {
                    // Unbound - Auto-Bind to NEW Pocket ID
                    PocketManager manager = PocketManager.getServerState(pocketWorld);
                    UUID newUuid = UUID.randomUUID();
                    manager.addPocket(newUuid);

                    int newId = manager.getPocketId(newUuid);
                    stack.getOrCreateNbt().putInt("BoundPocketId", newId);
                    stack.getOrCreateNbt().putUuid("InstanceUUID", newUuid);

                    player.sendMessage(
                            Text.literal("Crystal Bound to New Pocket ID: " + newId).formatted(Formatting.AQUA), true);

                    spawnPos = PocketManager.getPocketCenter(newId);
                }

                // Ensure safe spawn platform
                PocketManager.ensureSpawnPlatform(pocketWorld, spawnPos);

                // Check for specific Landing Platform block logic (prioritize landing on the
                // block)
                BlockPos platformPos = PocketManager.findNearestPlatform(pocketWorld, spawnPos, 10);
                if (platformPos != null) {
                    spawnPos = platformPos.up();
                } else {
                    spawnPos = spawnPos.up(); // Default behavior (center + 1)
                }

                player.teleport(pocketWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                playWarpSound(world, user.getBlockPos()); // Added sound

                // Sync World Border
                // PocketManager.sendBorderPacket(player, currentId);
            }

            user.getItemCooldownManager().set(this, 20);
            return TypedActionResult.success(stack);
        }

        return TypedActionResult.success(stack);
    }

    private void playWarpSound(World world, BlockPos pos) {
        world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_PORTAL_TRAVEL,
                net.minecraft.sound.SoundCategory.PLAYERS, 0.5f, 1.0f);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt() && stack.getNbt().contains("BoundPocketId")) {
            int id = stack.getNbt().getInt("BoundPocketId");
            tooltip.add(Text.literal("Pocket ID: " + id).formatted(Formatting.AQUA));
        } else {
            tooltip.add(Text.literal("Unbound").formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
