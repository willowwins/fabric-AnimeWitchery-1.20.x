package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Random;

public class DeckOfCardsItem extends Item {
    private final Random random = new Random();

    public DeckOfCardsItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            int card = random.nextInt(6);
            StatusEffectInstance effect = switch (card) {
                case 0 -> new StatusEffectInstance(StatusEffects.STRENGTH, 400, 1);
                case 1 -> new StatusEffectInstance(StatusEffects.SPEED, 400, 1);
                case 2 -> new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1);
                case 3 -> new StatusEffectInstance(StatusEffects.WEAKNESS, 400, 0); // Bad
                case 4 -> new StatusEffectInstance(StatusEffects.SLOWNESS, 400, 1); // Bad
                case 5 -> new StatusEffectInstance(StatusEffects.LUCK, 1200, 1);
                default -> null;
            };

            if (effect != null) {
                user.addStatusEffect(effect);
            }

            user.getItemCooldownManager().set(this, 60);
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN,
                SoundCategory.PLAYERS, 1.0F, 1.0F);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
