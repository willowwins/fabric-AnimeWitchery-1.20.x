package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;

public class NormalChalkItem extends Item {

    public NormalChalkItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        if (!world.isClient) {
            // Get the block we're clicking on
            BlockHitResult hitResult = (BlockHitResult) user.raycast(5.0D, 0.0F, false);
            BlockPos pos = hitResult.getBlockPos();
            Direction face = hitResult.getSide();
            
            // Calculate the position to place the circle (on top of the clicked surface)
            BlockPos circlePos = pos.offset(face);
            
            // Check if we can draw here (must be on a horizontal surface)
            if (canDrawCircle(world, circlePos, face)) {
                System.out.println("Normal Chalk: Creating barrier circle at " + circlePos);
                // Create the barrier circle block
                createBarrierCircle(world, circlePos);
                
                // Play drawing sound
                world.playSound(null, circlePos, net.minecraft.sound.SoundEvents.BLOCK_BEACON_ACTIVATE, net.minecraft.sound.SoundCategory.BLOCKS, 0.5f, 1.2f);
                
                return TypedActionResult.success(stack);
            } else {
                System.out.println("Normal Chalk: Cannot draw circle at " + circlePos + " with face " + face);
            }
        }
        
        return TypedActionResult.fail(stack);
    }
    
    private boolean canDrawCircle(World world, BlockPos pos, Direction face) {
        // Must be drawing on a horizontal surface (top or bottom)
        if (face != Direction.UP && face != Direction.DOWN) {
            System.out.println("Normal Chalk: Not a horizontal surface - face: " + face);
            return false;
        }
        
        // Check if the block is air or replaceable
        net.minecraft.block.BlockState state = world.getBlockState(pos);
        boolean canPlace = state.isAir() || state.isReplaceable();
        
        if (!canPlace) {
            System.out.println("Normal Chalk: Cannot place here - block: " + state.getBlock());
        }
        
        return canPlace;
    }
    
    private void createBarrierCircle(World world, BlockPos pos) {
        // Place the barrier circle block
        world.setBlockState(pos, ModBlocks.BARRIER_CIRCLE.getDefaultState());
        
        // The block entity will be created automatically and start in BASIC stage
    }
}
