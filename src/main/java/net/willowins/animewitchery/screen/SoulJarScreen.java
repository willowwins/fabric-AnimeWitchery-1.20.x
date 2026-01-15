package net.willowins.animewitchery.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.custom.SoulJarItem;
import net.willowins.animewitchery.networking.ModPackets;

public class SoulJarScreen extends HandledScreen<SoulJarScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/gui/soul_jar_gui.png");
    private ItemStack soulJarStack;
    private int selectedIndex = -1;
    private int scrollOffset = 0;

    public SoulJarScreen(SoulJarScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 220; // Taller GUI
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        updateSoulJarStack();

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Remove Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Release"), button -> {
            if (selectedIndex != -1) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(selectedIndex);
                ClientPlayNetworking.send(ModPackets.REMOVE_SOUL, buf);
                selectedIndex = -1; // Reset selection
            }
        }).dimensions(x + 110, y + 20, 60, 20).build());

        // Move Up Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Move Up"), button -> {
            if (selectedIndex > 0) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(selectedIndex); // From
                buf.writeInt(selectedIndex - 1); // To
                ClientPlayNetworking.send(ModPackets.SWAP_SOULS, buf);
                selectedIndex--;
            }
        }).dimensions(x + 110, y + 50, 60, 20).build());

        // Move Down Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Move Down"), button -> {
            NbtList souls = getSouls();
            if (selectedIndex != -1 && selectedIndex < souls.size() - 1) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(selectedIndex); // From
                buf.writeInt(selectedIndex + 1); // To
                ClientPlayNetworking.send(ModPackets.SWAP_SOULS, buf);
                selectedIndex++;
            }
        }).dimensions(x + 110, y + 80, 60, 20).build());
    }

    private void updateSoulJarStack() {
        ItemStack main = client.player.getMainHandStack();
        if (main.getItem() instanceof SoulJarItem) {
            this.soulJarStack = main;
        } else {
            this.soulJarStack = client.player.getOffHandStack();
        }
    }

    private NbtList getSouls() {
        updateSoulJarStack();
        NbtCompound nbt = soulJarStack.getNbt();
        if (nbt != null && nbt.contains("Souls")) {
            return nbt.getList("Souls", NbtElement.COMPOUND_TYPE);
        }
        return new NbtList();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        // Draw Souls List
        NbtList souls = getSouls();
        int listX = x + 10;
        int listY = y + 20;
        int visibleCount = 8;

        for (int i = 0; i < Math.min(visibleCount, souls.size()); i++) {
            int actualIndex = i + scrollOffset; // TODO: Implement scroll logic
            if (actualIndex >= souls.size())
                break;

            NbtCompound soul = souls.getCompound(actualIndex);
            String name = soul.getString("Name");
            if (name.isEmpty())
                name = soul.getString("EntityId");

            int color = (actualIndex == selectedIndex) ? 0xFFFF00 : 0xFFFFFF; // Yellow if selected
            context.drawText(textRenderer, Text.literal(name), listX, listY + i * 12, color, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // List click interact logic
        if (mouseX >= x + 10 && mouseX <= x + 100 && mouseY >= y + 20 && mouseY <= y + 120) {
            int clickedY = (int) mouseY - (y + 20);
            int index = clickedY / 12 + scrollOffset;
            NbtList souls = getSouls();
            if (index >= 0 && index < souls.size()) {
                this.selectedIndex = index;
                // Play click sound?
                return true;
            }
        }

        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
