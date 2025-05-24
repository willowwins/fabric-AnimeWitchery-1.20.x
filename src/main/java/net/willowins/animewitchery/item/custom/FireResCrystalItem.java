package net.willowins.animewitchery.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FireResCrystalItem extends Item {
    public FireResCrystalItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if(user.getMainHandStack().isOf(FireResCrystalItem.this)){
                user.getMainHandStack().decrement(1);
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,2400,1,true,true));
            } else if (user.getOffHandStack().isOf(FireResCrystalItem.this)) {
                user.getOffHandStack().decrement(1);
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,2400,1,true,true));
            }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(user.getMainHandStack().isOf(FireResCrystalItem.this)){
            user.getMainHandStack().decrement(1);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,2400,1,true,true));
        } else if (user.getOffHandStack().isOf(FireResCrystalItem.this)) {
            user.getOffHandStack().decrement(1);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,2400,1,true,true));
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("basically a medkit"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
