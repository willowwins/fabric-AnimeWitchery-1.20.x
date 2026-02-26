package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.enchantments.ModEnchantments;

import java.util.*;

public class ChainsawBreakHandler {

    private static final Set<BlockPos> BREAKING_BLOCKS = new HashSet<>();

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerWorld serverWorld))
                return;
            if (!(player instanceof ServerPlayerEntity))
                return;

            // Prevent recursion
            if (BREAKING_BLOCKS.contains(pos))
                return;

            ItemStack tool = player.getMainHandStack();
            if (EnchantmentHelper.getLevel(ModEnchantments.CHAINSAW_ENCHANT, tool) <= 0)
                return;

            // Allow if tool is an axe OR if it has the enchantment (e.g. book on stick?)
            // Usually limit to logging logic.
            // Check if broken block was a log
            if (!state.isIn(BlockTags.LOGS))
                return;

            // BFS for connected logs
            Queue<BlockPos> queue = new ArrayDeque<>();
            Set<BlockPos> visited = new HashSet<>();
            List<BlockPos> toBreak = new ArrayList<>();

            // We start searching from neighbors of the broken block
            // because the original block is already broken.
            visited.add(pos);

            // Add initial neighbors
            for (BlockPos neighbor : getNeighbors(pos)) {
                if (isLog(world.getBlockState(neighbor))) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }

            int count = 0;
            int maxBlocks = 64;

            while (!queue.isEmpty() && count < maxBlocks) {
                BlockPos current = queue.poll();
                toBreak.add(current);
                count++;

                for (BlockPos neighbor : getNeighbors(current)) {
                    if (!visited.contains(neighbor) && isLog(world.getBlockState(neighbor))) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            // Break blocks and drop at player
            BlockPos playerPos = player.getBlockPos();

            for (BlockPos target : toBreak) {
                BlockState targetState = world.getBlockState(target);
                BlockEntity targetEntity = world.getBlockEntity(target);

                BREAKING_BLOCKS.add(target); // Prevent infinite loop if event triggers again

                // Get drops
                List<ItemStack> drops = Block.getDroppedStacks(targetState, serverWorld, target, targetEntity, player,
                        tool);

                // Drop items at player
                for (ItemStack drop : drops) {
                    Block.dropStack(world, playerPos, drop);
                }

                // Damage tool?
                // tool.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
                // We should damage tool. BUT careful about breaking it mid-loop.
                if (tool.isDamageable()) {
                    tool.setDamage(tool.getDamage() + 1);
                    if (tool.getDamage() >= tool.getMaxDamage()) {
                        tool.decrement(1);
                        player.sendToolBreakStatus(player.getActiveHand());
                        break; // Tool broken, stop chopping
                    }
                }

                // Set to air
                world.setBlockState(target, Blocks.AIR.getDefaultState(), 3);

                BREAKING_BLOCKS.remove(target);
            }
        });
    }

    private static List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0)
                        continue;
                    neighbors.add(pos.add(x, y, z));
                }
            }
        }
        return neighbors;
    }

    private static boolean isLog(BlockState state) {
        return state.isIn(BlockTags.LOGS);
    }
}
