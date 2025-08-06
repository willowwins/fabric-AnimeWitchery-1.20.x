package net.willowins.animewitchery.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;

public class AlchemyTableScreen extends HandledScreen<AlchemyTableScreenHandler> {
    private static final Identifier CUSTOM_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/gui/alchemy_table_gui.png");
    private static final Identifier INVENTORY_TEXTURE = new Identifier("textures/gui/container/inventory.png");
    
    public AlchemyTableScreen(AlchemyTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176; // Match the texture width
        this.backgroundHeight = 252; // Match the texture height
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        
        // Draw the full custom alchemy table GUI texture at actual size (176x252)
        context.drawTexture(CUSTOM_TEXTURE, x, y, 0, 0, 176, 252);
        
        // Draw progress bar if processing
        PropertyDelegate propertyDelegate = handler.getPropertyDelegate();
        int progress = propertyDelegate.get(0);
        int maxProgress = propertyDelegate.get(1);
        boolean isProcessing = propertyDelegate.get(2) == 1;
        boolean isActivated = propertyDelegate.get(3) == 1;
        
        if (isProcessing && maxProgress > 0) {
            int progressWidth = (int) ((progress * 24.0f) / maxProgress);
            // Use a simple colored rectangle for progress
            context.fill(x + 79, y + 34, x + 79 + progressWidth, y + 34 + 17, 0xFF4CAF50);
        }
        
        // Draw activation status indicator
        if (isActivated) {
            // Draw a glowing indicator when activated
            context.fill(x + 79, y + 15, x + 79 + 18, y + 15 + 18, 0x80FFD700); // Semi-transparent gold
        }
        
        // Draw XP cost indicator if there's a valid recipe but not activated
        if (!isActivated && !isProcessing) {
            int xpCost = handler.getCurrentRecipeXpCost();
            if (xpCost > 0) {
                context.drawText(textRenderer, Text.literal("XP Cost: " + xpCost), x + 10, y + 10, 0xFFFFFF, true);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // No titles drawn - they're part of the custom texture
        // Note: Slot labels are now part of the custom texture
    }
} 