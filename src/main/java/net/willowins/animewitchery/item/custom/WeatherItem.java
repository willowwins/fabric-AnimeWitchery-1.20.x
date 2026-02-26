package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.willowins.animewitchery.mana.ManaHelper;

public class WeatherItem extends Item {
    private static final int RAIN_MANA_COST = 1000;
    private static final int THUNDER_MANA_COST = 2000;
    private static final int CLEAR_MANA_COST = 500;

    public WeatherItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Ensure NBT
        if (!stack.hasNbt()) {
            stack.getOrCreateNbt().putInt("Mode", 1);
        }

        if (player.isSneaking()) {
            // Cycle Mode
            int currentMode = stack.getNbt().getInt("Mode");
            int newMode = (currentMode + 1) % 3;
            stack.getNbt().putInt("Mode", newMode);

            if (!world.isClient) {
                String modeName = switch (newMode) {
                    case 0 -> "§eSunny";
                    case 1 -> "§9Rainy";
                    case 2 -> "§5Thunder";
                    default -> "Unknown";
                };
                player.sendMessage(Text.literal("§7Weather Mode: " + modeName), true);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 0.5f, 1.2f);
            }
            return TypedActionResult.success(stack);
        }

        // Execute Weather Change
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            int mode = stack.getNbt().getInt("Mode");

            if (mode == 0) { // Sunny
                if (!ManaHelper.consumeCostFromPlayerAndCatalysts(player, CLEAR_MANA_COST)) {
                    player.sendMessage(Text.literal("§cNot enough mana (" + CLEAR_MANA_COST + ")"), true);
                    return TypedActionResult.fail(stack);
                }
                serverWorld.setWeather(6000, 0, false, false);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.5f);
                player.sendMessage(Text.literal("§eWeather cleared!"), true);
            } else if (mode == 1) { // Rain
                if (!ManaHelper.consumeCostFromPlayerAndCatalysts(player, RAIN_MANA_COST)) {
                    player.sendMessage(Text.literal("§cNot enough mana (" + RAIN_MANA_COST + ")"), true);
                    return TypedActionResult.fail(stack);
                }
                serverWorld.setWeather(0, 6000, true, false);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.PLAYERS, 1.0f, 0.8f);
                player.sendMessage(Text.literal("§9Rain summoned!"), true);
            } else if (mode == 2) { // Thunder
                if (!ManaHelper.consumeCostFromPlayerAndCatalysts(player, THUNDER_MANA_COST)) {
                    player.sendMessage(Text.literal("§cNot enough mana (" + THUNDER_MANA_COST + ")"), true);
                    return TypedActionResult.fail(stack);
                }
                serverWorld.setWeather(0, 6000, true, true);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 1.0f, 1.0f);
                player.sendMessage(Text.literal("§5Thunderstorm summoned!"), true);
            }

            player.getItemCooldownManager().set(this, 60);
            return TypedActionResult.success(stack);
        }

        return TypedActionResult.success(stack);
    }

    public static float getMode(ItemStack stack) {
        if (!stack.hasNbt()) {
            return 1.0f; // Default to Rain
        }
        int modeInt = stack.getNbt().getInt("Mode");
        return modeInt / 2.0f; // 0.0, 0.5, 1.0
    }
}
