package net.willowins.animewitchery.world.dimension;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import net.willowins.animewitchery.AnimeWitchery;
import net.minecraft.util.math.BlockPos;

public class PocketManager extends PersistentState {
    public static final int POCKET_SPACING = 1000;
    public static final int GRID_WIDTH = 10;

    private final List<UUID> pocketDims = new ArrayList<>();
    private final Map<UUID, Integer> legacyAllocations = new HashMap<>();
    private final Map<Integer, BlockPos> legacyCustomSpawns = new HashMap<>();

    public void addPocket(UUID uuid) {
        if (legacyAllocations.containsKey(uuid)) {
            return; // Already legacy
        }
        if (!pocketDims.contains(uuid)) {
            pocketDims.add(uuid);
            markDirty();
        }
    }

    public boolean hasPocket(UUID uuid) {
        return pocketDims.contains(uuid) || legacyAllocations.containsKey(uuid);
    }

    public int getPocketId(UUID uuid) {
        if (legacyAllocations.containsKey(uuid)) {
            return legacyAllocations.get(uuid);
        }
        return pocketDims.indexOf(uuid);
    }

    public UUID getUuidForId(int id) {
        // Check legacy first
        for (Map.Entry<UUID, Integer> entry : legacyAllocations.entrySet()) {
            if (entry.getValue() == id)
                return entry.getKey();
        }
        // Check dynamic list
        if (id >= 0 && id < pocketDims.size()) {
            UUID u = pocketDims.get(id);
            // Verify this UUID is NOT legacy (or if it is, it must match 'id')
            if (legacyAllocations.containsKey(u)) {
                if (legacyAllocations.get(u) != id) {
                    // Conflict! This slot holds a UUID that belongs to another ID.
                    return null;
                }
            }
            return u;
        }
        return null;
    }

    public UUID claimId(int id) {
        if (id < 0)
            return null;
        UUID existing = getUuidForId(id);
        if (existing != null)
            return existing;

        // Fill gaps in dynamic list
        while (pocketDims.size() <= id) {
            UUID newUuid = UUID.randomUUID();
            pocketDims.add(newUuid);
            markDirty();
        }

        // Handle corrupted slot (where getUuidForId returned null but slot exists)
        UUID current = pocketDims.get(id);
        if (legacyAllocations.containsKey(current) && legacyAllocations.get(current) != id) {
            // Overwrite corrupted slot
            UUID newUuid = UUID.randomUUID();
            pocketDims.set(id, newUuid);
            markDirty();
            return newUuid;
        }

        return current;
    }

    public boolean isLegacy(UUID uuid) {
        return legacyAllocations.containsKey(uuid);
    }

    public BlockPos getLegacyCoord(UUID uuid) {
        if (legacyAllocations.containsKey(uuid)) {
            return getCustomSpawn(legacyAllocations.get(uuid));
        }
        // Fallback for dynamic logic if needed, or return null if strictly legacy
        int id = getPocketId(uuid);
        if (id != -1) {
            return getCustomSpawn(id);
        }
        return null;
    }

    public BlockPos getCustomSpawn(int pocketId) {
        if (legacyCustomSpawns.containsKey(pocketId)) {
            return legacyCustomSpawns.get(pocketId);
        }
        // Default to center if no custom spawn?
        // Or return null to indicate "use default"?
        // Calling code usually does: if (custom != null) spawn = custom; else spawn =
        // center;
        // So let's return null to indicate "no custom spawn".
        // BUT getLegacyCoord previously returned center as fallback.
        // Let's keep getCustomSpawn returning NULL if not found, to allow distinction.
        return null;
    }

    public void setLegacySpawn(int pocketId, BlockPos pos) {
        legacyCustomSpawns.put(pocketId, pos);
        markDirty();
    }

    public List<UUID> getPocketUuids() {
        List<UUID> allUuids = new ArrayList<>(pocketDims);
        allUuids.addAll(legacyAllocations.keySet());
        return allUuids;
    }

    public List<Integer> getAllActivePocketIds() {
        java.util.Set<Integer> uniqueIds = new java.util.HashSet<>();
        // Add dynamic IDs
        for (int i = 0; i < pocketDims.size(); i++) {
            uniqueIds.add(i);
        }
        // Add legacy IDs
        uniqueIds.addAll(legacyAllocations.values());

        List<Integer> sorted = new ArrayList<>(uniqueIds);
        java.util.Collections.sort(sorted);
        return sorted;
    }

    // Helper Methods for Grid Logic

    /**
     * Returns the exact center coordinate (X, Z) for a given Pocket ID.
     * With GRID_WIDTH = 10:
     * ID 0 -> (0, 0)
     * ID 1 -> (1000, 0)
     * ID 10 -> (0, 1000)
     */
    public static BlockPos getPocketCenter(int id) {
        int col = id % GRID_WIDTH;
        int row = id / GRID_WIDTH;
        int x = col * POCKET_SPACING; // Center of cell
        int z = row * POCKET_SPACING; // Center of cell
        return new BlockPos(x, 64, z);
    }

    /**
     * Returns the Pocket ID for a given position in the pocket dimension.
     */
    public static int getPocketIdFromPos(BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();

        // Shift by 500 to Map [-500, 500) to [0, 1000) for "index" calculation
        // ID 0 (0,0) -> Range -500 to 499.

        int col = (int) Math.floor((x + 500.0) / POCKET_SPACING);
        int row = (int) Math.floor((z + 500.0) / POCKET_SPACING);

        if (col < 0 || row < 0)
            return -1; // Invalid for now, or handle negative IDs?

        return (row * GRID_WIDTH) + col;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        pocketDims.forEach(uuid -> list.add(NbtHelper.fromUuid(uuid)));
        nbt.put("pocket_uuids", list);

        // Persist legacy map to ensure data safety
        NbtCompound legacyTag = new NbtCompound();
        legacyAllocations.forEach((uuid, id) -> legacyTag.putInt(uuid.toString(), id));
        nbt.put("pocketMap", legacyTag);

        // Persist custom spawns
        NbtCompound spawnsTag = new NbtCompound();
        legacyCustomSpawns.forEach((id, pos) -> spawnsTag.put(String.valueOf(id), NbtHelper.fromBlockPos(pos)));
        nbt.put("customSpawns", spawnsTag);

        return nbt;
    }

    public static PocketManager createFromNbt(NbtCompound tag) {
        PocketManager state = new PocketManager();

        if (tag.contains("pocket_uuids")) {
            NbtList list = tag.getList("pocket_uuids", NbtElement.INT_ARRAY_TYPE);
            for (NbtElement element : list) {
                state.pocketDims.add(NbtHelper.toUuid(element));
            }
        }

        if (tag.contains("pocketMap")) {
            NbtCompound mapTag = tag.getCompound("pocketMap");
            for (String key : mapTag.getKeys()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    int id = mapTag.getInt(key);
                    state.legacyAllocations.put(uuid, id);
                } catch (Exception e) {
                    AnimeWitchery.LOGGER.error("Failed to load legacy pocket entry: " + key, e);
                }
            }
        }

        if (tag.contains("customSpawns")) {
            NbtCompound spawnsTag = tag.getCompound("customSpawns");
            for (String key : spawnsTag.getKeys()) {
                try {
                    int id = Integer.parseInt(key);
                    BlockPos pos = NbtHelper.toBlockPos(spawnsTag.getCompound(key));
                    state.legacyCustomSpawns.put(id, pos);
                } catch (Exception e) {
                    AnimeWitchery.LOGGER.error("Failed to load custom spawn entry: " + key, e);
                }
            }
        }

        return state;
    }

    public static void ensureSpawnPlatform(ServerWorld world, BlockPos centerPos) {
        // Check if there is already a safe landing at the target height
        // Scan a few blocks down because the structure might be offset (e.g. placed at
        // Y-4).
        for (int i = 1; i <= 10; i++) {
            if (!world.getBlockState(centerPos.down(i)).isAir()) {
                return; // Found a block below, so it's safe.
            }
        }

        net.minecraft.structure.StructureTemplateManager templateManager = world.getStructureTemplateManager();
        net.minecraft.util.Identifier templateId = new net.minecraft.util.Identifier(AnimeWitchery.MOD_ID,
                "pocket_spawn_1");
        java.util.Optional<net.minecraft.structure.StructureTemplate> templateOptional = templateManager
                .getTemplate(templateId);

        if (templateOptional.isPresent()) {
            net.minecraft.structure.StructureTemplate template = templateOptional.get();
            net.minecraft.util.math.Vec3i size = template.getSize();

            // Center the structure on X and Z, keep Y at one block below spawn, then lower
            // by 4
            BlockPos placePos = centerPos.down().add(-size.getX() / 2, -4, -size.getZ() / 2);

            net.minecraft.structure.StructurePlacementData placementData = new net.minecraft.structure.StructurePlacementData();
            template.place(world, placePos, placePos, placementData,
                    net.minecraft.util.math.random.Random.create(world.getSeed()), 2);
            System.out.println("PocketManager: Placed Structure at " + placePos);
        } else {
            // Fallback: Generate 3x3 Amethyst Platform
            System.out.println("PocketManager: Placing Fallback Platform at " + centerPos);
            // Check if the block directly below the player (the potential platform center)
            // is air.
            // If it's not air, we assume a platform or structure already exists and do
            // nothing.
            // This prevents overwriting player builds or existing platforms on re-entry.
            if (world.getBlockState(centerPos.down()).isAir()) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos platformPos = centerPos.down().add(x, 0, z);
                        // Only replace air or other non-solid blocks to be safe, though the center
                        // check should handle most cases.
                        if (world.getBlockState(platformPos).isAir()) {
                            world.setBlockState(platformPos,
                                    net.willowins.animewitchery.block.ModBlocks.LANDING_PLATFORM.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    public static BlockPos findNearestPlatform(ServerWorld world, BlockPos center, int radius) {
        BlockPos min = center.add(-radius, -20, -radius);
        BlockPos max = center.add(radius, 20, radius);

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            if (world.getBlockState(pos).isOf(net.willowins.animewitchery.block.ModBlocks.LANDING_PLATFORM)) {
                return pos.toImmutable();
            }
        }
        return null;
    }

    public static PocketManager getServerState(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                PocketManager::createFromNbt,
                PocketManager::new,
                "pocket_manager");
    }
}
