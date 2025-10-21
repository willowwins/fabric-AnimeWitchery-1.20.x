package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;

import java.util.List;

public class FulfillingResonantBandItem extends Item {
    private static final int MANA_COST = 100;
    private static final float HUNGER_THRESHOLD = 10.0f; // 50% of 20 (max hunger)
    
    public FulfillingResonantBandItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof PlayerEntity player)) {
            return;
        }
        
        // Check every 20 ticks (1 second)
        if (world.getTime() % 20 != 0) {
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
                
                // Apply Regeneration effect (10 seconds, level I)
                player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 
                    200, // 10 seconds
                    0,   // Regeneration I
                    false, 
                    false, 
                    true
                ));
            }
        }
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Automatically refills hunger at 50%").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Costs: 100 Mana").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Restores full hunger and saturation").formatted(Formatting.GREEN));
        tooltip.add(Text.literal("Grants Regeneration I (10s)").formatted(Formatting.LIGHT_PURPLE));
    }
}

