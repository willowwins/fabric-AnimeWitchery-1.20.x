package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.willowins.animewitchery.item.custom.SpellbookItem;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration;
import net.willowins.animewitchery.ModScreenHandlers;

public class AdvancedSpellbookScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final ItemStack spellbookStack;
    private final Inventory spellbookInventory;
    
    public AdvancedSpellbookScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, ItemStack spellbookStack) {
        super(ModScreenHandlers.ADVANCED_SPELLBOOK_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.spellbookStack = spellbookStack;
        this.spellbookInventory = SpellbookItem.getInventory(spellbookStack);
        
                // Spellbook internal inventory slots (5 slots visible at top for pages)
                // These are positioned based on the texture layout
                for (int i = 0; i < 5; ++i) {
                    this.addSlot(new Slot(spellbookInventory, i, 8 + i * 18, 8)); // Top row, 5 slots
                }
                
                // Spell scroll storage slots (visible slots 5-9, positioned below page slots)
                for (int i = 5; i < 10; ++i) {
                    this.addSlot(new Slot(spellbookInventory, i, 8 + (i-5) * 18, 26)); // Second row, 5 slots
                }
                
                // Additional hidden storage slots for overflow
                for (int i = 10; i < 27; ++i) {
                    this.addSlot(new Slot(spellbookInventory, i, -1000, -1000)); // Hidden off-screen
                }
        
        // Player inventory slots (adjusted for 256-pixel GUI height)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 98 + i * 18)); // Adjusted for 256px GUI
            }
        }
        
        // Player hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 156)); // Adjusted for 256px GUI
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        // Save the spellbook inventory when the GUI is closed
        SpellbookItem.saveInventory(spellbookStack, (SimpleInventory) spellbookInventory);
    }
    
    public Inventory getSpellbookInventory() {
        return spellbookInventory;
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 36) {
                if (!this.insertItem(itemStack2, 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return itemStack;
    }
    
    public ItemStack getSpellbookStack() {
        return spellbookStack;
    }
    
    public AdvancedSpellConfiguration getAdvancedConfiguration() {
        return SpellbookItem.getAdvancedConfiguration(spellbookStack);
    }
    
    public void saveAdvancedConfiguration(AdvancedSpellConfiguration config) {
        SpellbookItem.saveAdvancedConfiguration(spellbookStack, config);
    }
    
    public java.util.List<AdvancedSpellConfiguration> getAllMulticasts() {
        return SpellbookItem.getAllMulticasts(spellbookStack);
    }
    
    public void saveAllMulticasts(java.util.List<AdvancedSpellConfiguration> multicasts) {
        SpellbookItem.saveAllMulticasts(spellbookStack, multicasts);
    }
    
    public int getActiveMulticastIndex() {
        return SpellbookItem.getActiveMulticastIndex(spellbookStack);
    }
    
    public void setActiveMulticastIndex(int index) {
        SpellbookItem.setActiveMulticastIndex(spellbookStack, index);
    }
}
