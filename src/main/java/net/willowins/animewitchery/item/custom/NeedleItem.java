package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import net.willowins.animewitchery.entity.projectile.NeedleProjectileEntity;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.enchantments.ModEnchantments;
import net.willowins.animewitchery.item.custom.AlchemicalCatalystItem;

public class NeedleItem extends SwordItem {
    private static final int USE_MANA_COST = 100;

    public NeedleItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        int sliverLevel = EnchantmentHelper.getLevel(ModEnchantments.SLIVER, stack);
        if (sliverLevel > 0) {
            if (!world.isClient) {
                if (!tryConsumeFromPlayerAndCatalysts(user, USE_MANA_COST)) {
                    user.sendMessage(
                            Text.literal("Not enough mana or stored mana in catalysts to throw needle (requires "
                                    + USE_MANA_COST + ")")
                                    .formatted(Formatting.RED),
                            true);
                    return TypedActionResult.fail(stack);
                }

                // Spawn the projectile
                NeedleProjectileEntity needle = new NeedleProjectileEntity(world, user, stack);
                needle.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 3.5f, 0.0f);
                world.spawnEntity(needle);

                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        net.minecraft.sound.SoundEvents.ENTITY_ARROW_SHOOT,
                        net.minecraft.sound.SoundCategory.PLAYERS,
                        0.5f, 1.0f);

                // Damage the item
                stack.damage(1, user, p -> p.sendToolBreakStatus(hand));

                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                // Cooldown
                user.getItemCooldownManager().set(stack.getItem(), 30);
            }
            return TypedActionResult.success(stack, world.isClient());
        }

        return super.use(world, user, hand);
    }

    /**
     * Attempts to consume the given cost from player mana + catalysts.
     * Returns true if fully paid, false otherwise.
     */
    private boolean tryConsumeFromPlayerAndCatalysts(PlayerEntity player, int cost) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        int playerMana = mana.getMana();

        if (playerMana >= cost) {
            mana.consume(cost);
            return true;
        }

        int remaining = cost - playerMana;
        if (playerMana > 0) {
            mana.consume(playerMana);
        }

        // Drain from catalysts in inventory
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof AlchemicalCatalystItem) {
                int stored = AlchemicalCatalystItem.getStoredMana(stack);
                if (stored <= 0)
                    continue;
                int take = Math.min(stored, remaining);
                AlchemicalCatalystItem.setStoredMana(stack, stored - take);
                remaining -= take;
                if (remaining <= 0) {
                    break;
                }
            }
        }

        return (remaining <= 0);
    }
}
