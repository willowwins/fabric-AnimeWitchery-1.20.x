package net.willowins.animewitchery.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.willowins.animewitchery.fluid.ModFluids;

public class StarlightBucketItem extends BucketItem {

    public StarlightBucketItem(Fluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);

        if (hitResult.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        }

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        }

        BlockPos blockPos = hitResult.getBlockPos();
        Direction direction = hitResult.getSide();
        BlockPos offsetPos = blockPos.offset(direction);

        if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(offsetPos, direction, itemStack)) {
            return TypedActionResult.fail(itemStack);
        }

        // Try to fill the block with starlight
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() instanceof FluidFillable) {
            FluidFillable fluidFillable = (FluidFillable) blockState.getBlock();

            System.out.println("[BUCKET DEBUG] Found FluidFillable block: " + blockState.getBlock());
            System.out.println("[BUCKET DEBUG] Can fill? "
                    + fluidFillable.canFillWithFluid(world, blockPos, blockState, ModFluids.STILL_STARLIGHT));

            if (fluidFillable.canFillWithFluid(world, blockPos, blockState, ModFluids.STILL_STARLIGHT)) {
                System.out.println("[BUCKET DEBUG] Attempting to fill with starlight");
                fluidFillable.tryFillWithFluid(world, blockPos, blockState,
                        ModFluids.STILL_STARLIGHT.getDefaultState());

                SoundEvent soundEvent = ModFluids.STILL_STARLIGHT.getBucketFillSound()
                        .orElse(SoundEvents.ITEM_BUCKET_EMPTY);
                world.playSound(user, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(user, GameEvent.FLUID_PLACE, blockPos);

                user.incrementStat(Stats.USED.getOrCreateStat(this));
                if (user instanceof ServerPlayerEntity) {
                    Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity) user, blockPos, itemStack);
                }

                if (user.getAbilities().creativeMode) {
                    return TypedActionResult.success(itemStack, world.isClient());
                } else {
                    return TypedActionResult.success(new ItemStack(Items.BUCKET), world.isClient());
                }
            }
        }

        // Fall back to default bucket behavior (placing as source block)
        return super.use(world, user, hand);
    }
}
