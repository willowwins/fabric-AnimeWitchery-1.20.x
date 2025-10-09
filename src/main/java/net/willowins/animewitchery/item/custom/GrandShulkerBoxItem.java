package net.willowins.animewitchery.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.custom.GrandShulkerBoxBlock;
import net.willowins.animewitchery.block.entity.GrandShulkerBoxBlockEntity;

public class GrandShulkerBoxItem extends BlockItem {
    
    public GrandShulkerBoxItem(Block block, Settings settings) {
        super(block, settings);
    }
    
    @Override
    public ActionResult place(ItemPlacementContext context) {
        ActionResult result = super.place(context);
        
        // If placement was successful, transfer NBT data from item to block entity
        if (result == ActionResult.SUCCESS && !context.getWorld().isClient) {
            ItemStack stack = context.getStack();
            BlockPos pos = context.getBlockPos();
            BlockEntity blockEntity = context.getWorld().getBlockEntity(pos);
            
            if (blockEntity instanceof GrandShulkerBoxBlockEntity grandBox) {
                // Handle color from NBT
                if (stack.hasNbt()) {
                    NbtCompound nbt = stack.getNbt();
                    if (nbt != null) {
                        // Apply color if present
                        if (nbt.contains("color")) {
                            String colorName = nbt.getString("color");
                            DyeColor color = DyeColor.byName(colorName, DyeColor.PURPLE);
                            BlockState currentState = context.getWorld().getBlockState(pos);
                            if (currentState.getBlock() instanceof GrandShulkerBoxBlock) {
                                // Get the appropriate colored block
                                Block coloredBlock = getColoredBlock(color);
                                BlockState newState = coloredBlock.getDefaultState().with(GrandShulkerBoxBlock.FACING, currentState.get(GrandShulkerBoxBlock.FACING));
                                context.getWorld().setBlockState(pos, newState);
                                
                                // Force block update to ensure client synchronization
                                context.getWorld().updateListeners(pos, currentState, newState, 3);
                            }
                        }
                        
                        // Transfer block entity data
                        if (nbt.contains("BlockEntityTag")) {
                            NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");
                            grandBox.readNbt(blockEntityTag);
                            grandBox.markDirty();
                        }
                    }
                }
                
                // Mark the chunk as dirty to ensure the data is saved
                if (context.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                    serverWorld.getChunkManager().markForUpdate(pos);
                }
            }
        }
        
        return result;
    }
    
    // Helper method to get the colored block for a given dye color
    private static Block getColoredBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_WHITE;
            case ORANGE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_ORANGE;
            case MAGENTA -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_MAGENTA;
            case LIGHT_BLUE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_LIGHT_BLUE;
            case YELLOW -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_YELLOW;
            case LIME -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_LIME;
            case PINK -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_PINK;
            case GRAY -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_GRAY;
            case LIGHT_GRAY -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_LIGHT_GRAY;
            case CYAN -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_CYAN;
            case PURPLE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_PURPLE;
            case BLUE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_BLUE;
            case BROWN -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_BROWN;
            case GREEN -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_GREEN;
            case RED -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_RED;
            case BLACK -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_BLACK;
        };
    }
    
}
