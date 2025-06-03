package net.willowins.animewitchery.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.ItemScatterer;
import net.minecraft.entity.player.PlayerEntity;

public class NBTPreservingToolItem extends Item {

    public NBTPreservingToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            BlockState state = world.getBlockState(pos);

            if (be != null) {
                // Create a new item stack representing the block
                ItemStack droppedStack = new ItemStack(state.getBlock().asItem());

                // Save the full BlockEntity NBT
                NbtCompound blockEntityTag = be.createNbtWithIdentifyingData();
                NbtCompound stackTag = new NbtCompound();
                stackTag.put("BlockEntityTag", blockEntityTag);
                droppedStack.setNbt(stackTag);

                // Remove the block without dropping its default drops
                world.removeBlockEntity(pos); // Remove block entity first
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3); // Replace with air (no drop flags)

                // Drop the NBT-preserved item manually
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), droppedStack);

                // Consume the item
                if (!player.isCreative()) {
                    stack.decrement(1);
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}
