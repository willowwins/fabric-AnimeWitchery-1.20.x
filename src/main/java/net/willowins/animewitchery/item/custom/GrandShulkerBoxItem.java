package net.willowins.animewitchery.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
            if (stack.hasNbt()) {
                NbtCompound nbt = stack.getNbt();
                if (nbt != null && nbt.contains("BlockEntityTag")) {
                    BlockPos pos = context.getBlockPos();
                    BlockEntity blockEntity = context.getWorld().getBlockEntity(pos);
                    if (blockEntity instanceof GrandShulkerBoxBlockEntity grandBox) {
                        NbtCompound blockEntityTag = nbt.getCompound("BlockEntityTag");
                        grandBox.readNbt(blockEntityTag);
                        grandBox.markDirty();
                        
                        // Mark the chunk as dirty to ensure the data is saved
                        if (context.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                            serverWorld.getChunkManager().markForUpdate(pos);
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
}
