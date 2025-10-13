package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.custom.SpellbookItem;
import net.willowins.animewitchery.item.custom.SpellScrollItem;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellEntry;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellPosition;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellTargeting;

import java.util.List;
import java.util.Random;

/**
 * Handles spellbook item interactions for adding/modifying spells
 * This works in conjunction with the enchanting table interaction
 */
public class SpellbookInteractionHandler {

    private static final Random RANDOM = new Random();

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) return TypedActionResult.pass(ItemStack.EMPTY);

            ItemStack heldItem = player.getStackInHand(hand);
            
            // Only handle spell scroll interactions
            if (!(heldItem.getItem() instanceof SpellScrollItem)) {
                return TypedActionResult.pass(heldItem);
            }

            // Check if player is holding a spellbook in the other hand
            ItemStack otherHand = hand == Hand.MAIN_HAND ? player.getOffHandStack() : player.getMainHandStack();
            if (!(otherHand.getItem() instanceof SpellbookItem)) {
                return TypedActionResult.pass(heldItem);
            }

            // Check if near an enchanting table (within 5 blocks)
            BlockPos tablePos = findNearbyEnchantingTable(world, player.getBlockPos());
            if (tablePos == null) {
                return TypedActionResult.pass(heldItem);
            }

            // Handle spell addition from spell scroll
            ActionResult result = handleSpellAddition(player, world, otherHand, heldItem, tablePos);
            return new TypedActionResult<>(result, heldItem);
        });
    }

    private static BlockPos findNearbyEnchantingTable(World world, BlockPos playerPos) {
        for (int x = -5; x <= 5; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (world.getBlockState(pos).isOf(Blocks.ENCHANTING_TABLE)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private static ActionResult handleSpellAddition(PlayerEntity player, World world, ItemStack spellbook, ItemStack spellScroll, BlockPos tablePos) {
        AdvancedSpellConfiguration config = SpellbookItem.getAdvancedConfiguration(spellbook);
        if (config == null) {
            config = new AdvancedSpellConfiguration("Advanced Spells");
        }

        // Get spell name from the spell scroll
        if (!(spellScroll.getItem() instanceof SpellScrollItem scrollItem)) {
            return ActionResult.PASS;
        }
        
        String spellName = scrollItem.getSpellName();

        // Create new spell entry with random properties
        SpellEntry newSpell = new SpellEntry(
            spellName,
            SpellPosition.values()[RANDOM.nextInt(SpellPosition.values().length)],
            SpellTargeting.values()[RANDOM.nextInt(SpellTargeting.values().length)],
            new int[]{0, 100, 250, 500, 750, 1000, 1250, 1500, 1750}[RANDOM.nextInt(9)],
            RANDOM.nextInt(5) + 1, // 1-5 multiplicity
            net.minecraft.util.math.Vec3d.ZERO, // offset
            0.0f, // yawOffset
            0.0f  // pitchOffset
        );

        config.addSpell(newSpell);
        SpellbookItem.saveAdvancedConfiguration(spellbook, config);

        player.sendMessage(Text.literal("§aAdded " + spellName + " spell!"), false);
        player.sendMessage(Text.literal("§7Position: " + newSpell.getPosition().getDisplayName() + 
            ", Target: " + newSpell.getTargeting().getDisplayName() + 
            ", Delay: " + newSpell.getDelay() + "ms, Multi: x" + newSpell.getMultiplicity()), false);

        world.playSound(null, tablePos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
                SoundCategory.BLOCKS, 1.0f, 1.5f);

        // Consume spell scroll
        spellScroll.decrement(1);

        return ActionResult.SUCCESS;
    }
}
