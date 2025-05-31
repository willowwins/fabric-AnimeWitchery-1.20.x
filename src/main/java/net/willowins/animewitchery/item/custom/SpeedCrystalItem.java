package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpeedCrystalItem extends Item {
    public SpeedCrystalItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if(user.getMainHandStack().isOf(SpeedCrystalItem.this)){
                user.getMainHandStack().decrement(1);
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED,2400,2,true,true));
                user.getItemCooldownManager().set(user.getMainHandStack().getItem(),1200);
            } else if (user.getOffHandStack().isOf(SpeedCrystalItem.this)) {
                user.getOffHandStack().decrement(1);
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED,2400,2,true,true));
                user.getItemCooldownManager().set(user.getOffHandStack().getItem(),1200);
            }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(user.getMainHandStack().isOf(SpeedCrystalItem.this)){
            user.getMainHandStack().decrement(1);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED,2400,2,true,true));
        } else if (user.getOffHandStack().isOf(SpeedCrystalItem.this)) {
            user.getOffHandStack().decrement(1);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED,2400,2,true,true));
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("gotta go fast"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
