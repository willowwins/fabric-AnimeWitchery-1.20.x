package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class SpawnerNbtHandler {
    
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            
            ItemStack stack = player.getStackInHand(hand);
            BlockPos clickedPos = hitResult.getBlockPos();
            BlockState clickedState = world.getBlockState(clickedPos);
            
            // Check if player is placing a spawner
            if (stack.getItem() == Items.SPAWNER && stack.hasNbt()) {
                // Calculate where the spawner will be placed
                BlockPos spawnerPos;
                ItemPlacementContext context = new ItemPlacementContext(player, hand, stack, hitResult);
                if (clickedState.canReplace(context)) {
                    // Replacing a replaceable block (like grass, water, etc.)
                    spawnerPos = clickedPos;
                } else {
                    // Placing against a solid block - offset by the clicked face
                    spawnerPos = clickedPos.offset(hitResult.getSide());
                }
                
                // Schedule NBT application for next tick (after block is placed)
                world.getServer().execute(() -> {
                    // Verify a spawner was actually placed at this position
                    if (world.getBlockState(spawnerPos).getBlock() == Blocks.SPAWNER) {
                        BlockEntity blockEntity = world.getBlockEntity(spawnerPos);
                        if (blockEntity instanceof MobSpawnerBlockEntity spawnerBlockEntity) {
                            if (stack.getNbt() != null && stack.getNbt().contains("BlockEntityTag")) {
                                spawnerBlockEntity.readNbt(stack.getNbt().getCompound("BlockEntityTag"));
                                spawnerBlockEntity.markDirty();
                            }
                        }
                    }
                });
            }
            
            return ActionResult.PASS;
        });
    }
}



