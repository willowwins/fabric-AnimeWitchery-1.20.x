package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.TrinketComponent;
import java.util.Optional;
import net.minecraft.block.enums.ChestType; // Re-added
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction; // Re-added
import net.minecraft.world.BlockView; // Added
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.block.ModBlocks;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.ProtectedChestBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.sound.SoundCategory; // Added
import net.minecraft.sound.SoundEvents; // Added

public class ProtectedChestBlock extends ChestBlock {

    public ProtectedChestBlock(Settings settings) {
        super(settings, () -> ModBlocks.PROTECTED_CHEST_ENTITY);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient && placer instanceof PlayerEntity player) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ProtectedChestBlockEntity chest) {
                chest.setOwner(player.getUuid(), player.getName().getString());
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ProtectedChestBlockEntity chest)) {
            return ActionResult.PASS;
        }

        ProtectedChestBlockEntity otherChest = getOtherChest(world, pos, state);

        // Helper check for key
        boolean hasKey = checkKey(player, chest.getLockName());
        if (!hasKey && otherChest != null && otherChest.isLocked()) {
            // If this chest is unlocked (or locked with X) but other is locked with Y,
            // check Y?
            // Ideally we sync locks. If other implies lock, check it.
            if (checkKey(player, otherChest.getLockName())) {
                hasKey = true;
            }
        }

        // Pickup Logic handled by UseBlockCallback in AnimeWitchery.java to bypass
        // sneak blocking

        // Handle Master Key
        ItemStack heldItem = player.getStackInHand(hand);
        if (heldItem.getItem() == net.willowins.animewitchery.item.ModItems.MASTER_KEY) {
            if (chest.isLocked() || (otherChest != null && otherChest.isLocked())) {
                // Open GUI without unlocking
                if (!player.isCreative()) {
                    heldItem.decrement(1);
                }

                world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0f, 1.0f);
                player.sendMessage(Text.literal("§6Master Key used! Access granted."), true);

                // Explicitly open the screen
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }

                return ActionResult.CONSUME;
            } else {
                player.sendMessage(Text.literal("§eThis chest is already unlocked."), true);
                return ActionResult.CONSUME;
            }
        }

        // Handle Key Interaction (Locking/Unlocking)
        if (heldItem.getItem() instanceof net.willowins.animewitchery.item.custom.KeyItem) {
            if (heldItem.hasCustomName()) {
                String keyName = heldItem.getName().getString();

                if (!chest.isLocked() && (otherChest == null || !otherChest.isLocked())) {
                    // Lock both
                    chest.setLockName(keyName);
                    if (otherChest != null)
                        otherChest.setLockName(keyName);

                    player.sendMessage(Text.literal("§aChest locked."), true);
                    return ActionResult.SUCCESS;
                } else {
                    // Unlock
                    boolean matched = false;
                    if (chest.getLockName() != null && chest.getLockName().equals(keyName))
                        matched = true;
                    if (!matched && otherChest != null && otherChest.getLockName() != null
                            && otherChest.getLockName().equals(keyName))
                        matched = true;

                    if (matched) {
                        if (player.isSneaking()) {
                            chest.setLockName(null);
                            if (otherChest != null)
                                otherChest.setLockName(null);
                            player.sendMessage(Text.literal("§eChest unlocked."), true);
                            return ActionResult.SUCCESS;
                        } else {
                            // Key matches, allow opening
                        }
                    } else {
                        // Check if it's already locked (and key failed)
                        if (chest.isLocked() || (otherChest != null && otherChest.isLocked())) {
                            player.sendMessage(Text.literal("§cThis key does not fit."), true);
                            return ActionResult.FAIL;
                        }
                    }
                }
            } else {
                player.sendMessage(Text.literal("§cThis key is blank! Rename it in an anvil to use it."), true);
                return ActionResult.FAIL;
            }
        }

        // Check Access
        if (chest.isLocked() || (otherChest != null && otherChest.isLocked())) {
            // Re-evaluate hasKey based on the actual lock present
            String required = chest.isLocked() ? chest.getLockName()
                    : (otherChest != null ? otherChest.getLockName() : null);
            if (required != null && !checkKey(player, required)) {
                player.sendMessage(Text.literal("§cThis chest is locked."), true);
                return ActionResult.FAIL;
            }
        }

        // Open the chest inventory
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        if (screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
        }

        return ActionResult.CONSUME;
    }

    public static boolean checkKey(PlayerEntity player, String lockName) {
        if (lockName == null)
            return true;

        // Check main hand and offhand
        if (isKey(player.getMainHandStack(), lockName) || isKey(player.getOffHandStack(), lockName)) {
            return true;
        }

        // Check entire inventory
        for (ItemStack stack : player.getInventory().main) {
            if (isKey(stack, lockName)) {
                return true;
            }
        }

        // Check Trinkets (Belt/Etc)
        try {
            Optional<TrinketComponent> component = TrinketsApi
                    .getTrinketComponent(player);
            if (component.isPresent()) {
                if (component.get().isEquipped(stack -> isKey(stack, lockName))) {
                    return true;
                }
            }
        } catch (NoClassDefFoundError e) {
            // Trinkets not loaded or API issue
        }

        return false;
    }

    public static boolean isKey(ItemStack stack, String name) {
        return stack.getItem() instanceof net.willowins.animewitchery.item.custom.KeyItem &&
                stack.hasCustomName() && stack.getName().getString().equals(name);
    }

    private ProtectedChestBlockEntity getOtherChest(World world, BlockPos pos, BlockState state) {
        ChestType type = state.get(CHEST_TYPE);
        if (type == ChestType.SINGLE)
            return null;

        Direction facing = state.get(FACING);
        Direction otherDir = type == ChestType.LEFT ? facing.rotateYClockwise() : facing.rotateYCounterclockwise();
        BlockPos otherPos = pos.offset(otherDir);

        BlockEntity be = world.getBlockEntity(otherPos);
        if (be instanceof ProtectedChestBlockEntity chest) {
            return chest;
        }
        return null;
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ProtectedChestBlockEntity chest) {
            // Owner Check
            if (!chest.isOwner(player) && !player.isCreative()) {
                return 0.0f;
            }

            // Offhand Key Check (if locked)
            if (chest.isLocked()) {
                ItemStack offHand = player.getOffHandStack();
                // Strict check: Must hold key in offhand to break
                if (!isKey(offHand, chest.getLockName()) && !player.isCreative()) {
                    return 0.0f;
                }
            }
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ProtectedChestBlockEntity chest) {
                ItemScatterer.spawn(world, pos, chest);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ProtectedChestBlockEntity(pos, state);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return net.minecraft.screen.ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}
