package net.willowins.animewitchery.item.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TorchFlowerEssenceItem extends Item {
    public TorchFlowerEssenceItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (world.getBlockState(pos).isOf(Blocks.NETHER_WART_BLOCK)) {
            if (!world.isClient) {
                world.setBlockState(pos, Blocks.SHROOMLIGHT.getDefaultState());
                world.playSound(null, pos, SoundEvents.BLOCK_SHROOMLIGHT_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);

                PlayerEntity player = context.getPlayer();
                if (player != null && !player.getAbilities().creativeMode) {
                    context.getStack().decrement(1);
                }
            }
            return ActionResult.success(world.isClient);
        }
        return super.useOnBlock(context);
    }
}
