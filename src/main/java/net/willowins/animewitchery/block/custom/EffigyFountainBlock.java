package net.willowins.animewitchery.block.custom;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.mixin.LivingEntityMixin;


public class EffigyFountainBlock extends Block {
    public EffigyFountainBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
     if (!world.isClient){
      EntityType.LIGHTNING_BOLT.spawn((ServerWorld) world, pos, SpawnReason.EVENT);
      player.sendMessage(Text.literal("A NEW HAND TOUCHES THE BEACON"),true);
      world.setBlockState(pos,ModBlocks.ACTIVE_EFFIGY_FOUNTAIN.getDefaultState());
      player.damage(player.getDamageSources().cramming(),2);


     }
     return ActionResult.SUCCESS;}

}
