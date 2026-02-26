package net.willowins.animewitchery.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.item.ModItems;
import java.util.List;
import java.util.Map;

public class AlchemicalEnchanterScreen extends HandledScreen<AlchemicalEnchanterScreenHandler> {
    private static final Identifier ALTERNATE_FONT = new Identifier("minecraft", "alt");
    private final List<Enchantment> uniqueEnchantments = Lists.newArrayList();
    private final Map<Enchantment, Integer> maxLevels = Maps.newHashMap();
    private Enchantment focusedEnchantment = null;
    private int selectedLevel = -1;
    private int ticks = 0;

    public AlchemicalEnchanterScreen(AlchemicalEnchanterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 350;
        this.backgroundHeight = 200;
    }

    @Override
    protected void init() {
        super.init();
        this.onEnchantmentsUpdated();
        // Shift titles away from the center to avoid overlap with the larger radial
        this.titleX = 15;
        this.titleY = 10;
        this.playerInventoryTitleX = 15;
        this.playerInventoryTitleY = 165;
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        this.ticks++;

        if (this.uniqueEnchantments.isEmpty() && !this.handler.availableEnchantments.isEmpty()) {
            this.onEnchantmentsUpdated();
        }
    }

    public void onEnchantmentsUpdated() {
        this.focusedEnchantment = null;
        this.selectedLevel = -1;
        this.uniqueEnchantments.clear();
        this.maxLevels.clear();

        for (EnchantmentLevelEntry entry : this.handler.availableEnchantments) {
            if (!this.uniqueEnchantments.contains(entry.enchantment)) {
                this.uniqueEnchantments.add(entry.enchantment);
                this.maxLevels.put(entry.enchantment, entry.level);
            }
        }
    }

    private String toRoman(int n) {
        String[] roman = { "0", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
        return (n >= 0 && n < roman.length) ? roman[n] : String.valueOf(n);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int centerX = i + 175;
        int centerY = j + 85;

        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double distSq = dx * dx + dy * dy;
        double angle = Math.atan2(dy, dx);
        if (angle < 0)
            angle += 2 * Math.PI;

        if (!this.uniqueEnchantments.isEmpty()) {
            float innerR = 50.0f;
            float outerR = 85.0f;
            if (distSq >= innerR * innerR && distSq <= outerR * outerR) {
                int count = Math.min(this.uniqueEnchantments.size(), 12);
                int segmentIdx = (int) Math.round((angle / (2 * Math.PI)) * count) % count;
                if (segmentIdx < this.uniqueEnchantments.size()) {
                    Enchantment clickedEnch = this.uniqueEnchantments.get(segmentIdx);
                    if (this.focusedEnchantment == clickedEnch) {
                        this.focusedEnchantment = null;
                        this.selectedLevel = -1;
                    } else {
                        this.focusedEnchantment = clickedEnch;
                        this.selectedLevel = -1;
                    }
                    this.client.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
                            .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.2F));
                    return true;
                }
            }

            if (this.focusedEnchantment != null) {
                int index = this.uniqueEnchantments.indexOf(this.focusedEnchantment);
                if (index != -1) {
                    int maxLvl = this.maxLevels.getOrDefault(this.focusedEnchantment, 1);

                    int sx = centerX + 115; // Separate Sub-Radial Center
                    int sy = centerY;

                    for (int level = 1; level <= maxLvl; level++) {
                        // Separate radial circle for level selection
                        double arcSpread = (maxLvl <= 3) ? (Math.PI / 1.5) : (Math.PI * 0.9);
                        double offset = (maxLvl > 1) ? (double) (level - 1) / (maxLvl - 1) - 0.5 : 0;
                        double tierAngle = offset * arcSpread; // Orbit around the right-side horizontal

                        float tierDist = 34.0f; // Dedicated sub-radial radius
                        int tx = (int) (sx + Math.cos(tierAngle) * tierDist);
                        int ty = (int) (sy + Math.sin(tierAngle) * tierDist);

                        double tdx = mouseX - tx;
                        double tdy = mouseY - ty;
                        if (tdx * tdx + tdy * tdy <= 12 * 12) {
                            this.selectedLevel = level;
                            this.client.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
                                    .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            return true;
                        }
                    }
                }
            }
        }

        if (this.selectedLevel != -1 && this.focusedEnchantment != null) {
            int ex = centerX, ey = centerY + 35;
            if (Math.pow(mouseX - ex, 2) + Math.pow(mouseY - ey, 2) <= 22 * 22) {
                int enchIndex = this.uniqueEnchantments.indexOf(this.focusedEnchantment);
                if (enchIndex != -1) {
                    int encodedId = (enchIndex * 100) + this.selectedLevel;
                    if (this.handler.onButtonClick(this.client.player, encodedId)) {
                        this.client.interactionManager.clickButton(this.handler.syncId, encodedId);
                        this.focusedEnchantment = null;
                        this.selectedLevel = -1;
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawCircularSegment(DrawContext context, int centerX, int centerY, float innerRadius,
            float outerRadius,
            double startAngle, double endAngle, int color, float z) {
        // High-density dot fill for truly SOLID lines
        double angleDist = Math.abs(endAngle - startAngle);
        // Density based on circumference to ensure no gaps
        int angleSteps = Math.max(30, (int) (angleDist * outerRadius * 2.5f));
        double angleStep = (endAngle - startAngle) / angleSteps;

        float radiusDist = outerRadius - innerRadius;
        // For lines/arcs, we only need 1 step if width is small, but for solid fills we
        // need more
        int radiusSteps = Math.max(1, (int) (radiusDist * 1.5f));
        float radiusStep = radiusSteps > 0 ? radiusDist / radiusSteps : 1.0f;

        for (int i = 0; i <= angleSteps; i++) {
            double angle = startAngle + i * angleStep;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            for (int j = 0; j <= radiusSteps; j++) {
                float r = innerRadius + j * radiusStep;
                int px = (int) (centerX + cos * r);
                int py = (int) (centerY + sin * r);
                context.fill(px, py, px + 1, py + 1, color);
            }
        }
    }

    private void drawArc(DrawContext context, int centerX, int centerY, float radius, double startAngle,
            double endAngle, int color, int steps, float z) {
        // Reduced thickness to 1px
        drawCircularSegment(context, centerX, centerY, radius - 0.5f, radius + 0.5f, startAngle, endAngle, color, z);
    }

    private void drawLine(DrawContext context, float x1, float y1, float x2, float y2, float width, int color,
            float z) {
        float dx = x2 - x1, dy = y2 - y1, len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len < 0.1f)
            return;

        int steps = (int) (len * 3.0f); // Higher density for solid lines
        for (int i = 0; i <= steps; i++) {
            float t = (float) i / steps;
            int px = (int) (x1 + dx * t);
            int py = (int) (y1 + dy * t);
            context.fill(px, py, px + (int) Math.ceil(width), py + (int) Math.ceil(width), color);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = this.x, j = this.y;
        int playerLevel = this.client.player.experienceLevel, centerX = i + 175, centerY = j + 85;

        // Render solid background segments
        context.fill(i, j, i + this.backgroundWidth, j + this.backgroundHeight, 0xF2050505);
        context.fill(i - 2, j - 2, i + this.backgroundWidth + 2, j, 0xFF543270);
        context.fill(i - 2, j + this.backgroundHeight, i + this.backgroundWidth + 2, j + this.backgroundHeight + 2,
                0xFF543270);
        context.fill(i - 2, j, i, j + this.backgroundHeight, 0xFF543270);
        context.fill(i + this.backgroundWidth, j, i + this.backgroundWidth + 2, j + this.backgroundHeight, 0xFF543270);

        context.draw(); // Flush background
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Draw header title if focused
        if (this.focusedEnchantment != null) {
            Text title = this.focusedEnchantment.getName(1);
            context.drawCenteredTextWithShadow(this.textRenderer, title, centerX, j + 15, 0xFFBB86FC);
        }

        if (!this.uniqueEnchantments.isEmpty()) {
            float innerR = 50.0f, outerR = 85.0f;
            int count = Math.min(this.uniqueEnchantments.size(), 12);
            double segmentWidth = (2 * Math.PI) / count;
            double mouseAngle = Math.atan2(mouseY - centerY, mouseX - centerX);
            if (mouseAngle < 0)
                mouseAngle += 2 * Math.PI;
            boolean mouseInRing = Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2) >= innerR * innerR &&
                    Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2) <= outerR * outerR;

            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 50.0f); // Ensure visibility above backgrounds

            // 1. Draw Main Purple Outlines (NO FILLING)
            int defaultBorder = 0xFF8A2BE2; // Purple
            drawArc(context, centerX, centerY, innerR, 0, 2 * Math.PI, defaultBorder, 60, 0.0f);
            drawArc(context, centerX, centerY, outerR, 0, 2 * Math.PI, defaultBorder, 60, 0.0f);
            for (int dividerIdx = 0; dividerIdx < count; dividerIdx++) {
                double divAngle = dividerIdx * segmentWidth - Math.PI / count;
                drawLine(context, (float) (centerX + Math.cos(divAngle) * innerR),
                        (float) (centerY + Math.sin(divAngle) * innerR),
                        (float) (centerX + Math.cos(divAngle) * outerR),
                        (float) (centerY + Math.sin(divAngle) * outerR), 1.0f,
                        defaultBorder, 0.0f);
            }

            // 2. Focused Segment Highlight (Thicker highlight for clarity)
            if (this.focusedEnchantment != null) {
                int idx = this.uniqueEnchantments.indexOf(this.focusedEnchantment);
                if (idx != -1) {
                    double start = idx * segmentWidth - Math.PI / count,
                            end = (idx + 1) * segmentWidth - Math.PI / count;
                    // Draw a 2px thick highlight for the focused segment
                    drawCircularSegment(context, centerX, centerY, 49.0f, 51.0f, start, end, 0xFFBB86FC, 0.0f);
                    drawCircularSegment(context, centerX, centerY, 84.0f, 86.0f, start, end, 0xFFBB86FC, 0.0f);
                }
            }
            context.getMatrices().pop();
            context.draw(); // Flush custom geometry

            // 3. Draw Symbols/Tooltip Text
            if (true) {
                for (int k = 0; k < this.uniqueEnchantments.size(); k++) {
                    double startAngle = k * segmentWidth - Math.PI / count,
                            endAngle = (k + 1) * segmentWidth - Math.PI / count,
                            centerAngle = (startAngle + endAngle) / 2.0;
                    Enchantment enchantment = this.uniqueEnchantments.get(k);
                    boolean isHovered = mouseInRing
                            && (int) Math.round((mouseAngle / (2 * Math.PI)) * count) % count == k;

                    float symbolR = (innerR + outerR) / 2.0f;
                    int sx = (int) (centerX + Math.cos(centerAngle) * symbolR),
                            sy = (int) (centerY + Math.sin(centerAngle) * symbolR);
                    String nameStr = enchantment.getName(1).getString();
                    Text symbol = Text.literal(nameStr.isEmpty() ? "?" : nameStr.substring(0, 1).toUpperCase())
                            .setStyle(Style.EMPTY.withFont(ALTERNATE_FONT));
                    context.getMatrices().push();
                    context.getMatrices().translate(0, 0, 50.0f);
                    context.drawText(this.textRenderer, symbol, sx - this.textRenderer.getWidth(symbol) / 2, sy - 4,
                            0xFFFFFF, false);
                    context.getMatrices().pop();

                    if (isHovered) {
                        int tx = (int) (centerX + Math.cos(centerAngle) * (outerR + 15)),
                                ty = (int) (centerY + Math.sin(centerAngle) * (outerR + 15));
                        Text name = enchantment.getName(1);
                        context.getMatrices().push();
                        context.getMatrices().translate(0, 0, 110.0f);
                        context.drawText(this.textRenderer, name, tx - this.textRenderer.getWidth(name) / 2, ty - 4,
                                0xFFFFFF, true);
                        context.getMatrices().pop();
                    }
                }
            }
        }

        // Focused level buttons
        if (this.focusedEnchantment != null) {
            int maxLvl = this.maxLevels.getOrDefault(this.focusedEnchantment, 1);
            int sx = centerX + 115, sy = centerY;

            // Removed Sub-Radial Background for cleaner skeleton look

            for (int level = 1; level <= maxLvl; level++) {
                int levelReq = 10 + (level - 1) * 5;
                boolean reached = playerLevel >= levelReq;
                double arcSpread = (maxLvl <= 3) ? (Math.PI / 1.5) : (Math.PI * 0.9);
                double offset = (maxLvl > 1) ? (double) (level - 1) / (maxLvl - 1) - 0.5 : 0;
                double tierA = offset * arcSpread;
                float tierDist = 34.0f;
                int tx = (int) (sx + Math.cos(tierA) * tierDist), ty = (int) (sy + Math.sin(tierA) * tierDist);

                boolean tHoverVal = Math.pow(mouseX - tx, 2) + Math.pow(mouseY - ty, 2) <= 12 * 12;
                boolean isSelected = (this.selectedLevel == level);
                int bgColor = (isSelected ? (int) (220 + 35 * Math.sin(this.ticks * 0.1))
                        : (tHoverVal ? 220 : 180)) << 24 |
                        (isSelected ? 0xBB86FC : (tHoverVal ? 0x888888 : (reached ? 0x333333 : 0x111111)));
                drawCircularSegment(context, tx, ty, 0, 11.0f, 0, 2 * Math.PI, bgColor, 0.0f);
                drawArc(context, tx, ty, 11.0f, 0, 2 * Math.PI, isSelected ? 0xFFBB86FC : 0xFF8A2BE2, 20, 0.0f);

                context.draw(); // Flush dot before text
                context.getMatrices().push();
                context.getMatrices().translate(0, 0, 70.0f);
                String label = toRoman(level);
                context.drawText(this.textRenderer, label, tx - this.textRenderer.getWidth(label) / 2, ty - 4, 0xFFFFFF,
                        false);
                context.getMatrices().pop();

                if (tHoverVal || isSelected) {
                    String reqText = levelReq + " XP";
                    context.getMatrices().push();
                    context.getMatrices().translate(0, 0, 80.0f);
                    context.drawText(this.textRenderer, reqText, tx - this.textRenderer.getWidth(reqText) / 2, ty + 12,
                            reached ? 0x80FF20 : 0xFF5555, true);
                    context.getMatrices().pop();
                }
            }
        }

        // Enchant button
        if (this.selectedLevel != -1 && this.focusedEnchantment != null) {
            int ex = centerX, ey = centerY + 35;
            boolean canEnch = !this.handler.getSlot(0).getStack().isEmpty()
                    && !this.handler.getSlot(1).getStack().isEmpty()
                    && playerLevel >= (10 + (selectedLevel - 1) * 5);
            int btnColor = (int) (200 + 55 * Math.sin(this.ticks * 0.1)) << 24 | (canEnch ? 0x00AA44 : 0x444444);
            drawCircularSegment(context, ex, ey, 0, 22.0f, 0, 2 * Math.PI, btnColor, 0.0f);
            drawArc(context, ex, ey, 22.0f, 0, 2 * Math.PI, canEnch ? 0xFFBB86FC : 0xFF2B2B2B, 30, 0.0f);
            context.draw(); // Flush button
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 55.0f);
            context.drawText(this.textRenderer, "ENCHANT", ex - this.textRenderer.getWidth("ENCHANT") / 2, ey - 4,
                    0xFFFFFF, true);
            context.getMatrices().pop();

            ItemStack catalyst = this.handler.getSlot(1).getStack();
            if (catalyst.isOf(ModItems.RESONANT_CATALYST)) {
                int manaCost = (10 + (selectedLevel - 1) * 5) * 100;
                // Shift mana cost further down to y=180
                context.drawText(this.textRenderer, "Mana: " + manaCost, i + 20, j + 180, 0x3B9EFF, true);
            }
        }

        // Central slots background highlights
        context.fill(i + 155 - 2, j + 75 - 2, i + 155 + 18, j + 75 + 18, 0xFF2B2B2B);
        context.fill(i + 155 - 1, j + 75 - 1, i + 155 + 17, j + 75 + 17, 0xFF000000);
        context.fill(i + 177 - 2, j + 75 - 2, i + 177 + 18, j + 75 + 18, 0xFF2B2B2B);
        context.fill(i + 177 - 1, j + 75 - 1, i + 177 + 17, j + 75 + 17, 0xFF000000);
        context.draw();
        RenderSystem.disableBlend();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
