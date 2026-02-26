package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CosmeticBagItem extends Item {
    public CosmeticBagItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getStackInHand(hand);
            user.openHandledScreen(createScreenHandlerFactory(stack));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private NamedScreenHandlerFactory createScreenHandlerFactory(ItemStack stack) {
        return new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player) -> {
            SimpleInventory inventory = new SimpleInventory(27); // 9x3 Chest GUI

            // Load from NBT
            NbtCompound nbt = stack.getOrCreateNbt();
            if (nbt.contains("Items")) {
                NbtList list = nbt.getList("Items", 10);
                for (int i = 0; i < list.size(); i++) {
                    NbtCompound itemNbt = list.getCompound(i);
                    int slot = itemNbt.getByte("Slot") & 255;
                    if (slot < inventory.size()) {
                        inventory.setStack(slot, ItemStack.fromNbt(itemNbt));
                    }
                }
            }

            inventory.addListener(sender -> {
                // Save to NBT on change
                NbtList list = new NbtList();
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack itemStack = inventory.getStack(i);
                    if (!itemStack.isEmpty()) {
                        NbtCompound itemNbt = new NbtCompound();
                        itemNbt.putByte("Slot", (byte) i);
                        itemStack.writeNbt(itemNbt);
                        list.add(itemNbt);
                    }
                }
                stack.getOrCreateNbt().put("Items", list);
                // Force update on server side
                if (playerInventory.player instanceof net.minecraft.server.network.ServerPlayerEntity) {
                    playerInventory.markDirty();
                }
            });

            return new net.willowins.animewitchery.screen.CosmeticBagScreenHandler(syncId, playerInventory, inventory);
        }, Text.of("Cosmetic Bag"));
    }
}
