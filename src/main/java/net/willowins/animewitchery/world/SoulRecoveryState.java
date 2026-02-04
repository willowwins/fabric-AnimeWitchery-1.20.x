package net.willowins.animewitchery.world;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.*;

public class SoulRecoveryState extends PersistentState {
    private final Map<UUID, List<ItemStack>> pendingSouls = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        pendingSouls.forEach((uuid, items) -> {
            NbtCompound entry = new NbtCompound();
            entry.putUuid("Player", uuid);
            NbtList itemList = new NbtList();
            for (ItemStack item : items) {
                NbtCompound itemNbt = new NbtCompound();
                item.writeNbt(itemNbt);
                itemList.add(itemNbt);
            }
            entry.put("Items", itemList);
            list.add(entry);
        });
        nbt.put("PendingSouls", list);
        return nbt;
    }

    public static SoulRecoveryState createFromNbt(NbtCompound nbt) {
        SoulRecoveryState state = new SoulRecoveryState();
        if (nbt.contains("PendingSouls")) {
            NbtList list = nbt.getList("PendingSouls", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound entry = list.getCompound(i);
                UUID uuid = entry.getUuid("Player");
                NbtList itemList = entry.getList("Items", NbtElement.COMPOUND_TYPE);
                List<ItemStack> items = new ArrayList<>();
                for (int j = 0; j < itemList.size(); j++) {
                    items.add(ItemStack.fromNbt(itemList.getCompound(j)));
                }
                state.pendingSouls.put(uuid, items);
            }
        }
        return state;
    }

    public static SoulRecoveryState getServerState(ServerWorld world) {
        ServerWorld serverWorld = world.getServer().getWorld(World.OVERWORLD);
        if (serverWorld == null)
            return null;

        return serverWorld.getPersistentStateManager().getOrCreate(
                SoulRecoveryState::createFromNbt,
                SoulRecoveryState::new,
                "animewitchery_soul_recovery");
    }

    public void addSoul(UUID playerUuid, ItemStack soul) {
        pendingSouls.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(soul);
        markDirty();
    }

    public List<ItemStack> retrieveSouls(UUID playerUuid) {
        if (pendingSouls.containsKey(playerUuid)) {
            List<ItemStack> items = new ArrayList<>(pendingSouls.get(playerUuid));
            pendingSouls.remove(playerUuid);
            markDirty();
            return items;
        }
        return Collections.emptyList();
    }
}
