package net.willowins.animewitchery.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import net.willowins.animewitchery.item.ModItems;

public class ChiselItem extends Item {
    public ChiselItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        
        // Check if we're using the chisel on an obelisk
        if (state.isOf(ModBlocks.OBELISK)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ObeliskBlockEntity obeliskEntity) {
                // Check if player is sneaking (shift key)
                if (context.getPlayer() != null && context.getPlayer().isSneaking()) {
                    // Destroy the obelisk and drop a shard
                    world.breakBlock(pos, false);
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    
                    // Drop obelisk shard
                    ItemStack shardStack = new ItemStack(ModItems.OBELISK_SHARD, 1);
                    ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, shardStack);
                    world.spawnEntity(itemEntity);
                    
                    // Play breaking sound
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0f, 0.8f);
                    
                    // Send feedback message
                    if (context.getPlayer() != null) {
                        context.getPlayer().sendMessage(Text.literal("§7§oThe obelisk shatters into fragments..."), true);
                    }
                    
                    return ActionResult.SUCCESS;
                } else {
                    // Normal texture cycling
                    obeliskEntity.cycleTextureVariant();
                    int newVariant = obeliskEntity.getTextureVariant();
                    
                    // Play chisel sound
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.5f, 1.2f);
                    
                    // Send feedback message
                    if (context.getPlayer() != null) {
                        context.getPlayer().sendMessage(Text.literal("§7§oThe obelisk's surface shifts to reveal a new pattern..."), true);
                    }
                    
                    return ActionResult.SUCCESS;
                }
            }
        }
        
        return ActionResult.PASS;
    }
}
