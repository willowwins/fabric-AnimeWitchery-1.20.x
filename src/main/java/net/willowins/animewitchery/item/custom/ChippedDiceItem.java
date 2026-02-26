package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChippedDiceItem extends Item {
    private final Random random = new Random();
    private static final List<Potion> POTION_POOL = Arrays.asList(
            Potions.REGENERATION,
            Potions.SWIFTNESS,
            Potions.STRENGTH,
            Potions.HEALING,
            Potions.POISON,
            Potions.WEAKNESS,
            Potions.SLOWNESS,
            Potions.HARMING);

    public ChippedDiceItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            // Play Dice Roll Sound
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_SNARE.value(), SoundCategory.PLAYERS, 1.0f, 2.0f);

            // Select Random Potion
            Potion randomPotion = POTION_POOL.get(random.nextInt(POTION_POOL.size()));

            // Create Splash Potion Item Stack
            ItemStack potionStack = new ItemStack(Items.SPLASH_POTION);
            PotionUtil.setPotion(potionStack, randomPotion);

            // Spawn Potion Entity
            PotionEntity potionEntity = new PotionEntity(world, user);
            potionEntity.setItem(potionStack);

            // Shoot it
            potionEntity.setVelocity(user, user.getPitch(), user.getYaw(), -20.0F, 0.5F, 1.0F);
            world.spawnEntity(potionEntity);

            // Cooldown
            user.getItemCooldownManager().set(this, 40); // 2 seconds
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
