package net.willowins.animewitchery.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CosmeticBagScreen extends HandledScreen<CosmeticBagScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/generic_54.png");

    public CosmeticBagScreen(CosmeticBagScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 114 + 3 * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, 3 * 18 + 17);
        context.drawTexture(TEXTURE, x, y + 3 * 18 + 17, 0, 126, this.backgroundWidth, 96);

        // Draw Vanity Slot Highlights (Slots 0-3)
        // Coords relative to screen: x + 8 + col*18, y + 18 + row*18
        // Row 0, Cols 0-3
        for (int i = 0; i < 4; i++) {
            int slotX = x + 7 + i * 18;
            int slotY = y + 17;
            // Draw a colored overlay or outline
            // Using a semi-transparent colored box to highlight
            context.fill(slotX, slotY, slotX + 18, slotY + 18, 0x40FFD700); // Transparent Gold

            // Draw slot border
            context.drawBorder(slotX, slotY, 18, 18, 0xFFD700FF);

            // Draw empty slot icon if empty
            if (this.handler.getSlot(i).getStack().isEmpty()) {
                Identifier icon = switch (i) {
                    case 0 -> new Identifier("minecraft", "textures/item/empty_armor_slot_helmet.png");
                    case 1 -> new Identifier("minecraft", "textures/item/empty_armor_slot_chestplate.png");
                    case 2 -> new Identifier("minecraft", "textures/item/empty_armor_slot_leggings.png");
                    case 3 -> new Identifier("minecraft", "textures/item/empty_armor_slot_boots.png");
                    default -> null;
                };
                if (icon != null) {
                    context.drawTexture(icon, slotX + 1, slotY + 1, 0, 0, 16, 16, 16, 16);
                }
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
