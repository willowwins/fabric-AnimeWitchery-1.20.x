package net.willowins.animewitchery.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.custom.SoundBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoundBlock extends BlockWithEntity {
    public SoundBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SoundBlockEntity soundEntity) {
                long currentTime = world.getTime();

                if (soundEntity.canPlay(currentTime)) {
                    world.playSound(null, pos, SoundEvents.MUSIC_DISC_OTHERSIDE, SoundCategory.BLOCKS, 1f, 1f);
                    soundEntity.markPlayed(currentTime);
                    return ActionResult.SUCCESS;
                } else {
                    // Send feedback to player that it's on cooldown
                    player.sendMessage(Text.literal("The music block is still resonating..."), true);
                    return ActionResult.CONSUME;
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.literal("the gift of good music is more valuable than any one thing"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoundBlockEntity(pos, state);
    }

    @Override
    public net.minecraft.block.BlockRenderType getRenderType(BlockState state) {
        return net.minecraft.block.BlockRenderType.MODEL;
    }
}
