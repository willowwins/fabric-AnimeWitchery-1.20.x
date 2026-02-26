package net.willowins.animewitchery.screen.class_system;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClassSelectionScreen extends Screen {
    private final boolean isSecondary;

    public ClassSelectionScreen(boolean isSecondary) {
        super(Text.of(isSecondary ? "Secondary Class Selection" : "Class Selection"));
        this.isSecondary = isSecondary;
    }

    public ClassSelectionScreen() {
        this(false);
    }

    @Override
    protected void init() {
        super.init();

        int gridX = this.width / 2 - 150;
        int gridY = 50;
        int btnWidth = 70;
        int btnHeight = 20;
        int padding = 5;

        int col = 0;
        int row = 0;

        for (String className : net.willowins.animewitchery.component.ClassRegistry.CLASSES) {
            final String fClassName = className;
            this.addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(Text.of(className), (button) -> {
                // Send Packet
                net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                        net.willowins.animewitchery.networking.ModPackets.SELECT_CLASS_ID,
                        net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create().writeString(fClassName));
                this.client.setScreen(null); // Close screen
            })
                    .dimensions(gridX + (col * (btnWidth + padding)), gridY + (row * (btnHeight + padding)), btnWidth,
                            btnHeight)
                    .build());

            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}
