package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.enchantments.ModEnchantments;

public class ExcavationBreakHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerWorld serverWorld)) return;

            ItemStack tool = player.getMainHandStack();
            if (EnchantmentHelper.getLevel(ModEnchantments.EXCAVATE_ENCHANT, tool) <= 0) return;
            if (!tool.isSuitableFor(state)) return;

            // Detect block face using player's camera direction and position
            BlockHitResult hitResult = (BlockHitResult) player.raycast(5.0D, 0.0F, false);
            Direction face = hitResult.getSide();

            // Determine axes for the 3x3 plane based on the face
            Direction.Axis axis = face.getAxis();
            int[][] offsets = switch (axis) {
                case X -> new int[][]{{0, -1}, {0, 0}, {0, 1}, {0, -1}, {0, 0}, {0, 1}, {0, -1}, {0, 0}, {0, 1}}; // Y/Z plane
                case Y -> new int[][]{{-1, 0}, {0, 0}, {1, 0}, {-1, 0}, {0, 0}, {1, 0}, {-1, 0}, {0, 0}, {1, 0}}; // X/Z plane
                case Z -> new int[][]{{-1, 0}, {0, 0}, {1, 0}, {-1, 0}, {0, 0}, {1, 0}, {-1, 0}, {0, 0}, {1, 0}}; // X/Y plane
            };

            // Apply 3x3 pattern on plane perpendicular to the face
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;

                    BlockPos targetPos;
                    switch (axis) {
                        case X:
                            targetPos = pos.add(0, dx, dy); // Y/Z plane
                            break;
                        case Y:
                            targetPos = pos.add(dx, 0, dy); // X/Z plane
                            break;
                        case Z:
                            targetPos = pos.add(dx, dy, 0); // X/Y plane
                            break;
                        default:
                            continue; // skip invalid axis
                    }

                    BlockState targetState = world.getBlockState(targetPos);


                    if (tool.isSuitableFor(targetState)) {
                        serverWorld.breakBlock(targetPos, true, player);
                    }
                }
            }
        });
    }
}
