package net.willowins.animewitchery.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class IntermediaryScreen extends Screen {

    private int tickCount = 0;
    private static final int MAX_TICKS = 600; // 30 seconds timeout

    public IntermediaryScreen() {
        super(Text.literal("Loading"));
    }

    @Override
    protected void init() {
        super.init();
        System.out.println("[IntermediaryScreen] Screen initialized");
    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;

        // Safety timeout - if stuck for 30 seconds, return to title
        if (tickCount > MAX_TICKS) {
            System.out.println("[IntermediaryScreen] Timeout reached - returning to title");
            if (this.client != null) {
                this.client.setScreen(new net.minecraft.client.gui.screen.TitleScreen());
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render dirt background like loading screen
        if (this.client != null) {
            this.renderBackgroundTexture(context);
        }

        // Show loading message
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                "Loading World...",
                this.width / 2,
                this.height / 2,
                0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        // Allow closing after timeout
        if (tickCount > MAX_TICKS) {
            super.close();
        } else {
            System.out.println("[IntermediaryScreen] Close attempted - ignoring (use timeout)");
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return tickCount > MAX_TICKS;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
