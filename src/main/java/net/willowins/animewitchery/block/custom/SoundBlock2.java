package net.willowins.animewitchery.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.willowins.animewitchery.sound.ModSounds;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoundBlock2 extends Block {
    public SoundBlock2(Settings settings) {
        super(settings);

    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {

        world.playSound(player, pos, ModSounds.LEMON_HATSUNE_MIKU, SoundCategory.BLOCKS, 1f,1f);
        return ActionResult.SUCCESS;

    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.literal("the gift of hatsune miku"));
        super.appendTooltip(stack, world, tooltip, options);
    }
}
