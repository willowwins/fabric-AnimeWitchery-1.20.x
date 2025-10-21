package net.willowins.animewitchery.item.custom;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;

import java.util.List;

public class FulfillingTiaraItem extends TrinketItem {
    private static final int MANA_COST = 100;
    private static final float HUNGER_THRESHOLD = 10.0f; // 50% of 20 (max hunger)
    
    public FulfillingTiaraItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld().isClient || !(entity instanceof PlayerEntity player)) {
            return;
        }
        
        // Check every 20 ticks (1 second)
        if (entity.getWorld().getTime() % 20 != 0) {
            return;
        }
        
        // Check if hunger is at or below 50%
        if (player.getHungerManager().getFoodLevel() <= HUNGER_THRESHOLD) {
            IManaComponent manaComponent = ModComponents.PLAYER_MANA.get(player);
            
            if (manaComponent.getMana() >= MANA_COST) {
                // Consume mana
                manaComponent.setMana(manaComponent.getMana() - MANA_COST);
                
                // Restore hunger and saturation
                player.getHungerManager().setFoodLevel(20); // Full hunger
                player.getHungerManager().setSaturationLevel(20.0f); // Full saturation
            }
        }
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Automatically refills hunger at 50%").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Costs: 100 Mana").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Restores full hunger and saturation").formatted(Formatting.GREEN));
    }
}

