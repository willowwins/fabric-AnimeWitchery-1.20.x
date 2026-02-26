package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class RevolverItem extends Item {
    public RevolverItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // Ensure NBT exists
        if (!stack.hasNbt()) {
            stack.setNbt(new NbtCompound());
            stack.getNbt().putInt("Ammo", 0);
        }

        int currentAmmo = stack.getNbt().getInt("Ammo");

        // Reload Logic (Sneak + Right Click)
        if (user.isSneaking()) {
            if (currentAmmo >= 6) {
                return TypedActionResult.fail(stack);
            }

            // Search for bullets
            ItemStack bulletStack = ItemStack.EMPTY;
            if (user.getAbilities().creativeMode) {
                // Creative reload
                stack.getNbt().putInt("Ammo", 6);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                        SoundCategory.PLAYERS, 1.0f, 1.0f);
                user.getItemCooldownManager().set(this, 40); // 2 sec reload
                return TypedActionResult.success(stack);
            }

            // Survival reload
            for (int i = 0; i < user.getInventory().size(); i++) {
                ItemStack s = user.getInventory().getStack(i);
                if (s.getItem() == ModItems.BULLET) {
                    bulletStack = s;
                    break;
                }
            }

            if (!bulletStack.isEmpty()) {
                int needed = 6 - currentAmmo;
                int toTake = Math.min(needed, bulletStack.getCount());

                bulletStack.decrement(toTake);
                stack.getNbt().putInt("Ammo", currentAmmo + toTake);

                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                        SoundCategory.PLAYERS, 1.0f, 1.0f);
                user.getItemCooldownManager().set(this, 40); // 2 sec reload

                return TypedActionResult.success(stack);
            } else {
                // No ammo
                return TypedActionResult.fail(stack);
            }
        }

        // Shoot Logic
        if (currentAmmo > 0) {
            if (!world.isClient) {
                // Determine projectile based on level/bonuses (Placeholder: Arrow for now,
                // maybe custom BulletEntity later)
                // For now, we simulate a hitscan or high-velocity arrow
                // Using standard arrow for simplicity of damaging mechanics
                PersistentProjectileEntity projectile = new net.minecraft.entity.projectile.ArrowEntity(world, user);
                projectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 5.0F, 1.0F); // High velocity (5.0)
                projectile.setDamage(projectile.getDamage() + 2.0); // High base damage
                projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED; // Can't pick up bullets

                world.spawnEntity(projectile);

                stack.getNbt().putInt("Ammo", currentAmmo - 1);
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE,
                    SoundCategory.PLAYERS, 0.5f, 2.0f); // Pop sound
            user.getItemCooldownManager().set(this, 10); // Recoil/Fire rate

            return TypedActionResult.success(stack);
        } else {
            // Empty click
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL,
                    SoundCategory.PLAYERS, 1.0f, 2.0f);
            return TypedActionResult.fail(stack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int ammo = 0;
        if (stack.hasNbt()) {
            ammo = stack.getNbt().getInt("Ammo");
        }
        tooltip.add(Text.literal("Ammo: " + ammo + "/6").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Sneak + Right Click to Reload").formatted(Formatting.YELLOW));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
