package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RepairEssenceItem extends Item {
    
    public RepairEssenceItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack essenceStack = player.getStackInHand(hand);
        ItemStack offHandStack = player.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
        
        // Check if the other hand has a damageable item
        if (!offHandStack.isEmpty() && offHandStack.isDamageable() && offHandStack.getDamage() > 0) {
            if (!world.isClient) {
                // Calculate repair amount (2000 durability)
                int currentDamage = offHandStack.getDamage();
                int repairAmount = Math.min(currentDamage, 2000);
                
                // Repair the item
                offHandStack.setDamage(currentDamage - repairAmount);
                
                // Consume one repair essence
                essenceStack.decrement(1);
                
                // Play repair sound
                world.playSound(null, player.getX(), player.getY(), player.getZ(), 
                              SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 0.8f, 1.2f);
                
                // Send feedback message
                player.sendMessage(
                    Text.literal("✦ Repaired " + offHandStack.getName().getString() + " by " + repairAmount + " durability.")
                        .formatted(Formatting.AQUA),
                    true
                );
            }
            
            return TypedActionResult.success(essenceStack, world.isClient);
        }
        
        // No valid item to repair
        if (!world.isClient && !offHandStack.isEmpty()) {
            player.sendMessage(
                Text.literal("⚠️ This item cannot be repaired or is already at full durability.")
                    .formatted(Formatting.GRAY),
                true
            );
        }
        
        return TypedActionResult.pass(essenceStack);
    }
}

