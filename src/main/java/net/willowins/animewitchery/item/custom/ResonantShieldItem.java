package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.custom.ResonantShieldEntity;

public class ResonantShieldItem extends Item {
    public ResonantShieldItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000; // Allow holding for a long time
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        if (!world.isClient) {
            ResonantShieldEntity shield = new ResonantShieldEntity(world, user);
            world.spawnEntity(shield);
        }

        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        // Ensure shield entity exists?
        // Actually the entity manages its own existence by checking owner.isUsingItem()
        // If we wanted to be stricter we could store UUID in stack NBT, but for now
        // the entity's own check is sufficient as long as we only spawn one.
        // But what if we spawn multiple by right clicking fast?
        // use() only happens once per right click hold.
        // So one entity per hold session.
    }
}
