package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.willowins.animewitchery.enchantments.ModEnchantments;

public class ExcavationBreakHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerWorld serverWorld)) return;

            ItemStack tool = player.getMainHandStack();

            if (EnchantmentHelper.getLevel(ModEnchantments.EXCAVATE_ENCHANT, tool) > 0) {
                if (!tool.isSuitableFor(state)) return;

                Direction facing = player.getHorizontalFacing(); // To determine orientation
                boolean isVertical = player.getPitch() < -60 || player.getPitch() > 60;

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {

                            // Only apply for horizontal 3x3 or vertical 3x3 based on where the player is looking
                            if (!(dx == 0 && dy == 0 && dz == 0)) {
                                BlockPos newPos = pos.add(dx, dy, dz);
                                BlockState targetState = world.getBlockState(newPos);
                                if (tool.isSuitableFor(targetState)) {
                                    serverWorld.breakBlock(newPos, true, player);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
