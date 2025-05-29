package net.willowins.animewitchery.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class DeepslatDiamondOreMixinMixin {

    @Inject(method = "onUse", at = @At("HEAD"))
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.getBlockState(pos).isOf(Blocks.DEEPSLATE_DIAMOND_ORE) && !world.isClient) {
            if (player.getMainHandStack().isOf(Blocks.STONE.asItem())){
                world.setBlockState(pos,Blocks.DIAMOND_ORE.getDefaultState());
                player.getMainHandStack().decrement(1);
                world.playSound(null, pos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1f,1f);


            } else if (player.getOffHandStack().isOf(Items.COPPER_INGOT)) {
                Block.dropStack(world,pos,new ItemStack(Blocks.DEEPSLATE_DIAMOND_ORE.asItem(),1));
                player.getOffHandStack().decrement(1);
                world.playSound(null, pos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1f,1f);

            }
        }
    }

}
