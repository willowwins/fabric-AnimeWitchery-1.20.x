package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.effect.ModEffect;

public class KamikazeRitualScroll extends Item {
    
    public KamikazeRitualScroll(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        
        if (!world.isClient) {
            // Only allow activation on server side
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            
            // Check if player already has the ritual bound
            if (player.hasStatusEffect(ModEffect.KAMIKAZE_RITUAL)) {
                player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §cYou are already bound to the Kamikaze Ritual!"));
                return TypedActionResult.fail(stack);
            }
            
            // Check if player has required components (nether star, dragon breath, etc.)
            if (!hasRequiredComponents(player)) {
                player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §cYou lack the required components to invoke this ritual!"));
                return TypedActionResult.fail(stack);
            }
            
            // Consume the scroll
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
            
            // Consume required components
            consumeRequiredComponents(player);
            
            // Apply the ritual effect
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                ModEffect.KAMIKAZE_RITUAL, 
                Integer.MAX_VALUE, // Permanent until death
                0, 
                false, 
                true, 
                true
            ));
            
            // Send dramatic message
            player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §cYour soul is now bound to the Kamikaze Ritual!"));
            player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §cUpon death, you will be erased from this world!"));
            
            // Play ritual activation sound and particles
            world.playSound(null, player.getBlockPos(), 
                net.minecraft.sound.SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 
                player.getSoundCategory(), 1.0f, 0.5f);
            
            // TODO: Add ritual circle particles and holograms
            
            return TypedActionResult.success(stack);
        }
        
        return TypedActionResult.success(stack);
    }
    
    private boolean hasRequiredComponents(PlayerEntity player) {
        // Check for nether star
        boolean hasNetherStar = player.getInventory().containsAny(item -> 
            item.getItem() == net.minecraft.item.Items.NETHER_STAR);
        
        // Check for dragon breath
        boolean hasDragonBreath = player.getInventory().containsAny(item -> 
            item.getItem() == net.minecraft.item.Items.DRAGON_BREATH);
        
        // Check for blood rune stone
        boolean hasBloodRune = player.getInventory().containsAny(item -> 
            item.getItem().toString().contains("blood_rune_stone"));
        
        return hasNetherStar && hasDragonBreath && hasBloodRune;
    }
    
    private void consumeRequiredComponents(PlayerEntity player) {
        // Remove nether star
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == net.minecraft.item.Items.NETHER_STAR) {
                stack.decrement(1);
                break;
            }
        }
        
        // Remove dragon breath
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == net.minecraft.item.Items.DRAGON_BREATH) {
                stack.decrement(1);
                break;
            }
        }
        
        // Remove blood rune stone
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem().toString().contains("blood_rune_stone")) {
                stack.decrement(1);
                break;
            }
        }
    }
}
