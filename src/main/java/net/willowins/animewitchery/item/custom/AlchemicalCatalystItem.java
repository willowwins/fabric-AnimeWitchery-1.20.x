package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import net.willowins.animewitchery.mana.ManaComponent;
import org.jetbrains.annotations.Nullable;
import java.util.List;

import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.mana.IManaComponent;

public class AlchemicalCatalystItem extends Item {
    private static final String MANA_KEY = "StoredMana";
    private static final int MAX_MANA = 10000; // catalyst capacity

    public AlchemicalCatalystItem(Settings settings) {
        super(settings);
    }

    // === Item NBT Storage ===
    public static int getStoredMana(ItemStack stack) {
        return stack.getOrCreateNbt().getInt(MANA_KEY);
    }

    public static void setStoredMana(ItemStack stack, int amount) {
        stack.getOrCreateNbt().putInt(MANA_KEY, Math.max(0, Math.min(amount, MAX_MANA)));
    }

    // === Right-click to extract from player ===
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            IManaComponent playerMana = ModComponents.PLAYER_MANA.get(user);

            if (getStoredMana(stack) == 0) {
                if (playerMana.consume(MAX_MANA)) {
                    setStoredMana(stack, MAX_MANA);
                    user.sendMessage(Text.literal("The Catalyst is now charged!").formatted(Formatting.AQUA), true);
                } else {
                    user.sendMessage(Text.literal("You lack sufficient mana!").formatted(Formatting.RED), true);
                }
            } else {
                user.sendMessage(Text.literal("The Catalyst is already charged.").formatted(Formatting.YELLOW), true);
            }
        }


        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    // === Tooltip ===
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Stored Mana: " + getStoredMana(stack) + " / " + MAX_MANA).formatted(Formatting.AQUA));
    }

    // === Durability bar as mana meter ===
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true; // always visible
    }


    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round((float) getStoredMana(stack) / MAX_MANA * 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x3B9EFF; // mana blue
    }


}
