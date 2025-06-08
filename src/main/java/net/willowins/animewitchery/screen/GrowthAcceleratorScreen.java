package net.willowins.animewitchery.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GrowthAcceleratorScreen extends HandledScreen<GrowthAcceleratorScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/generic_54.png");

    public GrowthAcceleratorScreen(GrowthAcceleratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 222; // 3 chest rows + player inventory + hotbar
        this.playerInventoryTitleY = 114; // Adjust this to position the player inventory label
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, this.title, 8, 6, 0x404040, true);
        context.drawText(textRenderer, this.playerInventoryTitle, 8, 128, 0x404040, true);
    }


    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
