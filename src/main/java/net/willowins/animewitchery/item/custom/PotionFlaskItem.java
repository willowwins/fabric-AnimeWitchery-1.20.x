package net.willowins.animewitchery.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PotionFlaskItem extends PotionItem {

    public PotionFlaskItem(Settings settings) {
        super(settings);
    }

    private static final int MAX_CHARGES = 4;
    private static final int BAR_COLOR = 0x3d3dec; // Deep blue/Indigo

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity) user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) playerEntity, stack);
        }

        if (!world.isClient) {
            for (StatusEffectInstance effect : PotionUtil.getPotionEffects(stack)) {
                if (effect.getEffectType().isInstant()) {
                    effect.getEffectType().applyInstantEffect(playerEntity, playerEntity, user, effect.getAmplifier(),
                            1.0D);
                } else {
                    user.addStatusEffect(new StatusEffectInstance(effect));
                }
            }
        }

        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                int currentCharges = getCharges(stack);
                if (currentCharges > 1) {
                    setCharges(stack, currentCharges - 1);
                    return stack;
                } else {
                    // Last charge consumed
                    stack.decrement(1);
                }
            }
        }

        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(ModItems.EMPTY_FLASK);
            }

            if (playerEntity != null) {
                playerEntity.getInventory().insertStack(new ItemStack(ModItems.EMPTY_FLASK));
            }
        }

        return stack;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getCharges(stack) < MAX_CHARGES;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round((float) getCharges(stack) / MAX_CHARGES * 13.0F);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    private int getCharges(ItemStack stack) {
        if (!stack.hasNbt() || !stack.getNbt().contains("animewitchery_charges")) {
            return MAX_CHARGES; // Default to full
        }
        return stack.getNbt().getInt("animewitchery_charges");
    }

    private void setCharges(ItemStack stack, int charges) {
        stack.getOrCreateNbt().putInt("animewitchery_charges", Math.max(0, Math.min(charges, MAX_CHARGES)));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Volatile Catalyst:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Hold in Offhand to double thrown potion effects.").formatted(Formatting.GREEN));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
