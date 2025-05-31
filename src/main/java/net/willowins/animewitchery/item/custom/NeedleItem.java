package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.projectile.NeedleProjectileEntity;

public class NeedleItem extends SwordItem {
    public NeedleItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && stack.hasEnchantments() && stack.getEnchantments().toString().contains("sliver_enchant")) {
            NeedleProjectileEntity needle = new NeedleProjectileEntity(world, user);
            needle.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 3.5f, 0.0f); // speed, inaccuracy
            world.spawnEntity(needle);

            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    net.minecraft.sound.SoundEvents.ENTITY_ARROW_SHOOT,
                    net.minecraft.sound.SoundCategory.PLAYERS,
                    0.5f, 1.0f);

            // Optional: damage item
            stack.damage(1, user, p -> p.sendToolBreakStatus(hand));
            user.getItemCooldownManager().set(user.getMainHandStack().getItem(),200);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

}
