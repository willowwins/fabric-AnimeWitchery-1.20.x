package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EmergencyRecallItem extends Item {
    private static final float HEALTH_THRESHOLD = 4.0f; // 2 hearts
    
    public EmergencyRecallItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof ServerPlayerEntity player)) {
            return;
        }
        
        // Check every 5 ticks
        if (world.getTime() % 5 != 0) {
            return;
        }
        
        // Check if health is at or below threshold
        if (player.getHealth() <= HEALTH_THRESHOLD) {
            // Get spawn point
            ServerWorld spawnWorld = player.getServer().getWorld(player.getSpawnPointDimension());
            BlockPos spawnPos = player.getSpawnPointPosition();
            
            if (spawnWorld != null && spawnPos != null) {
                // Teleport to spawn
                player.teleport(spawnWorld, 
                    spawnPos.getX() + 0.5, 
                    spawnPos.getY(), 
                    spawnPos.getZ() + 0.5, 
                    player.getYaw(), 
                    player.getPitch());
                
                // Play teleport sound
                spawnWorld.playSound(null, spawnPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, 
                    SoundCategory.PLAYERS, 1.0f, 1.0f);
                
                // Send message
                player.sendMessage(Text.literal("Emergency Recall activated!").formatted(Formatting.LIGHT_PURPLE), false);
                
                // Consume the item
                stack.decrement(1);
            }
        }
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Teleports to spawn at 4 HP or less").formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.literal("Consumed on use").formatted(Formatting.RED));
        tooltip.add(Text.literal("Must be in inventory").formatted(Formatting.GRAY));
    }
}

