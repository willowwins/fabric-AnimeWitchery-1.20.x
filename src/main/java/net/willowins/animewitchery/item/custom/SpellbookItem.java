package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.willowins.animewitchery.util.SpellConfiguration;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Advanced spellbook that can store and cast multiple named spell configurations
 */
public class SpellbookItem extends Item {
    
    private static final String CONFIGURATIONS_KEY = "configurations";
    private static final String MULTICAST_CONFIGS_KEY = "multicast_configs";
    private static final String ADVANCED_CONFIG_KEY = "advanced_config";
    private static final String ACTIVE_MULTICAST_INDEX_KEY = "active_multicast";
    private static final String ACTIVE_CONFIG_INDEX_KEY = "active_config";
    private static final String ACTIVE_SPELL_INDEX_KEY = "active_spell_index";
    private static final String INVENTORY_KEY = "SpellbookInventory";
    private static final int MAX_CONFIGURATIONS = 5;
    private static final int MAX_MULTICASTS = 10;
    private static final int INVENTORY_SIZE = 27; // 3 rows of 9 slots
    
    public SpellbookItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        if (!world.isClient) {
            // Shift + Right-click: Open Advanced GUI
            if (user.isSneaking()) {
                user.openHandledScreen(createAdvancedScreenHandlerFactory(stack));
                return new TypedActionResult<>(ActionResult.SUCCESS, stack);
            }
            // Right-click: Cast currently selected multicast (all spells in it)
            else {
                List<AdvancedSpellConfiguration> multicasts = getAllMulticasts(stack);
                if (!multicasts.isEmpty()) {
                    int activeIndex = getActiveMulticastIndex(stack);
                    if (activeIndex >= multicasts.size()) {
                        activeIndex = 0;
                        setActiveMulticastIndex(stack, activeIndex);
                    }
                    
                    AdvancedSpellConfiguration activeMulticast = multicasts.get(activeIndex);
                    if (!activeMulticast.getSpells().isEmpty()) {
                        // Cast all spells in the multicast
                        castAdvancedSpellPattern(user, world, activeMulticast);
                        user.getItemCooldownManager().set(this, 60); // 3 second cooldown
                        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
                    } else {
                        user.sendMessage(Text.literal("§7Multicast '" + activeMulticast.getName() + "' is empty!"), true);
                    }
                } else {
                    user.sendMessage(Text.translatable("message.animewitchery.spellbook_empty"), true);
                    user.sendMessage(Text.literal("§7Sneak + right-click to open GUI or use enchanting table!"), true);
                }
            }
        }
        
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }
    
    /**
     * Handle scroll input for cycling through configurations (Shift + Scroll)
     */
    @Override
    public boolean onStackClicked(ItemStack stack, net.minecraft.screen.slot.Slot slot, net.minecraft.util.ClickType clickType, PlayerEntity player) {
        return super.onStackClicked(stack, slot, clickType, player);
    }
    
    /**
     * Cycle to the next multicast (Shift + Scroll Up)
     */
    public static void cycleConfigurationNext(ItemStack stack, PlayerEntity player) {
        List<AdvancedSpellConfiguration> multicasts = getAllMulticasts(stack);
        if (!multicasts.isEmpty()) {
            int currentIndex = getActiveMulticastIndex(stack);
            int newIndex = (currentIndex + 1) % multicasts.size();
            setActiveMulticastIndex(stack, newIndex);
            
            AdvancedSpellConfiguration multicast = multicasts.get(newIndex);
            player.sendMessage(Text.literal("Active Multicast: " + multicast.getName() + " (" + (newIndex + 1) + "/" + multicasts.size() + ") - " + multicast.getSpells().size() + " spells").formatted(Formatting.AQUA), true);
        } else {
            player.sendMessage(Text.literal("§7No multicasts configured!").formatted(Formatting.GRAY), true);
        }
    }
    
    /**
     * Cycle to the previous multicast (Shift + Scroll Down)
     */
    public static void cycleConfigurationPrevious(ItemStack stack, PlayerEntity player) {
        List<AdvancedSpellConfiguration> multicasts = getAllMulticasts(stack);
        if (!multicasts.isEmpty()) {
            int currentIndex = getActiveMulticastIndex(stack);
            int newIndex = (currentIndex - 1 + multicasts.size()) % multicasts.size();
            setActiveMulticastIndex(stack, newIndex);
            
            AdvancedSpellConfiguration multicast = multicasts.get(newIndex);
            player.sendMessage(Text.literal("Active Multicast: " + multicast.getName() + " (" + (newIndex + 1) + "/" + multicasts.size() + ") - " + multicast.getSpells().size() + " spells").formatted(Formatting.AQUA), true);
        } else {
            player.sendMessage(Text.literal("§7No multicasts configured!").formatted(Formatting.GRAY), true);
        }
    }
    
    /**
     * Called when player attacks with the spellbook (left-click)
     * Cycles through saved configurations
     */
    @Override
    public boolean postHit(ItemStack stack, net.minecraft.entity.LivingEntity target, net.minecraft.entity.LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player && !player.getWorld().isClient) {
            cycleActiveConfiguration(player, stack);
        }
        return false; // Don't damage entities
    }
    
    /**
     * Called when player mines a block with the spellbook (also left-click in air)
     */
    @Override
    public boolean postMine(ItemStack stack, World world, net.minecraft.block.BlockState state, net.minecraft.util.math.BlockPos pos, net.minecraft.entity.LivingEntity miner) {
        if (miner instanceof PlayerEntity player && !world.isClient) {
            cycleActiveConfiguration(player, stack);
        }
        return false; // Don't break blocks
    }
    
    private NamedScreenHandlerFactory createAdvancedScreenHandlerFactory(ItemStack spellbookStack) {
        return new SimpleNamedScreenHandlerFactory(
            (syncId, playerInventory, player) -> {
                SimpleInventory inventory = new SimpleInventory(9);
                return new net.willowins.animewitchery.screen.AdvancedSpellbookScreenHandler(syncId, playerInventory, inventory, spellbookStack);
            },
            Text.translatable("container.animewitchery.advanced_spellbook")
        );
    }
    
    /**
     * Imports a spellbook page into this book
     */
    private TypedActionResult<ItemStack> importPage(PlayerEntity player, ItemStack book, ItemStack page) {
        List<SpellConfiguration> configs = getAllConfigurations(book);
        
        if (configs.size() >= MAX_CONFIGURATIONS) {
            player.sendMessage(Text.translatable("message.animewitchery.spellbook_configurations_full"), true);
            return new TypedActionResult<>(ActionResult.FAIL, book);
        }
        
        SpellEntry entry = SpellbookPageItem.getSpellEntry(page);
        if (entry != null) {
            // Convert SpellEntry to SpellConfiguration for legacy compatibility
            SpellConfiguration newConfig = new SpellConfiguration(
                entry.getSpellName(), 
                List.of(entry.getSpellName()), 
                "beam", 
                entry.getDelay() / 50 // Convert ms to ticks
            );
            configs.add(newConfig);
            saveAllConfigurations(book, configs);
            
            page.decrement(1);
            player.sendMessage(Text.translatable("message.animewitchery.page_imported", entry.getSpellName()).formatted(Formatting.GREEN), true);
        }
        
        return new TypedActionResult<>(ActionResult.SUCCESS, book);
    }
    
    /**
     * Cycles through saved configurations
     */
    private void cycleActiveConfiguration(PlayerEntity player, ItemStack book) {
        AdvancedSpellConfiguration advancedConfig = getAdvancedConfiguration(book);
        if (advancedConfig != null && !advancedConfig.getSpells().isEmpty()) {
            player.sendMessage(Text.literal("Advanced Config: " + advancedConfig.getName()).formatted(Formatting.GOLD), true);
        } else {
            player.sendMessage(Text.translatable("message.animewitchery.spellbook_empty"), true);
        }
    }
    
    /**
     * Casts all spells in the configured pattern
     */
    private void castSpellPattern(PlayerEntity player, World world, SpellConfiguration config) {
        // Check if this is an advanced configuration
        AdvancedSpellConfiguration advancedConfig = getAdvancedConfiguration(player.getMainHandStack());
        if (advancedConfig != null && !advancedConfig.getSpells().isEmpty()) {
            castAdvancedSpellPattern(player, world, advancedConfig);
            return;
        }
        
        // Fallback to legacy system
        List<String> spells = config.getSpells();
        String pattern = config.getPattern();
        
        // Calculate total mana cost
        int totalManaCost = calculateTotalManaCost(spells, pattern);
        
        // Check if player has enough mana
        if (!net.willowins.animewitchery.mana.ManaUtils.consumeWithStorage(player, totalManaCost)) {
            player.sendMessage(Text.translatable("message.animewitchery.not_enough_mana", totalManaCost), true);
            return;
        }
        
        // Cast spells based on pattern
        switch (pattern) {
            case "beam" -> castBeamPattern(player, world, spells);
            case "circle" -> castCirclePattern(player, world, spells);
            case "cone" -> castConePattern(player, world, spells);
            case "burst" -> castBurstPattern(player, world, spells);
            default -> castBeamPattern(player, world, spells);
        }
    }
    
    /**
     * Cast advanced spell configuration
     */
    private void castAdvancedSpellPattern(PlayerEntity player, World world, AdvancedSpellConfiguration config) {
        // Calculate total mana cost
        int totalManaCost = config.calculateTotalManaCost();
        
        // Check if player has enough mana
        if (!net.willowins.animewitchery.mana.ManaUtils.consumeWithStorage(player, totalManaCost)) {
            player.sendMessage(Text.translatable("message.animewitchery.not_enough_mana", totalManaCost), true);
            return;
        }
        
        // Cast using advanced system
        net.willowins.animewitchery.util.AdvancedSpellCaster.castAdvancedConfiguration(player, world, config);
        
        // Send feedback message
        player.sendMessage(Text.literal("§d✦ Advanced spell pattern cast: " + config.getName()).formatted(), true);
    }
    
    /**
     * Casts a single spell from the advanced configuration
     */
    private void castSingleSpell(PlayerEntity player, World world, AdvancedSpellConfiguration config, int spellIndex) {
        if (spellIndex < 0 || spellIndex >= config.getSpells().size()) {
            return;
        }
        
        AdvancedSpellConfiguration.SpellEntry spell = config.getSpells().get(spellIndex);
        
        // Calculate mana cost for this single spell
        int manaCost = spell.getMultiplicity() * 100; // Base cost per spell * multiplicity
        
        // Check if player has enough mana
        if (!net.willowins.animewitchery.mana.ManaUtils.consumeWithStorage(player, manaCost)) {
            player.sendMessage(Text.translatable("message.animewitchery.not_enough_mana", manaCost), true);
            return;
        }
        
        // Cast the spell using the simplified spell caster
        net.willowins.animewitchery.util.SpellCaster.castSpellWithoutManaCost(
            player, 
            world, 
            spell.getSpellName(), 
            getTargetingModeString(spell.getTargeting())
        );
        
        // Send feedback message
        player.sendMessage(Text.literal("§d✦ Cast: " + spell.getSpellName()).formatted(), true);
    }
    
    /**
     * Converts spell targeting enum to targeting mode string
     */
    private String getTargetingModeString(AdvancedSpellConfiguration.SpellTargeting targeting) {
        return switch (targeting) {
            case CASTER -> "self";
            case TARGET_ENTITY, TARGET_BLOCK -> "direct";
            case AREA -> "area";
            case AUTO -> "direct";
            case PROJECTILE -> "direct";
        };
    }
    
    /**
     * Calculates total mana cost for the spell pattern
     */
    private int calculateTotalManaCost(List<String> spells, String pattern) {
        int baseCost = 0;
        
        // Add up individual spell costs (reduced since they're in a book)
        for (String spell : spells) {
            baseCost += getSpellBaseCost(spell) / 2; // 50% discount per spell
        }
        
        // Add pattern overhead
        int patternCost = switch (pattern) {
            case "beam" -> 100;
            case "circle" -> 300;
            case "cone" -> 200;
            case "burst" -> 500;
            default -> 100;
        };
        
        // Apply spell count scaling - more spells cost exponentially more
        int spellCount = spells.size();
        if (spellCount > 1) {
            // Exponential scaling: each additional spell increases cost by 40%
            double scalingFactor = Math.pow(1.4, spellCount - 1);
            baseCost = (int) (baseCost * scalingFactor);
        }
        
        return baseCost + patternCost;
    }
    
    /**
     * Gets base mana cost for a single spell
     */
    private int getSpellBaseCost(String spellName) {
        return switch (spellName) {
            case "Fire Blast", "Earth Spike", "Wither Touch", "Light Burst", "Shadow Bind" -> 500;
            case "Water Shield", "Wind Gust" -> 300;
            case "Healing Wave" -> 800;
            default -> 0;
        };
    }
    
    /**
     * Beam Pattern - Direct forward casting (self-cast mode)
     */
    private void castBeamPattern(PlayerEntity player, World world, List<String> spells) {
        for (String spell : spells) {
            net.willowins.animewitchery.util.SpellCaster.castSpellWithoutManaCost(player, world, spell, "self");
        }
    }
    
    /**
     * Circle Pattern - Casts surrounding the target entity
     */
    private void castCirclePattern(PlayerEntity player, World world, List<String> spells) {
        int spellCount = spells.size();
        for (int i = 0; i < spellCount; i++) {
            float originalYaw = player.getYaw();
            float angle = (360.0f / spellCount) * i;
            player.setYaw(originalYaw + angle);
            
            net.willowins.animewitchery.util.SpellCaster.castSpellWithoutManaCost(player, world, spells.get(i), "surrounding_target");
            
            player.setYaw(originalYaw);
        }
    }
    
    /**
     * Cone Pattern - Spell wall from both sides towards target
     */
    private void castConePattern(PlayerEntity player, World world, List<String> spells) {
        for (String spell : spells) {
            net.willowins.animewitchery.util.SpellCaster.castSpellWithoutManaCost(player, world, spell, "spell_wall");
        }
    }
    
    /**
     * Burst Pattern - Area effect around player
     */
    private void castBurstPattern(PlayerEntity player, World world, List<String> spells) {
        for (String spell : spells) {
            net.willowins.animewitchery.util.SpellCaster.castSpellWithoutManaCost(player, world, spell, "area");
        }
    }
    
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // Show stored spell count
        List<String> availableSpells = getAvailableSpells(stack);
        tooltip.add(Text.literal("Stored Spells: " + availableSpells.size()).formatted(Formatting.AQUA));
        
        // Show multicast info
        List<AdvancedSpellConfiguration> multicasts = getAllMulticasts(stack);
        if (!multicasts.isEmpty()) {
            int activeIndex = getActiveMulticastIndex(stack);
            if (activeIndex < multicasts.size()) {
                AdvancedSpellConfiguration activeMulticast = multicasts.get(activeIndex);
                tooltip.add(Text.literal("Active: " + activeMulticast.getName()).formatted(Formatting.LIGHT_PURPLE));
                tooltip.add(Text.literal("  " + activeMulticast.getSpells().size() + " spells configured").formatted(Formatting.GRAY));
            }
            tooltip.add(Text.literal("Multicasts: " + multicasts.size()).formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.literal("No multicasts configured").formatted(Formatting.GRAY));
        }
        
        tooltip.add(Text.literal("Shift+Right-click: Open GUI").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        tooltip.add(Text.literal("Shift+Scroll: Change multicast").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        tooltip.add(Text.literal("Right-click: Cast active multicast").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        
        super.appendTooltip(stack, world, tooltip, context);
    }
    
    // === Static helper methods for NBT manipulation ===
    
    /**
     * Gets all configurations from a spellbook
     */
    public static List<SpellConfiguration> getAllConfigurations(ItemStack book) {
        List<SpellConfiguration> configs = new ArrayList<>();
        NbtCompound nbt = book.getNbt();
        
        if (nbt != null && nbt.contains(CONFIGURATIONS_KEY)) {
            NbtList configList = nbt.getList(CONFIGURATIONS_KEY, 10);
            for (int i = 0; i < configList.size(); i++) {
                configs.add(SpellConfiguration.fromNbt(configList.getCompound(i)));
            }
        }
        
        return configs;
    }
    
    /**
     * Saves all configurations to a spellbook
     */
    public static void saveAllConfigurations(ItemStack book, List<SpellConfiguration> configs) {
        NbtCompound nbt = book.getOrCreateNbt();
        NbtList configList = new NbtList();
        
        for (SpellConfiguration config : configs) {
            configList.add(config.toNbt());
        }
        
        nbt.put(CONFIGURATIONS_KEY, configList);
    }
    
    /**
     * Gets the active configuration
     */
    public static SpellConfiguration getActiveConfiguration(ItemStack book) {
        List<SpellConfiguration> configs = getAllConfigurations(book);
        if (configs.isEmpty()) return null;
        
        int index = getActiveConfigurationIndex(book);
        return configs.get(Math.min(index, configs.size() - 1));
    }
    
    /**
     * Gets the active configuration index
     */
    public static int getActiveConfigurationIndex(ItemStack book) {
        NbtCompound nbt = book.getNbt();
        return nbt != null && nbt.contains(ACTIVE_CONFIG_INDEX_KEY) ? nbt.getInt(ACTIVE_CONFIG_INDEX_KEY) : 0;
    }
    
    /**
     * Sets the active configuration index
     */
    public static void setActiveConfigurationIndex(ItemStack book, int index) {
        book.getOrCreateNbt().putInt(ACTIVE_CONFIG_INDEX_KEY, index);
    }
    
    /**
     * Gets the active spell index
     */
    public static int getActiveSpellIndex(ItemStack book) {
        NbtCompound nbt = book.getNbt();
        return nbt != null && nbt.contains(ACTIVE_SPELL_INDEX_KEY) ? nbt.getInt(ACTIVE_SPELL_INDEX_KEY) : 0;
    }
    
    /**
     * Sets the active spell index
     */
    public static void setActiveSpellIndex(ItemStack book, int index) {
        book.getOrCreateNbt().putInt(ACTIVE_SPELL_INDEX_KEY, index);
    }
    
    /**
     * Gets the active multicast index
     */
    public static int getActiveMulticastIndex(ItemStack book) {
        NbtCompound nbt = book.getNbt();
        return nbt != null && nbt.contains(ACTIVE_MULTICAST_INDEX_KEY) ? nbt.getInt(ACTIVE_MULTICAST_INDEX_KEY) : 0;
    }
    
    /**
     * Sets the active multicast index
     */
    public static void setActiveMulticastIndex(ItemStack book, int index) {
        book.getOrCreateNbt().putInt(ACTIVE_MULTICAST_INDEX_KEY, index);
    }
    
    /**
     * Gets all multicast configurations from a spellbook
     */
    public static List<AdvancedSpellConfiguration> getAllMulticasts(ItemStack book) {
        List<AdvancedSpellConfiguration> multicasts = new ArrayList<>();
        NbtCompound nbt = book.getNbt();
        
        if (nbt != null && nbt.contains(MULTICAST_CONFIGS_KEY)) {
            NbtList multicastList = nbt.getList(MULTICAST_CONFIGS_KEY, 10); // 10 = Compound type
            for (int i = 0; i < multicastList.size(); i++) {
                NbtCompound multicastNbt = multicastList.getCompound(i);
                multicasts.add(AdvancedSpellConfiguration.fromNbt(multicastNbt));
            }
        }
        
        // Fallback: If no multicasts, create one from legacy advanced_config
        if (multicasts.isEmpty()) {
            AdvancedSpellConfiguration legacyConfig = getAdvancedConfiguration(book);
            if (legacyConfig != null && !legacyConfig.getSpells().isEmpty()) {
                multicasts.add(legacyConfig);
            }
        }
        
        return multicasts;
    }
    
    /**
     * Saves all multicast configurations to a spellbook
     */
    public static void saveAllMulticasts(ItemStack book, List<AdvancedSpellConfiguration> multicasts) {
        NbtList multicastList = new NbtList();
        for (AdvancedSpellConfiguration multicast : multicasts) {
            multicastList.add(multicast.toNbt());
        }
        book.getOrCreateNbt().put(MULTICAST_CONFIGS_KEY, multicastList);
    }
    
    /**
     * Adds a new multicast configuration to the spellbook
     */
    public static void addMulticast(ItemStack book, AdvancedSpellConfiguration multicast) {
        List<AdvancedSpellConfiguration> multicasts = getAllMulticasts(book);
        if (multicasts.size() < MAX_MULTICASTS) {
            multicasts.add(multicast);
            saveAllMulticasts(book, multicasts);
        }
    }
    
    /**
     * Gets the advanced configuration from a spellbook
     */
    public static AdvancedSpellConfiguration getAdvancedConfiguration(ItemStack book) {
        NbtCompound nbt = book.getNbt();
        if (nbt != null && nbt.contains(ADVANCED_CONFIG_KEY)) {
            return AdvancedSpellConfiguration.fromNbt(nbt.getCompound(ADVANCED_CONFIG_KEY));
        }
        return null;
    }
    
    /**
     * Saves an advanced configuration to a spellbook
     */
    public static void saveAdvancedConfiguration(ItemStack book, AdvancedSpellConfiguration config) {
        NbtCompound nbt = book.getOrCreateNbt();
        nbt.put(ADVANCED_CONFIG_KEY, config.toNbt());
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        List<AdvancedSpellConfiguration> multicasts = getAllMulticasts(stack);
        return !multicasts.isEmpty() && multicasts.stream().anyMatch(mc -> !mc.getSpells().isEmpty());
    }
    
    /**
     * Gets the internal inventory from a spellbook
     */
    public static SimpleInventory getInventory(ItemStack book) {
        SimpleInventory inventory = new SimpleInventory(INVENTORY_SIZE);
        NbtCompound nbt = book.getNbt();
        
        if (nbt != null && nbt.contains(INVENTORY_KEY)) {
            NbtList inventoryNbt = nbt.getList(INVENTORY_KEY, 10); // 10 = Compound type
            for (int i = 0; i < inventoryNbt.size() && i < INVENTORY_SIZE; i++) {
                NbtCompound slotNbt = inventoryNbt.getCompound(i);
                int slot = slotNbt.getInt("Slot");
                if (slot >= 0 && slot < INVENTORY_SIZE) {
                    inventory.setStack(slot, ItemStack.fromNbt(slotNbt));
                }
            }
        }
        
        return inventory;
    }
    
    /**
     * Saves the internal inventory to a spellbook
     */
    public static void saveInventory(ItemStack book, SimpleInventory inventory) {
        NbtList inventoryNbt = new NbtList();
        
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound slotNbt = new NbtCompound();
                slotNbt.putInt("Slot", i);
                stack.writeNbt(slotNbt);
                inventoryNbt.add(slotNbt);
            }
        }
        
        book.getOrCreateNbt().put(INVENTORY_KEY, inventoryNbt);
    }
    
    /**
     * Gets list of available spell names from the internal inventory
     */
    public static List<String> getAvailableSpells(ItemStack book) {
        List<String> availableSpells = new ArrayList<>();
        SimpleInventory inventory = getInventory(book);
        
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof SpellScrollItem scrollItem) {
                String spellName = scrollItem.getSpellName();
                if (!availableSpells.contains(spellName)) {
                    availableSpells.add(spellName);
                }
            }
        }
        
        return availableSpells;
    }
}
