package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.willowins.animewitchery.networking.ModPackets;
import net.willowins.animewitchery.screen.AlchemicalEnchanterScreenHandler;
import net.willowins.animewitchery.util.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AlchemicalEnchanterBlockEntity extends BlockEntity
        implements ImplementedInventory, NamedScreenHandlerFactory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final List<EnchantmentLevelEntry> availableEnchantments = new ArrayList<>();

    public AlchemicalEnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALCHEMICAL_ENCHANTER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    public List<EnchantmentLevelEntry> getAvailableEnchantments() {
        return availableEnchantments;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (slot == 0) {
            generateEnchantmentLists();
        }
        markDirty();
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        ItemStack result = ImplementedInventory.super.removeStack(slot, count);
        if (slot == 0) {
            generateEnchantmentLists();
        }
        markDirty();
        return result;
    }

    private void generateEnchantmentLists() {
        if (world == null || world.isClient)
            return;

        ItemStack itemStack = inventory.get(0);
        availableEnchantments.clear();

        if (!itemStack.isEmpty()) {
            System.out.println("[AnimeWitchery] Generating dynamic enchants for: " + itemStack.getName().getString());
            for (Enchantment enchantment : Registries.ENCHANTMENT) {
                if (enchantment.isAcceptableItem(itemStack)) {
                    // Store the max level available for this item
                    availableEnchantments.add(new EnchantmentLevelEntry(enchantment, enchantment.getMaxLevel()));
                }
            }
            System.out.println("[AnimeWitchery] Found " + availableEnchantments.size() + " unique enchants.");
        }
        // When the lists are generated/updated, we need to sync them to any open screen
        // handler
        // This will be handled by the screen handler itself requesting a sync on open,
        // and then potentially by a block update if the screen handler listens to it.
        // For now, we'll assume the screen handler will request on open.
    }

    public void syncToPlayer(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer
                && serverPlayer.currentScreenHandler instanceof AlchemicalEnchanterScreenHandler handler) {
            syncToPlayer(player, handler.syncId);
        }
    }

    public void syncToPlayer(PlayerEntity player, int syncId) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(syncId);
            buf.writeInt(availableEnchantments.size());
            for (EnchantmentLevelEntry entry : availableEnchantments) {
                buf.writeInt(Registries.ENCHANTMENT.getRawId(entry.enchantment));
                buf.writeInt(entry.level); // This is maxLevel
            }
            ServerPlayNetworking.send(serverPlayer, ModPackets.SYNC_ENCHANTMENT_LISTS, buf);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return pos.isWithinDistance(player.getBlockPos(), 4.5);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.enchant");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        generateEnchantmentLists(); // Force refresh before opening
        AlchemicalEnchanterScreenHandler handler = new AlchemicalEnchanterScreenHandler(syncId, playerInventory,
                ScreenHandlerContext.create(world, pos), this);
        handler.onContentChanged(this); // Initialize properties and server-side state
        syncToPlayer(player, syncId); // Send initial sync packet
        return handler;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        generateEnchantmentLists();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.inventory);
        super.writeNbt(nbt);
    }
}
