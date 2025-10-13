package net.willowins.animewitchery.util;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.ModItems;

public class BookInteractionHandler {
    
    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            
            // Only handle right-click interactions
            if (hand == Hand.MAIN_HAND) {
                return handleBookInteraction(player, world, hand, stack);
            }
            
            return TypedActionResult.pass(stack);
        });
    }
    
    private static TypedActionResult<ItemStack> handleBookInteraction(PlayerEntity player, World world, Hand hand, ItemStack heldItem) {
        if (world.isClient) {
            return TypedActionResult.pass(heldItem);
        }
        
        // Check if the player is holding raw silver in their off-hand
        ItemStack offhandStack = player.getOffHandStack();
        if (!offhandStack.isOf(ModItems.RAWSILVER)) {
            return TypedActionResult.pass(heldItem);
        }
        
        // Check if the item being right-clicked is a book
        Item heldItemType = heldItem.getItem();
        if (!isBookItem(heldItemType)) {
            return TypedActionResult.pass(heldItem);
        }
        
        // Check if player has space in inventory
        if (!player.getInventory().insertStack(new ItemStack(ModItems.RITUALS_BOOK))) {
            player.sendMessage(Text.translatable("message.animewitchery.inventory_full"), true);
            return TypedActionResult.pass(heldItem);
        }
        
        // Consume the raw silver
        offhandStack.decrement(1);
        
        // Send success message
        player.sendMessage(Text.translatable("message.animewitchery.rituals_book_created"), true);
        
        AnimeWitchery.LOGGER.info("Player {} created a Grimoire of Quarters using a book and raw silver", player.getName().getString());
        
        return TypedActionResult.success(heldItem);
    }
    
    private static boolean isBookItem(Item item) {
        return item == Items.BOOK || 
               item == Items.ENCHANTED_BOOK ||
               item == Items.WRITTEN_BOOK ||
               item == Items.WRITABLE_BOOK;
    }
}
