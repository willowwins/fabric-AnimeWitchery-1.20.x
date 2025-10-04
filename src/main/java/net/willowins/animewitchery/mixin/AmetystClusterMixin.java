package net.willowins.animewitchery.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
public class AmetystClusterMixin {

    @Inject(method = "onUse", at = @At("HEAD"))
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.getBlockState(pos).isOf(Blocks.AMETHYST_CLUSTER) && !world.isClient) {
            if (player.getMainHandStack().isOf(ModItems.BLAZE_SACK)){
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                Block.dropStack(world,pos,new ItemStack(ModItems.ALCHEMICAL_CATALYST,1));
                player.getMainHandStack().decrement(1);


            }else if (player.getMainHandStack().isOf(Blocks.CRYING_OBSIDIAN.asItem())&& player.getOffHandStack().isOf(Blocks.CHISELED_QUARTZ_BLOCK.asItem())){
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                Block.dropStack(world,pos,new ItemStack(ModItems.STAFF_HEAD,1));
                player.getMainHandStack().decrement(1);
                player.getOffHandStack().decrement(1);

            }else if(player.getMainHandStack().isOf(ModItems.SILVERSPOOL)&& player.getOffHandStack().isOf(ModItems.HEALING_STAFF)){
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                Block.dropStack(world,pos,new ItemStack(ModItems.SILVER_PENDANT,1));
                player.getMainHandStack().decrement(1);
        }
    }
}
}
