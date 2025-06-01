package net.willowins.animewitchery.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PlayerUseDispenserScreen extends HandledScreen<PlayerUseDispenserScreenHandler> {

    private static final int TEXTURE_WIDTH = 176;
    private static final int TEXTURE_HEIGHT = 166;
    private static final Identifier VANILLA_DISPENSER_TEXTURE = new Identifier("minecraft", "textures/gui/container/dispenser.png");

    public PlayerUseDispenserScreen(PlayerUseDispenserScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = TEXTURE_WIDTH;
        this.backgroundHeight = TEXTURE_HEIGHT;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.setShaderColor(1f, 1f, 1f, 1f);
        context.drawTexture(VANILLA_DISPENSER_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}