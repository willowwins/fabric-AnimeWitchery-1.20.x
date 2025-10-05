package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class RepairCharmItem extends Item {
    private static final int REPAIR_INTERVAL = 20; // 20 ticks = 1 second
    private static final int REPAIR_AMOUNT = 1;   // durability restored per interval

    public RepairCharmItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        // Use tick counter stored in NBT
        int tickCounter = stack.getOrCreateNbt().getInt("repair_ticks");
        tickCounter++;
        if (tickCounter >= REPAIR_INTERVAL) {
            tickCounter = 0;

            // Loop through all inventory slots
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack invStack = player.getInventory().getStack(i);

                if (invStack.isDamageable() && invStack.isDamaged()) {
                    invStack.setDamage(invStack.getDamage() - REPAIR_AMOUNT);
                }
            }
        }

        stack.getOrCreateNbt().putInt("repair_ticks", tickCounter);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        // Optional: make it always glow like it's enchanted
        return true;
    }
}
