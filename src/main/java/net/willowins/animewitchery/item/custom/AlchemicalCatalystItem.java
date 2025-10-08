package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlchemicalCatalystItem extends Item {

    public static AlchemicalCatalystItem INSTANCE; // Reference for registry
    public static final String MANA_KEY = "StoredMana";
    public static final int MAX_MANA = 10000;

    public AlchemicalCatalystItem(Settings settings) {
        super(settings);
        INSTANCE = this;
    }

    // === NBT helpers ===
    public static int getStoredMana(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.getInt(MANA_KEY);
    }

    public static void setStoredMana(ItemStack stack, int amount) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(MANA_KEY, Math.max(0, Math.min(amount, MAX_MANA)));
    }

    // === Right-click actions ===
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            IManaComponent playerMana = ModComponents.PLAYER_MANA.get(user);
            int stored = getStoredMana(stack);
            int playerCurrent = playerMana.getMana();
            int playerMax = playerMana.getMaxMana();

            if (user.isSneaking()) {
                // --- Discharge from item to player ---
                if (stored > 0) {
                    int space = playerMax - playerCurrent;
                    int transfer = Math.min(stored, space);

                    if (transfer > 0) {
                        playerMana.setMana(playerCurrent + transfer);
                        setStoredMana(stack, stored - transfer);
                        user.sendMessage(Text.literal("Discharged " + transfer + " mana to player").formatted(Formatting.GREEN), true);
                    } else {
                        user.sendMessage(Text.literal("You are already at full mana").formatted(Formatting.YELLOW), true);
                    }
                } else {
                    user.sendMessage(Text.literal("Catalyst is empty").formatted(Formatting.GRAY), true);
                }

            } else {
                // --- Charge item from player mana ---
                if (stored < MAX_MANA) {
                    int space = MAX_MANA - stored;
                    int transferable = Math.min(playerCurrent, space);

                    if (transferable > 0) {
                        boolean consumed = playerMana.consume(transferable);
                        if (consumed) {
                            setStoredMana(stack, stored + transferable);
                            user.sendMessage(Text.literal("Stored " + transferable + " mana in catalyst").formatted(Formatting.AQUA), true);
                        } else {
                            user.sendMessage(Text.literal("Failed to draw mana").formatted(Formatting.RED), true);
                        }
                    } else {
                        user.sendMessage(Text.literal("You have no mana to charge with").formatted(Formatting.GRAY), true);
                    }
                } else {
                    user.sendMessage(Text.literal("Catalyst already full").formatted(Formatting.YELLOW), true);
                }
            }
        }

        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    // === Tooltip ===
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int stored = getStoredMana(stack);
        tooltip.add(Text.literal("Stored Mana: " + stored + " / " + MAX_MANA).formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Counts toward total mana").formatted(Formatting.DARK_PURPLE));
    }

    // === Durability bar as mana meter ===
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round((float) getStoredMana(stack) / MAX_MANA * 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x3B9EFF; // Bright blue
    }
}
