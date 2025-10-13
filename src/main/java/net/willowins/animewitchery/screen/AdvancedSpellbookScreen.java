package net.willowins.animewitchery.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellEntry;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellPosition;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellTargeting;

import java.util.List;

public class AdvancedSpellbookScreen extends HandledScreen<AdvancedSpellbookScreenHandler> {
    
    private static final Identifier TEXTURE = new Identifier("animewitchery", "textures/gui/spellbook_gui.png");
    private static final Identifier CONTAINER_TEXTURE = new Identifier("textures/gui/container/generic_54.png");
    private static final int BACKGROUND_WIDTH = 256; // Width as specified (256 pixels)
    private static final int BACKGROUND_HEIGHT = 256; // Height as specified (256 pixels)
    
    // Try to detect texture dimensions dynamically
    private int textureWidth = BACKGROUND_WIDTH;
    private int textureHeight = BACKGROUND_HEIGHT;
    
    private int scrollOffset = 0;
    private static final int MAX_VISIBLE_SPELLS = 6; // 6 EDIT buttons visible in texture
    
    private boolean inEditMode = false;
    private int editingSpellIndex = -1;
    private int currentMulticastIndex = 0; // Which multicast we're currently viewing/editing
    
    public AdvancedSpellbookScreen(AdvancedSpellbookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = BACKGROUND_WIDTH;
        this.backgroundHeight = BACKGROUND_HEIGHT;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.titleY = -1000; // Hide default title
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
        
        // Load current multicast index from spellbook
        currentMulticastIndex = handler.getActiveMulticastIndex();

        // No config name field - we'll just display the multicast name
        updateWidgets();
    }

    private void updateWidgets() {
        this.clearChildren(); // Clear all existing widgets

        if (!inEditMode) {
            createMainViewWidgets();
        } else {
            createEditViewWidgets();
        }
    }

    private void createMainViewWidgets() {
        // PREV button (bottom left) - better positioned
        this.addDrawableChild(ButtonWidget.builder(Text.literal("PREV"), button -> onPreviousMulticast())
                .position(this.x + 8, this.y + 48)
                .size(50, 20)
                .build());
        
        // NEXT button (bottom right) - better positioned
        this.addDrawableChild(ButtonWidget.builder(Text.literal("NEXT"), button -> onNextMulticast())
                .position(this.x + 198, this.y + 48)
                .size(50, 20)
                .build());

        // ADD SPELL button (top right) - better positioned
        this.addDrawableChild(ButtonWidget.builder(Text.literal("ADD"), button -> onAddSpell())
                .position(this.x + 198, this.y + 8)
                .size(50, 18)
                .build());

        // Right side EDIT buttons - positioned to align with page slots
        for (int i = 0; i < 5; i++) { // 5 visible page slots
            final int slotIndex = i; // Make final for lambda
            int buttonY = this.y + 8 + (i * 18); // Align with page slots
            this.addDrawableChild(ButtonWidget.builder(Text.literal("EDIT"), button -> onEditSpell(slotIndex))
                    .position(this.x + 98, buttonY)
                    .size(40, 16)
                    .build());
        }
    }

    private void createEditViewWidgets() {
        if (editingSpellIndex == -1) {
            updateWidgets();
            return;
        }

        // Get the page from the inventory slot
        net.minecraft.inventory.SimpleInventory inventory = (net.minecraft.inventory.SimpleInventory) handler.getSpellbookInventory();
        ItemStack pageStack = inventory.getStack(editingSpellIndex);
        
        if (pageStack.isEmpty() || !(pageStack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellbookPageItem)) {
            updateWidgets();
            return;
        }
        
        SpellEntry spell = net.willowins.animewitchery.item.custom.SpellbookPageItem.getSpellEntry(pageStack);
        if (spell == null) {
            updateWidgets();
            return;
        }

        // Cycle buttons for spell properties (left side) - better spacing
        int leftX = this.x + 8;
        int startY = this.y + 72;
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Name"), button -> onCycleSpellName(editingSpellIndex))
                .position(leftX, startY)
                .size(45, 16)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Pos"), button -> onCyclePosition(editingSpellIndex))
                .position(leftX, startY + 20)
                .size(45, 16)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Target"), button -> onCycleTarget(editingSpellIndex))
                .position(leftX, startY + 40)
                .size(45, 16)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Delay"), button -> onCycleDelay(editingSpellIndex))
                .position(leftX, startY + 60)
                .size(45, 16)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Multi"), button -> onCycleMultiplicity(editingSpellIndex))
                .position(leftX, startY + 80)
                .size(45, 16)
                .build());

        // Back and Delete buttons (right side) - better positioned
        this.addDrawableChild(ButtonWidget.builder(Text.literal("BACK"), button -> {
                    inEditMode = false;
                    editingSpellIndex = -1;
                    updateWidgets();
                })
                .position(this.x + 198, this.y + 72)
                .size(50, 16)
                .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("DEL"), button -> onDeleteSpell(editingSpellIndex))
                .position(this.x + 198, this.y + 92)
                .size(50, 16)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        
        // Draw "SPELL NAME" label in the center top area
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty() && currentMulticastIndex < multicasts.size()) {
            AdvancedSpellConfiguration currentMulticast = multicasts.get(currentMulticastIndex);
            String displayName = currentMulticast.getName();
            if (displayName.length() > 15) {
                displayName = displayName.substring(0, 15) + "...";
            }
            context.drawText(this.textRenderer, Text.literal(displayName).formatted(Formatting.WHITE), this.x + 100, this.y + 8, 0xFFFFFF, false);
        } else {
            // Debug text when no multicasts
            context.drawText(this.textRenderer, Text.literal("No Multicasts").formatted(Formatting.GRAY), this.x + 100, this.y + 8, 0xAAAAAA, false);
        }
        
        // Debug info - show GUI state (better positioned)
        context.drawText(this.textRenderer, Text.literal("GUI: " + (inEditMode ? "EDIT" : "MAIN")).formatted(Formatting.YELLOW), this.x + 8, this.y + 170, 0xFFFF00, false);
        
        // Show spell count and available spells
        net.minecraft.inventory.SimpleInventory inventory = (net.minecraft.inventory.SimpleInventory) handler.getSpellbookInventory();
        int pageCount = 0;
        for (int i = 0; i < 5; i++) {
            if (!inventory.getStack(i).isEmpty()) pageCount++;
        }
        context.drawText(this.textRenderer, Text.literal("Pages: " + pageCount).formatted(Formatting.AQUA), this.x + 8, this.y + 180, 0x00FFFF, false);
        
        // Debug info for button visibility
        context.drawText(this.textRenderer, Text.literal("Buttons: " + this.children().size()).formatted(Formatting.GREEN), this.x + 8, this.y + 190, 0x00FF00, false);
        
                // Draw spell list text (left side, aligned with EDIT buttons)
                if (!inEditMode) {
                    // Read from spellbook pages in slots 0-4
                    // inventory already declared above
                    
                    for (int i = 0; i < 5; i++) {
                        ItemStack pageStack = inventory.getStack(i);
                        if (!pageStack.isEmpty() && pageStack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellbookPageItem) {
                            SpellEntry entry = net.willowins.animewitchery.item.custom.SpellbookPageItem.getSpellEntry(pageStack);
                            if (entry != null) {
                                String spellName = entry.getSpellName();
                                if (spellName.length() > 12) {
                                    spellName = spellName.substring(0, 12);
                                }
                                int textY = this.y + 12 + (i * 18); // Aligned with page slots
                                context.drawText(this.textRenderer, Text.literal(spellName).formatted(Formatting.GOLD), this.x + 8, textY, 0xFFD700, false);
                            } else {
                                // Debug: show if page exists but no spell entry
                                int textY = this.y + 12 + (i * 18);
                                context.drawText(this.textRenderer, Text.literal("Empty Page").formatted(Formatting.GRAY), this.x + 8, textY, 0xAAAAAA, false);
                            }
                        } else {
                            // Debug: show slot status
                            int textY = this.y + 12 + (i * 18);
                            context.drawText(this.textRenderer, Text.literal("Slot " + i).formatted(Formatting.DARK_GRAY), this.x + 8, textY, 0x555555, false);
                        }
                    }
                } else if (editingSpellIndex != -1) {
                    // Draw spell editor text (center area) - read from page in slot
                    // inventory already declared above
                    ItemStack pageStack = inventory.getStack(editingSpellIndex);
                    
                    if (!pageStack.isEmpty() && pageStack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellbookPageItem) {
                        SpellEntry spell = net.willowins.animewitchery.item.custom.SpellbookPageItem.getSpellEntry(pageStack);
                        if (spell != null) {
                            int centerX = this.x + 55;
                            int startY = this.y + 20;
                            
                            context.drawText(this.textRenderer, Text.literal("Editing #" + (editingSpellIndex + 1)).formatted(Formatting.AQUA), centerX, startY, 0x55FFFF, false);
                            context.drawText(this.textRenderer, Text.literal(spell.getSpellName()).formatted(Formatting.GOLD), centerX, startY + 18, 0xFFD700, false);
                            context.drawText(this.textRenderer, Text.literal("Pos: " + spell.getPosition().getDisplayName()).formatted(Formatting.GRAY), centerX, startY + 36, 0xAAAAAA, false);
                            context.drawText(this.textRenderer, Text.literal("Tgt: " + spell.getTargeting().getDisplayName()).formatted(Formatting.GRAY), centerX, startY + 54, 0xAAAAAA, false);
                            context.drawText(this.textRenderer, Text.literal("Delay: " + spell.getDelay() + "ms").formatted(Formatting.GRAY), centerX, startY + 72, 0xAAAAAA, false);
                            context.drawText(this.textRenderer, Text.literal("Multi: x" + spell.getMultiplicity()).formatted(Formatting.GRAY), centerX, startY + 90, 0xAAAAAA, false);
                        }
                    }
                }
        
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Try to use the custom spellbook texture, fallback to container texture if it fails
        try {
            // Use the texture with its natural dimensions
            context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        } catch (Exception e) {
            // Fallback to standard container texture if custom texture fails
            context.drawTexture(CONTAINER_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
        AdvancedSpellConfiguration config = handler.getAdvancedConfiguration();
        if (verticalAmount != 0 && config != null && config.getSpells().size() > MAX_VISIBLE_SPELLS) {
            if (verticalAmount > 0 && scrollOffset > 0) {
                scrollOffset--;
            } else if (verticalAmount < 0 && scrollOffset + MAX_VISIBLE_SPELLS < config.getSpells().size()) {
                scrollOffset++;
            }
            updateWidgets(); // Re-render widgets to reflect scroll
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, verticalAmount);
    }

    // Event handlers
    private void onNewMulticast() {
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (multicasts.size() < 10) { // MAX_MULTICASTS
            AdvancedSpellConfiguration newMulticast = new AdvancedSpellConfiguration("Multicast " + (multicasts.size() + 1));
            multicasts.add(newMulticast);
            handler.saveAllMulticasts(multicasts);
            currentMulticastIndex = multicasts.size() - 1;
            handler.setActiveMulticastIndex(currentMulticastIndex);
            updateWidgets();
        }
    }
    
    private void onPreviousMulticast() {
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty()) {
            currentMulticastIndex = (currentMulticastIndex - 1 + multicasts.size()) % multicasts.size();
            handler.setActiveMulticastIndex(currentMulticastIndex);
            updateWidgets();
        }
    }
    
    private void onNextMulticast() {
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty()) {
            currentMulticastIndex = (currentMulticastIndex + 1) % multicasts.size();
            handler.setActiveMulticastIndex(currentMulticastIndex);
            updateWidgets();
        }
    }
    
    private void onAddSpell() {
        // Get available spells from spell scrolls in slots 5-9 (visible scroll slots)
        net.minecraft.inventory.SimpleInventory inventory = (net.minecraft.inventory.SimpleInventory) handler.getSpellbookInventory();
        
        // Find first spell scroll in visible scroll slots (5-9)
        for (int i = 5; i < 10; i++) {
            ItemStack scrollStack = inventory.getStack(i);
            if (!scrollStack.isEmpty() && scrollStack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellScrollItem) {
                net.willowins.animewitchery.item.custom.SpellScrollItem scrollItem = (net.willowins.animewitchery.item.custom.SpellScrollItem) scrollStack.getItem();
                String spellName = scrollItem.getSpellName();
                
                // Create a new spell entry
                SpellEntry newSpell = new SpellEntry(spellName);
                
                // Create a spell page item with this entry
                ItemStack spellPage = net.willowins.animewitchery.item.custom.SpellbookPageItem.createPage(newSpell);
                
                // Find an empty slot in the page inventory (first 5 slots)
                for (int j = 0; j < 5; j++) {
                    if (inventory.getStack(j).isEmpty()) {
                        inventory.setStack(j, spellPage);
                        scrollStack.decrement(1); // Consume the scroll
                        updateWidgets();
                        return;
                    }
                }
                
                // If no empty page slots, can't add
                return;
            }
        }
        
        // No spell scrolls found in visible slots
    }

    private void onSaveConfig() {
        // Save all multicasts
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty()) {
            handler.saveAllMulticasts(multicasts);
        }
    }

    private void onClearConfig() {
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty() && currentMulticastIndex < multicasts.size()) {
            AdvancedSpellConfiguration currentMulticast = multicasts.get(currentMulticastIndex);
            currentMulticast.getSpells().clear();
            handler.saveAllMulticasts(multicasts);
            updateWidgets();
        }
    }

    private void onEditSpell(int index) {
        inEditMode = true;
        editingSpellIndex = index;
        updateWidgets();
    }

    private void onDeleteSpell(int index) {
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty() && currentMulticastIndex < multicasts.size()) {
            AdvancedSpellConfiguration currentMulticast = multicasts.get(currentMulticastIndex);
            currentMulticast.removeSpell(index);
            handler.saveAllMulticasts(multicasts);
            inEditMode = false;
            editingSpellIndex = -1;
            updateWidgets();
        }
    }

    private void onCycleSpellName(int index) {
        // Get the page from the inventory slot
        net.minecraft.inventory.SimpleInventory inventory = (net.minecraft.inventory.SimpleInventory) handler.getSpellbookInventory();
        ItemStack pageStack = inventory.getStack(index);
        
        if (pageStack.isEmpty() || !(pageStack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellbookPageItem)) return;
        
        SpellEntry spell = net.willowins.animewitchery.item.custom.SpellbookPageItem.getSpellEntry(pageStack);
        if (spell == null) return;
        
        // Get available spells from scroll slots (5-9)
        List<String> availableSpells = new java.util.ArrayList<>();
        for (int i = 5; i < 10; i++) {
            ItemStack scrollStack = inventory.getStack(i);
            if (!scrollStack.isEmpty() && scrollStack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellScrollItem) {
                net.willowins.animewitchery.item.custom.SpellScrollItem scrollItem = (net.willowins.animewitchery.item.custom.SpellScrollItem) scrollStack.getItem();
                String spellName = scrollItem.getSpellName();
                if (!availableSpells.contains(spellName)) {
                    availableSpells.add(spellName);
                }
            }
        }
        
        if (availableSpells.isEmpty()) return;
        
        // Find current spell in list and cycle to next
        int currentIndex = availableSpells.indexOf(spell.getSpellName());
        if (currentIndex == -1) currentIndex = 0;
        String newName = availableSpells.get((currentIndex + 1) % availableSpells.size());
        spell.setSpellName(newName);
        
        // Update the page with the new spell entry
        pageStack = net.willowins.animewitchery.item.custom.SpellbookPageItem.createPage(spell);
        inventory.setStack(index, pageStack);
        
        updateWidgets();
    }

    private void onCyclePosition(int index) {
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty() && currentMulticastIndex < multicasts.size()) {
            AdvancedSpellConfiguration currentMulticast = multicasts.get(currentMulticastIndex);
            if (index < currentMulticast.getSpells().size()) {
                SpellEntry spell = currentMulticast.getSpells().get(index);
                SpellPosition[] positions = SpellPosition.values();
                int currentIndex = java.util.Arrays.asList(positions).indexOf(spell.getPosition());
                SpellPosition newPosition = positions[(currentIndex + 1) % positions.length];
                spell.setPosition(newPosition);
                handler.saveAllMulticasts(multicasts);
                updateWidgets();
            }
        }
    }

    private void onCycleTarget(int index) {
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty() && currentMulticastIndex < multicasts.size()) {
            AdvancedSpellConfiguration currentMulticast = multicasts.get(currentMulticastIndex);
            if (index < currentMulticast.getSpells().size()) {
                SpellEntry spell = currentMulticast.getSpells().get(index);
                SpellTargeting[] targets = SpellTargeting.values();
                int currentIndex = java.util.Arrays.asList(targets).indexOf(spell.getTargeting());
                SpellTargeting newTarget = targets[(currentIndex + 1) % targets.length];
                spell.setTargeting(newTarget);
                handler.saveAllMulticasts(multicasts);
                updateWidgets();
            }
        }
    }

    private void onCycleDelay(int index) {
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty() && currentMulticastIndex < multicasts.size()) {
            AdvancedSpellConfiguration currentMulticast = multicasts.get(currentMulticastIndex);
            if (index < currentMulticast.getSpells().size()) {
                SpellEntry spell = currentMulticast.getSpells().get(index);
                int[] delays = {0, 100, 250, 500, 750, 1000, 1250, 1500, 1750};
                int currentDelay = spell.getDelay();
                int currentIndex = -1;
                for (int i = 0; i < delays.length; i++) {
                    if (delays[i] == currentDelay) {
                        currentIndex = i;
                        break;
                    }
                }
                if (currentIndex == -1) currentIndex = 0;
                int newDelay = delays[(currentIndex + 1) % delays.length];
                spell.setDelay(newDelay);
                handler.saveAllMulticasts(multicasts);
                updateWidgets();
            }
        }
    }

    private void onCycleMultiplicity(int index) {
        List<AdvancedSpellConfiguration> multicasts = handler.getAllMulticasts();
        if (!multicasts.isEmpty() && currentMulticastIndex < multicasts.size()) {
            AdvancedSpellConfiguration currentMulticast = multicasts.get(currentMulticastIndex);
            if (index < currentMulticast.getSpells().size()) {
                SpellEntry spell = currentMulticast.getSpells().get(index);
                int currentMulti = spell.getMultiplicity();
                int newMulti = (currentMulti % 5) + 1; // 1-5
                spell.setMultiplicity(newMulti);
                handler.saveAllMulticasts(multicasts);
                updateWidgets();
            }
        }
    }
}