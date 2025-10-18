package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class SpawnerNbtHandler {
    
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            
            ItemStack stack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            
            // Check if player is placing a spawner
            if (stack.getItem() == Items.SPAWNER && stack.hasNbt()) {
                // Schedule NBT application for next tick (after block is placed)
                world.getServer().execute(() -> {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity instanceof MobSpawnerBlockEntity spawnerBlockEntity) {
                        if (stack.getNbt() != null && stack.getNbt().contains("BlockEntityTag")) {
                            spawnerBlockEntity.readNbt(stack.getNbt().getCompound("BlockEntityTag"));
                            spawnerBlockEntity.markDirty();
                        }
                    }
                });
            }
            
            return ActionResult.PASS;
        });
    }
}



