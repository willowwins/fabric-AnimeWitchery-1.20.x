package net.willowins.animewitchery.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ModTotemItem extends Item {


    public ModTotemItem(Settings settings) {
        super(settings);
    }

    // Optional: Override use animation if you want
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    // Optional: If you want to add custom right-click behavior
    @Override
    public TypedActionResult<ItemStack> use(World world, net.minecraft.entity.player.PlayerEntity user, Hand hand) {
        // Just pass through - no special right-click behavior by default
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}