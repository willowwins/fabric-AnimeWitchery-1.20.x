package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.willowins.animewitchery.item.ModItems;

public class SpawnerNbtHandler {

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient)
                return ActionResult.PASS;

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

            // Soul Spawner Binding
            if (stack.getItem() == ModItems.SOUL && clickedState.getBlock() == Blocks.SPAWNER) {
                BlockEntity blockEntity = world.getBlockEntity(clickedPos);
                if (blockEntity instanceof MobSpawnerBlockEntity spawnerBlockEntity) {
                    NbtCompound soulNbt = stack.getNbt();
                    if (soulNbt != null && soulNbt.contains("EntityData")) {
                        // Extract Entity ID
                        NbtCompound entityData = soulNbt.getCompound("EntityData");
                        // Some souls might have EntityId separate, some inside EntityData as 'id'
                        String entityId = soulNbt.contains("EntityId") ? soulNbt.getString("EntityId")
                                : entityData.getString("id");

                        if (!entityId.isEmpty()) {
                            // Update Spawner Logic
                            MobSpawnerLogic logic = spawnerBlockEntity.getLogic();
                            NbtCompound spawnerNbt = new NbtCompound();
                            logic.writeNbt(spawnerNbt);

                            // Create new SpawnData
                            NbtCompound spawnData = new NbtCompound();
                            spawnData.put("entity", entityData);
                            spawnerNbt.put("SpawnData", spawnData);

                            // Reset SpawnPotentials if we want it to be pure
                            spawnerNbt.remove("SpawnPotentials");

                            logic.readNbt(world, clickedPos, spawnerNbt);
                            spawnerBlockEntity.markDirty();
                            world.updateListeners(clickedPos, clickedState, clickedState, 3);

                            // Feedback
                            if (!player.isCreative()) {
                                stack.decrement(1);
                            }

                            world.playSound(null, clickedPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                                    SoundCategory.BLOCKS, 1.0f, 1.0f);
                            if (world instanceof ServerWorld serverWorld) {
                                serverWorld.spawnParticles(ParticleTypes.SOUL,
                                        clickedPos.getX() + 0.5, clickedPos.getY() + 0.5, clickedPos.getZ() + 0.5, 20,
                                        0.3, 0.3, 0.3, 0.05);
                            }

                            player.sendMessage(Text.literal("Soul bound to spawner: " + soulNbt.getString("EntityName"))
                                    .formatted(Formatting.GREEN), true);

                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }

            return ActionResult.PASS;
        });
    }
}
