package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.custom.SpellbookItem;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellEntry;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellPosition;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellTargeting;

import java.util.List;
import java.util.Random;

/**
 * Handles spellbook interactions with enchanting tables
 * Uses the enchanting table GUI to configure advanced spells
 */
public class SpellbookEnchantingHandler {

    private static final Random RANDOM = new Random();

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            if (!world.getBlockState(pos).isOf(Blocks.ENCHANTING_TABLE)) {
                return ActionResult.PASS;
            }

            ItemStack heldItem = player.getStackInHand(hand);
            if (!(heldItem.getItem() instanceof SpellbookItem)) {
                return ActionResult.PASS;
            }

            // Check if sneaking (for spell configuration)
            if (!player.isSneaking()) {
                return ActionResult.PASS;
            }

            // Handle spellbook enchanting table interaction
            return handleSpellbookEnchanting(player, world, heldItem, pos);
        });
    }

    private static ActionResult handleSpellbookEnchanting(net.minecraft.entity.player.PlayerEntity player, World world, ItemStack spellbook, BlockPos tablePos) {
        AdvancedSpellConfiguration config = SpellbookItem.getAdvancedConfiguration(spellbook);
        
        // Create default configuration if none exists
        if (config == null) {
            config = new AdvancedSpellConfiguration("Advanced Spells");
            SpellbookItem.saveAdvancedConfiguration(spellbook, config);
        }
        
        // Show current spell configuration
        player.sendMessage(Text.literal("§6📖 Advanced Spellbook Configuration"), false);
        player.sendMessage(Text.literal("§7Current spells: " + config.getSpells().size()), false);
        
        if (config.getSpells().isEmpty()) {
            player.sendMessage(Text.literal("§7No spells configured. Use spell scrolls to add spells!"), false);
            player.sendMessage(Text.literal("§7Right-click with a spell scroll while holding spellbook"), false);
            player.sendMessage(Text.literal("§7Craft spell scrolls at the alchemy table"), false);
        } else {
            // List current spells
            List<SpellEntry> spells = config.getSpells();
            for (int i = 0; i < spells.size(); i++) {
                SpellEntry spell = spells.get(i);
                player.sendMessage(Text.literal("§7" + (i + 1) + ". " + spell.getSpellName() + 
                    " [" + spell.getPosition().getDisplayName() + ", " + spell.getTargeting().getDisplayName() + 
                    ", " + spell.getDelay() + "ms, x" + spell.getMultiplicity() + "]"), false);
            }
            
            player.sendMessage(Text.literal("§7Right-click with items to modify spells:"), false);
            player.sendMessage(Text.literal("§7• Book + Coal = Remove last spell"), false);
            player.sendMessage(Text.literal("§7• Book + Charcoal = Clear all spells"), false);
            player.sendMessage(Text.literal("§7• Book + Blaze Powder = Cycle position of last spell"), false);
            player.sendMessage(Text.literal("§7• Book + Ghast Tear = Cycle target of last spell"), false);
            player.sendMessage(Text.literal("§7• Book + Glowstone Dust = Cycle delay of last spell"), false);
            player.sendMessage(Text.literal("§7• Book + Gunpowder = Cycle multiplicity of last spell"), false);
        }
        
        player.sendMessage(Text.literal("§7Right-click spellbook to cast spells!"), false);
        
        // Play enchanting table sound
        world.playSound(null, tablePos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
                SoundCategory.BLOCKS, 1.0f, 1.0f);
        
        return ActionResult.SUCCESS;
    }

    /**
     * Called when player right-clicks with items near an enchanting table while holding a spellbook
     */
    public static ActionResult handleSpellbookItemInteraction(net.minecraft.entity.player.PlayerEntity player, World world, ItemStack spellbook, ItemStack interactionItem, BlockPos tablePos) {
        if (world.isClient) return ActionResult.PASS;
        
        AdvancedSpellConfiguration config = SpellbookItem.getAdvancedConfiguration(spellbook);
        
        // Create default configuration if none exists
        if (config == null) {
            config = new AdvancedSpellConfiguration("Advanced Spells");
            SpellbookItem.saveAdvancedConfiguration(spellbook, config);
        }
        
        // Check for spell addition
        if (interactionItem.isOf(Items.BOOK)) {
            // This will be handled by the main interaction logic
            return ActionResult.PASS;
        }
        
        // Check for spell modifications
        if (interactionItem.isOf(Items.COAL)) {
            // Remove last spell
            if (!config.getSpells().isEmpty()) {
                config.removeSpell(config.getSpells().size() - 1);
                SpellbookItem.saveAdvancedConfiguration(spellbook, config);
                player.sendMessage(Text.literal("§cRemoved last spell"), false);
                world.playSound(null, tablePos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
                        SoundCategory.BLOCKS, 0.8f, 0.5f);
                return ActionResult.SUCCESS;
            }
        } else if (interactionItem.isOf(Items.CHARCOAL)) {
            // Clear all spells
            config.getSpells().clear();
            SpellbookItem.saveAdvancedConfiguration(spellbook, config);
            player.sendMessage(Text.literal("§cCleared all spells"), false);
            world.playSound(null, tablePos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
                    SoundCategory.BLOCKS, 0.8f, 0.3f);
            return ActionResult.SUCCESS;
        } else if (!config.getSpells().isEmpty()) {
            SpellEntry lastSpell = config.getSpells().get(config.getSpells().size() - 1);
            
            if (interactionItem.isOf(Items.BLAZE_POWDER)) {
                // Cycle position
                SpellPosition[] positions = SpellPosition.values();
                int currentIndex = java.util.Arrays.asList(positions).indexOf(lastSpell.getPosition());
                SpellPosition newPosition = positions[(currentIndex + 1) % positions.length];
                lastSpell.setPosition(newPosition);
                SpellbookItem.saveAdvancedConfiguration(spellbook, config);
                player.sendMessage(Text.literal("§ePosition: " + newPosition.getDisplayName()), false);
                world.playSound(null, tablePos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
                        SoundCategory.BLOCKS, 1.0f, 1.2f);
                return ActionResult.SUCCESS;
            } else if (interactionItem.isOf(Items.GHAST_TEAR)) {
                // Cycle target
                SpellTargeting[] targets = SpellTargeting.values();
                int currentIndex = java.util.Arrays.asList(targets).indexOf(lastSpell.getTargeting());
                SpellTargeting newTarget = targets[(currentIndex + 1) % targets.length];
                lastSpell.setTargeting(newTarget);
                SpellbookItem.saveAdvancedConfiguration(spellbook, config);
                player.sendMessage(Text.literal("§eTarget: " + newTarget.getDisplayName()), false);
                world.playSound(null, tablePos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
                        SoundCategory.BLOCKS, 1.0f, 1.2f);
                return ActionResult.SUCCESS;
            } else if (interactionItem.isOf(Items.GLOWSTONE_DUST)) {
                // Cycle delay
                int[] delays = {0, 100, 250, 500, 750, 1000, 1250, 1500, 1750};
                int currentDelay = lastSpell.getDelay();
                int currentIndex = -1;
                for (int i = 0; i < delays.length; i++) {
                    if (delays[i] == currentDelay) {
                        currentIndex = i;
                        break;
                    }
                }
                if (currentIndex == -1) currentIndex = 0;
                int newDelay = delays[(currentIndex + 1) % delays.length];
                lastSpell.setDelay(newDelay);
                SpellbookItem.saveAdvancedConfiguration(spellbook, config);
                player.sendMessage(Text.literal("§eDelay: " + newDelay + "ms"), false);
                world.playSound(null, tablePos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
                        SoundCategory.BLOCKS, 1.0f, 1.2f);
                return ActionResult.SUCCESS;
            } else if (interactionItem.isOf(Items.GUNPOWDER)) {
                // Cycle multiplicity
                int currentMulti = lastSpell.getMultiplicity();
                int newMulti = (currentMulti % 5) + 1; // 1-5
                lastSpell.setMultiplicity(newMulti);
                SpellbookItem.saveAdvancedConfiguration(spellbook, config);
                player.sendMessage(Text.literal("§eMultiplicity: x" + newMulti), false);
                world.playSound(null, tablePos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 
                        SoundCategory.BLOCKS, 1.0f, 1.2f);
                return ActionResult.SUCCESS;
            }
        }
        
        return ActionResult.PASS;
    }

    /**
     * Handle book + material interactions for adding spells
     */
    public static ActionResult handleSpellAddition(net.minecraft.entity.player.PlayerEntity player, World world, ItemStack spellbook, ItemStack book, ItemStack material, BlockPos tablePos) {
        if (world.isClient) return ActionResult.PASS;
        
        AdvancedSpellConfiguration config = SpellbookItem.getAdvancedConfiguration(spellbook);
        String spellName = null;
        
        // Determine spell name from material
        if (material.isOf(Items.LAPIS_LAZULI)) {
            spellName = "Fire Blast";
        } else if (material.isOf(Items.REDSTONE)) {
            spellName = "Light Burst";
        } else if (material.isOf(Items.DIAMOND)) {
            spellName = "Healing Wave";
        } else if (material.isOf(Items.EMERALD)) {
            spellName = "Wind Gust";
        } else if (material.isOf(Items.IRON_INGOT)) {
            spellName = "Water Shield";
        } else if (material.isOf(Items.GOLD_INGOT)) {
            spellName = "Earth Spike";
        } else if (material.isOf(Items.NETHER_STAR)) {
            spellName = "Wither Touch";
        } else if (material.isOf(Items.ENDER_PEARL)) {
            spellName = "Shadow Bind";
        }
        
        if (spellName != null) {
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
            
            // Consume materials
            book.decrement(1);
            material.decrement(1);
            
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }
}
