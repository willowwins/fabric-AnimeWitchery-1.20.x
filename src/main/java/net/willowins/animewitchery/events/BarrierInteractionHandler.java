package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;

/**
 * Allows unauthorized players to interact with barrier circles and distance glyphs
 * so they can add themselves to the allowlist using renamed paper.
 */
public class BarrierInteractionHandler {
    
    public static void register() {
        UseBlockCallback.EVENT.register(BarrierInteractionHandler::onUseBlock);
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        // Only process on server side
        if(player.isCreative()){return ActionResult.PASS;}
        if (world.isClient || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.PASS;
        }
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        
        // Check if player is interacting with a barrier circle or distance glyph
        if (state.isOf(ModBlocks.BARRIER_CIRCLE) || state.isOf(ModBlocks.BARRIER_DISTANCE_GLYPH)) {
            // Find the nearest barrier to check if player is inside one
            BarrierCircleBlockEntity barrier = BarrierCircleBlockEntity.findBarrierAt(world, player.getPos());

            if (barrier != null) {
                // Check if player is unauthorized


                if (!barrier.isPlayerAllowedByUuid(serverPlayer.getUuid())) {
                    // Allow the interaction to proceed anyway - don't cancel it
                    // This is handled by returning PASS, which lets the normal block interaction happen
                    System.out.println("BarrierInteraction: Allowing unauthorized player " + serverPlayer.getName().getString() + 
                                     " to interact with barrier block for allowlist management");
                }
            }
        }
        
        return ActionResult.PASS;
    }
}

