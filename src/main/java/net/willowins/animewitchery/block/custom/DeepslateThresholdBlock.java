package net.willowins.animewitchery.block.custom;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.DeepslateThresholdBlockEntity;
import net.willowins.animewitchery.item.custom.CrystallineSpatialFoldItem;
import net.willowins.animewitchery.world.dimension.PocketManager;
import net.willowins.animewitchery.world.dimension.ModDimensions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DeepslateThresholdBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // VoxelShapes (North/Default)
    private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(
            Block.createCuboidShape(-8, 1, 12, -4, 32, 16),
            Block.createCuboidShape(-8, 0, 10, 24, 1, 16),
            Block.createCuboidShape(20, 1, 12, 24, 32, 16),
            Block.createCuboidShape(-8, 26.98, 11.99, 8.02, 32, 16.01),
            Block.createCuboidShape(8.99, 26.98, 11.99, 25.01, 32, 16.01),
            Block.createCuboidShape(8, 1, 12, 20, 31.3, 15),
            Block.createCuboidShape(-5.001, 27.898, 12.999, 20.001, 32, 16.001),
            Block.createCuboidShape(-4, 1, 12, 8, 31.3, 15),
            Block.createCuboidShape(-4, 1, 15, 20, 31, 16));

    private final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    public DeepslateThresholdBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
        runCalculation(SHAPE_NORTH);
    }

    private void runCalculation(VoxelShape north) {
        SHAPES.put(Direction.NORTH, north);
        VoxelShape east = rotate90(north);
        SHAPES.put(Direction.EAST, east);
        VoxelShape south = rotate90(east);
        SHAPES.put(Direction.SOUTH, south);
        VoxelShape west = rotate90(south);
        SHAPES.put(Direction.WEST, west);
    }

    private VoxelShape rotate90(VoxelShape shape) {
        VoxelShape newShape = VoxelShapes.empty();
        for (net.minecraft.util.math.Box box : shape.getBoundingBoxes()) {
            newShape = VoxelShapes.union(newShape, Block.createCuboidShape(
                    (1.0 - box.maxZ) * 16, box.minY * 16, box.minX * 16,
                    (1.0 - box.minZ) * 16, box.maxY * 16, box.maxX * 16));
        }
        return newShape;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DeepslateThresholdBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.block.entity.BlockEntityTicker<T> getTicker(World world,
            BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
        return checkType(type, net.willowins.animewitchery.block.entity.ModBlockEntities.DEEPSLATE_THRESHOLD_ENTITY,
                (world1, pos, state1, be) -> {
                    if (!world1.isClient && world1 instanceof ServerWorld serverWorld) {
                        // Scan for players inside the block's area
                        List<ServerPlayerEntity> players = serverWorld.getEntitiesByClass(ServerPlayerEntity.class,
                                new net.minecraft.util.math.Box(pos), p -> true);

                        for (ServerPlayerEntity player : players) {
                            // Check cooldown to avoid spam
                            if (!player.getItemCooldownManager().isCoolingDown(
                                    net.willowins.animewitchery.item.ModItems.CRYSTALLINE_SPATIAL_FOLD)) {
                                teleportPlayer(player, be);
                            }
                        }
                    }
                });
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof CrystallineSpatialFoldItem) {
            if (world.isClient)
                return ActionResult.SUCCESS;

            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DeepslateThresholdBlockEntity threshold) {
                boolean isPocket = world.getRegistryKey().equals(ModDimensions.POCKET_LEVEL_KEY);

                if (isPocket) {
                    // ==========================================
                    // EXIT MODE (Inside Pocket)
                    // Binds the "Return Location" (TargetPos)
                    // ==========================================

                    if (stack.hasNbt() && stack.getNbt().contains("LastPosX")) {
                        double x = stack.getNbt().getDouble("LastPosX");
                        double y = stack.getNbt().getDouble("LastPosY");
                        double z = stack.getNbt().getDouble("LastPosZ");
                        String dim = stack.getNbt().getString("LastDim");

                        threshold.setTargetLocation(BlockPos.ofFloored(x, y, z), dim);
                        threshold.setPocketUuid(null); // Clear entrance data to avoid confusion
                        threshold.setPocketId(-1);

                        player.sendMessage(Text.literal("Threshold (Exit) bound to Return Point: " + (int) x + ", "
                                + (int) y + ", " + (int) z + " [" + dim + "]").formatted(Formatting.GREEN), true);
                    } else {
                        player.sendMessage(
                                Text.literal("Spatial Fold has no stored location! Use it in the Overworld first.")
                                        .formatted(Formatting.RED),
                                true);
                    }

                } else {
                    // ==========================================
                    // ENTRANCE MODE (Overworld/Other)
                    // Binds the "Pocket ID" (Destination)
                    // ==========================================

                    if (stack.hasNbt() && stack.getNbt().contains("BoundPocketId")) {
                        int boundId = stack.getNbt().getInt("BoundPocketId");
                        PocketManager manager = PocketManager
                                .getServerState(world.getServer().getWorld(ModDimensions.POCKET_LEVEL_KEY));
                        UUID targetUuid = manager.getUuidForId(boundId);

                        if (targetUuid == null) {
                            targetUuid = manager.claimId(boundId); // Try claim legacy/lost
                        }

                        if (targetUuid != null) {
                            int finalId = manager.getPocketId(targetUuid);
                            threshold.setPocketUuid(targetUuid);
                            threshold.setPocketId(finalId);
                            // Clear Exit Data
                            threshold.setTargetLocation(null, null);

                            player.sendMessage(Text.literal("Threshold (Entrance) bound to Pocket ID: " + finalId)
                                    .formatted(Formatting.GREEN), true);
                        } else {
                            player.sendMessage(Text.literal("Invalid Pocket ID: " + boundId).formatted(Formatting.RED),
                                    true);
                        }
                    } else {
                        // Unbound Item -> Bind to Player's Personal Pocket
                        UUID playerUuid = player.getUuid();
                        PocketManager manager = PocketManager
                                .getServerState(world.getServer().getWorld(ModDimensions.POCKET_LEVEL_KEY));

                        if (!manager.hasPocket(playerUuid)) {
                            manager.addPocket(playerUuid);
                        }

                        int personalId = manager.getPocketId(playerUuid);
                        threshold.setPocketUuid(playerUuid);
                        threshold.setPocketId(personalId);
                        threshold.setTargetLocation(null, null);

                        player.sendMessage(
                                Text.literal("Threshold (Entrance) bound to Personal Pocket ID: " + personalId)
                                        .formatted(Formatting.GREEN),
                                true);
                    }
                }

                world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_END_PORTAL_FRAME_FILL,
                        net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DeepslateThresholdBlockEntity threshold) {
                teleportPlayer(player, threshold);
            }
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof ServerPlayerEntity player) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DeepslateThresholdBlockEntity threshold) {
                teleportPlayer(player, threshold);
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    private void teleportPlayer(ServerPlayerEntity player, DeepslateThresholdBlockEntity threshold) {
        if (player.getServer() == null)
            return;

        // Cooldown Check
        if (player.getItemCooldownManager()
                .isCoolingDown(net.willowins.animewitchery.item.ModItems.CRYSTALLINE_SPATIAL_FOLD)) {
            return;
        }

        ServerWorld currentWorld = player.getServerWorld();
        boolean isCurrentlyInPocket = currentWorld.getRegistryKey().equals(ModDimensions.POCKET_LEVEL_KEY);

        if (isCurrentlyInPocket) {
            // =============================================================
            // EXIT LOGIC: Pocket -> Overworld (or Bound Dimension)
            // =============================================================

            BlockPos targetPos = threshold.getTargetPos();
            String targetDimId = threshold.getTargetDim();

            ServerWorld targetWorld = null;
            if (targetDimId != null) {
                RegistryKey<World> dimKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(targetDimId));
                targetWorld = player.getServer().getWorld(dimKey);
            }

            // Fallback to Overworld if dimension is missing
            if (targetWorld == null) {
                targetWorld = player.getServer().getOverworld();
            }

            // Fallback to World Spawn if position is missing
            double destX, destY, destZ;
            if (targetPos != null) {
                destX = targetPos.getX() + 0.5;
                destY = targetPos.getY();
                destZ = targetPos.getZ() + 0.5;
            } else {
                BlockPos spawn = targetWorld.getSpawnPos();
                destX = spawn.getX() + 0.5;
                destY = spawn.getY();
                destZ = spawn.getZ() + 0.5;
                player.sendMessage(
                        Text.literal("Threshold has no return point! Warping to Spawn.").formatted(Formatting.RED),
                        true);
            }

            // Execute Teleport
            player.sendMessage(Text.literal("Exiting Pocket Dimension...").formatted(Formatting.AQUA), true);

            net.fabricmc.fabric.api.dimension.v1.FabricDimensions.teleport(player, targetWorld,
                    new net.minecraft.world.TeleportTarget(
                            new net.minecraft.util.math.Vec3d(destX, destY + 0.1, destZ),
                            new net.minecraft.util.math.Vec3d(0, 0, 0),
                            player.getYaw(),
                            player.getPitch()));

        } else {
            // =============================================================
            // ENTRANCE LOGIC: Overworld -> Pocket
            // =============================================================

            UUID pocketUuid = threshold.getPocketUuid();
            if (pocketUuid == null) {
                player.sendMessage(Text.literal("Threshold is unbound! Use a bound Spatial Fold to activate it.")
                        .formatted(Formatting.RED), true);
                return;
            }

            ServerWorld pocketWorld = player.getServer().getWorld(ModDimensions.POCKET_LEVEL_KEY);
            if (pocketWorld == null)
                return; // Should never happen unless mod is broken

            PocketManager manager = PocketManager.getServerState(pocketWorld);

            // Ensure Pocket Exists
            if (!manager.hasPocket(pocketUuid)) {
                manager.addPocket(pocketUuid);
            }

            // Resolve ID
            int pocketId = threshold.getPocketId();
            if (pocketId < 0) {
                pocketId = manager.getPocketId(pocketUuid);
                threshold.setPocketId(pocketId);
            }

            // Calculate Destination
            // 1. Check for valid Legacy/Custom Spawn
            BlockPos customSpawn = manager.getLegacyCoord(pocketUuid);

            BlockPos destPos;
            if (customSpawn != null) {
                destPos = customSpawn;
            } else {
                // 2. Default to Grid Center
                destPos = PocketManager.getPocketCenter(pocketId);
            }

            System.out.println("DeepslateThreshold DEBUG: Entrance Logic Triggered");
            System.out.println(" - Current Pos: " + player.getBlockPos().toShortString());
            System.out.println(" - Pocket UUID: " + pocketUuid);
            System.out.println(" - Resolved Pocket ID: " + pocketId);
            System.out.println(" - Target Custom Spawn: " + customSpawn);
            System.out.println(" - Calculated Dest Pos: " + destPos.toShortString());

            // 3. Ensure Platform Exists
            PocketManager.ensureSpawnPlatform(pocketWorld, destPos);

            // Refine destination to land ON the platform if available
            BlockPos platformPos = PocketManager.findNearestPlatform(pocketWorld, destPos, 10);
            if (platformPos != null) {
                destPos = platformPos.up();
            } else {
                // If using custom spawn or default, make sure we aren't inside the floor if
                // it's just a raw coord
                // (Existing logic didn't explicitly .up() for destPos, so I'll check if we
                // should)
                // Actually, let's keep it safe. If no platform found, we trust destPos from
                // logic above.
                // But wait, ensureSpawnPlatform places it at Y=63 (if center is Y=64), so Y=64
                // is correct for standing.
                // If destPos came from 'getPocketCenter' it is Y=64.
                // If destPos came from 'customSpawn', it is whatever was set.

                // If it was center, ensureSpawnPlatform ensures a floor at destPos.down().
                // So destPos is correct standing spot.
            }

            // Execute Teleport
            player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.TitleS2CPacket(
                    Text.literal("Entering Pocket").formatted(Formatting.LIGHT_PURPLE)));
            System.out.println(
                    " - Executing Teleport to: " + destPos.getX() + ", " + destPos.getY() + ", " + destPos.getZ());

            // Use Fabric API for safer dimension travel if available, or robust vanilla
            // fallback
            net.fabricmc.fabric.api.dimension.v1.FabricDimensions.teleport(player, pocketWorld,
                    new net.minecraft.world.TeleportTarget(
                            new net.minecraft.util.math.Vec3d(destPos.getX() + 0.5, destPos.getY() + 0.1,
                                    destPos.getZ() + 0.5),
                            new net.minecraft.util.math.Vec3d(0, 0, 0),
                            player.getYaw(),
                            player.getPitch()));
        }

        // Trigger Cooldown
        player.getItemCooldownManager().set(net.willowins.animewitchery.item.ModItems.CRYSTALLINE_SPATIAL_FOLD, 100);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }
}
