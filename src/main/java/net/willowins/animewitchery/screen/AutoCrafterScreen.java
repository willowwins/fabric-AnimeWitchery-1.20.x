package net.willowins.animewitchery.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AutoCrafterScreen extends HandledScreen<AutoCrafterScreenHandler> {

    // Use the crafting table GUI texture from Minecraft
    private static final Identifier CRAFTING_TABLE_TEXTURE =
            new Identifier("minecraft", "textures/gui/container/crafting_table.png");

    public AutoCrafterScreen(AutoCrafterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // Set your GUI size same as crafting table (usually 176x166)
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Set shader and texture
        RenderSystem.setShaderTexture(0, CRAFTING_TABLE_TEXTURE);

        // Calculate top-left corner position to center the GUI
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        // Draw the textured rectangle for background
        context.drawTexture(CRAFTING_TABLE_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // Draw the title text at the top left (slightly offset)
        context.drawText(textRenderer, this.title, 28, 6, 0x404040, false);

        // Draw player inventory title at bottom left
        context.drawText(textRenderer, this.playerInventoryTitle, 8, backgroundHeight - 94, 0x404040, false);
    }
}
