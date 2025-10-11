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
        
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            // Check if it's already raining/thundering
            boolean isRaining = serverWorld.isRaining();
            boolean isThundering = serverWorld.isThundering();
            
            if (player.isSneaking()) {
                // Shift + Right-click: Clear weather
                if (!isRaining && !isThundering) {
                    if (player instanceof ServerPlayerEntity sp) {
                        sp.sendMessage(Text.literal("§7§oThe weather is already clear."), true);
                    }
                    return TypedActionResult.pass(stack);
                }
                
                if (!ManaHelper.consumeCostFromPlayerAndCatalysts(player, CLEAR_MANA_COST)) {
                    if (player instanceof ServerPlayerEntity sp) {
                        sp.sendMessage(Text.literal("§7§oNot enough mana. Need " + CLEAR_MANA_COST + " mana."), true);
                    }
                    return TypedActionResult.fail(stack);
                }
                
                serverWorld.setWeather(6000, 0, false, false); // Clear for 5 minutes
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS, 1.0f, 1.5f);
                
                if (player instanceof ServerPlayerEntity sp) {
                    sp.sendMessage(Text.literal("§e⛅ Weather cleared! (-" + CLEAR_MANA_COST + " mana)"), true);
                }
                
                player.getItemCooldownManager().set(this, 40); // 2 second cooldown
                return TypedActionResult.success(stack);
                
            } else if (isThundering) {
                // Already thundering, can't make it more intense
                if (player instanceof ServerPlayerEntity sp) {
                    sp.sendMessage(Text.literal("§7§oThe storm is already at maximum intensity!"), true);
                }
                return TypedActionResult.pass(stack);
                
            } else if (isRaining) {
                // Right-click while raining: Upgrade to thunderstorm
                if (!ManaHelper.consumeCostFromPlayerAndCatalysts(player, THUNDER_MANA_COST)) {
                    if (player instanceof ServerPlayerEntity sp) {
                        sp.sendMessage(Text.literal("§7§oNot enough mana. Need " + THUNDER_MANA_COST + " mana."), true);
                    }
                    return TypedActionResult.fail(stack);
                }
                
                serverWorld.setWeather(0, 6000, true, true); // Thunder for 5 minutes
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 1.0f, 1.0f);
                
                if (player instanceof ServerPlayerEntity sp) {
                    sp.sendMessage(Text.literal("§1⚡ Thunderstorm summoned! (-" + THUNDER_MANA_COST + " mana)"), true);
                }
                
                player.getItemCooldownManager().set(this, 60); // 3 second cooldown
                return TypedActionResult.success(stack);
                
            } else {
                // Right-click clear weather: Make it rain
                if (!ManaHelper.consumeCostFromPlayerAndCatalysts(player, RAIN_MANA_COST)) {
                    if (player instanceof ServerPlayerEntity sp) {
                        sp.sendMessage(Text.literal("§7§oNot enough mana. Need " + RAIN_MANA_COST + " mana."), true);
                    }
                    return TypedActionResult.fail(stack);
                }
                
                serverWorld.setWeather(0, 6000, true, false); // Rain for 5 minutes
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.PLAYERS, 1.0f, 0.8f);
                
                if (player instanceof ServerPlayerEntity sp) {
                    sp.sendMessage(Text.literal("§9☔ Rain summoned! (-" + RAIN_MANA_COST + " mana)"), true);
                }
                
                player.getItemCooldownManager().set(this, 40); // 2 second cooldown
                return TypedActionResult.success(stack);
            }
        }

        return TypedActionResult.pass(stack);
    }
}
