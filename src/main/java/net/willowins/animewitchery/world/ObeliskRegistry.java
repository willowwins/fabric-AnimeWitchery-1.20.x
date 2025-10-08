package net.willowins.animewitchery.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores all known Obelisk positions in a given dimension.
 * Persistent across world saves, per dimension.
 */
public class ObeliskRegistry extends PersistentState {

    private final Set<BlockPos> obelisks = new HashSet<>();

    /** Checks if an Obelisk exists at the given position. */
    public boolean contains(BlockPos pos) {
        return obelisks.contains(pos);
    }

    /** Adds an Obelisk to the registry (if not already present). */
    public void register(BlockPos pos) {
        if (obelisks.add(pos)) {
            markDirty();
            System.out.println("[ObeliskRegistry] Registered Obelisk at " + pos);
        }
    }

    /** Removes an Obelisk from the registry (if it exists). */
    public void unregister(BlockPos pos) {
        if (obelisks.remove(pos)) {
            markDirty();
            System.out.println("[ObeliskRegistry] Unregistered Obelisk at " + pos);
        }
    }

    /** Returns all registered Obelisks. */
    public Set<BlockPos> getAll() {
        return obelisks;
    }

    // ====================================================
    // ===============  PERSISTENT STATE LOGIC  ===========
    // ====================================================

    /** Retrieves or creates this world's registry. */
    public static ObeliskRegistry get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                ObeliskRegistry::createFromNbt,
                ObeliskRegistry::new,
                "obelisk_registry"
        );
    }

    /** Loads registry from NBT. */
    public static ObeliskRegistry createFromNbt(NbtCompound nbt) {
        ObeliskRegistry reg = new ObeliskRegistry();
        NbtList list = nbt.getList("Obelisks", 10);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound posTag = list.getCompound(i);
            reg.obelisks.add(new BlockPos(
                    posTag.getInt("x"),
                    posTag.getInt("y"),
                    posTag.getInt("z")
            ));
        }
        return reg;
    }

    /** Saves registry to NBT. */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (BlockPos pos : obelisks) {
            NbtCompound tag = new NbtCompound();
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
            list.add(tag);
        }
        nbt.put("Obelisks", list);
        return nbt;
    }

    // ====================================================
    // ===============  SEARCH UTILITIES  =================
    // ====================================================

    /**
     * Finds the nearest registered Obelisk to a given position, within a radius.
     * @param origin the starting position (usually player position)
     * @param maxDistance maximum search radius in blocks
     * @return the nearest Obelisk position, or null if none are within range
     */
    @Nullable
    public BlockPos findNearest(BlockPos origin, int maxDistance) {
        BlockPos nearest = null;
        double bestDistSq = (double) maxDistance * maxDistance;

        for (BlockPos pos : obelisks) {
            double distSq = origin.getSquaredDistance(pos);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                nearest = pos;
            }
        }

        return nearest;
    }
}
