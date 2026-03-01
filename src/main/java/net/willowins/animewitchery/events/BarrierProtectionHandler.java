package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;

public class BarrierProtectionHandler {

    public static void register() {
        // Prevent block breaking
        PlayerBlockBreakEvents.BEFORE.register(BarrierProtectionHandler::onBlockBreak);

        // Prevent block interaction (except barrier circles themselves for allowlist
        // management)
        UseBlockCallback.EVENT.register(BarrierProtectionHandler::onUseBlock);

    }

    private static boolean onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
            BlockEntity blockEntity) {

        if (world.isClient || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return true; // Allow on client

        }
        if (player.isCreative()) return true; // Creative mode bypasses protection
        if (player.isSpectator()) return true; // Spectator mode bypasses protection
        // Check if the BLOCK being broken is in a barrier area
        // We check the block's position, not the player's.
        // BarrierCircleBlockEntity.findBarrierAt ignores Y, so this covers Bedrock to
        // Sky.
        BarrierCircleBlockEntity barrier = BarrierCircleBlockEntity.findBarrierAt(world, pos.toCenterPos());

        if (barrier != null && !barrier.isPlayerAllowedByUuid(serverPlayer.getUuid())) {
            // Player is unauthorized and trying to break a block inside a protected barrier
            serverPlayer.sendMessage(
                    Text.literal("⚠️ This area is protected by a magical barrier.")
                            .formatted(Formatting.RED),
                    true);

            return false; // Cancel block break
        }

        return true; // Allow block break
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);

        // Always allow interaction with barrier circles and distance glyphs (for
        // allowlist management)
        if (state.isOf(ModBlocks.BARRIER_CIRCLE) || state.isOf(ModBlocks.BARRIER_DISTANCE_GLYPH)) {
            return ActionResult.PASS;
        }

        // Check if the BLOCK being interacted with is in a barrier area
        BarrierCircleBlockEntity barrier = BarrierCircleBlockEntity.findBarrierAt(world, pos.toCenterPos());

        if (barrier != null && !barrier.isPlayerAllowedByUuid(serverPlayer.getUuid())) {
            // Player is unauthorized and trying to interact inside a protected barrier
            serverPlayer.sendMessage(
                    Text.literal("This area is protected by a magical barrier.")
                            .formatted(Formatting.RED),
                    true);
            return ActionResult.FAIL; // Cancel interaction
        }

        return ActionResult.PASS; // Allow interaction
    }
}
