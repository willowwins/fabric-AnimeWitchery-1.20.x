package net.willowins.animewitchery.screen.class_system;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.willowins.animewitchery.component.IClassComponent;

public class SkillTreeScreen extends Screen {
    private final IClassComponent classData;

    public SkillTreeScreen(IClassComponent classData) {
        super(Text.of("Skill Tree"));
        this.classData = classData;
    }

    @Override
    protected void init() {
        super.init();
    }

    private double scrollX = 0;
    private double scrollY = 0;
    private boolean isDragging = false;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int centerX = (int) (this.width / 2 + scrollX);
        int centerY = (int) (this.height / 2 + scrollY);

        // Draw Title (Static)
        String title = classData.getPrimaryClass() + " - Lvl " + classData.getLevel();
        context.drawCenteredTextWithShadow(this.textRenderer, title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Skill Points: " + classData.getSkillPoints(),
                this.width / 2, 35, 0xAAAAAA);

        // Draw Tier Barriers
        int tierHeight = 50;
        int maxTier = 10;
        for (int i = 1; i < maxTier; i++) {
            // Barrier is between Tier i and i+1
            // Tier 1 Y = 0. Tier 2 Y = -50. Barrier at -25?
            int barrierY = centerY - ((i - 1) * tierHeight) - (tierHeight / 2);

            boolean tierCompleted = isTierCompleted(i);
            int color = tierCompleted ? 0x4455FF55 : 0xAAFF0000; // Green if done, Red if blocking

            context.fill(centerX - 200, barrierY, centerX + 200, barrierY + 2, color);
            if (!tierCompleted) {
                context.drawTextWithShadow(textRenderer, "Tier " + i + " Incomplete", centerX - 250, barrierY - 4,
                        0xFF5555);
            }
        }

        // Render Connections First (Behind nodes)
        for (net.willowins.animewitchery.component.SkillRegistry.SkillDef skill : net.willowins.animewitchery.component.SkillRegistry.SKILLS
                .values()) {
            if (shouldShowSkill(skill)) {
                if (skill.parentId != null) {
                    net.willowins.animewitchery.component.SkillRegistry.SkillDef parent = net.willowins.animewitchery.component.SkillRegistry
                            .get(skill.parentId);
                    if (parent != null) {
                        drawLine(context, centerX + parent.x, centerY + parent.y, centerX + skill.x, centerY + skill.y,
                                0xFF888888);
                    }
                }
            }
        }

        // Render Nodes
        net.willowins.animewitchery.component.SkillRegistry.SkillDef hoveredSkill = null;

        for (net.willowins.animewitchery.component.SkillRegistry.SkillDef skill : net.willowins.animewitchery.component.SkillRegistry.SKILLS
                .values()) {
            if (shouldShowSkill(skill)) {
                int x = centerX + skill.x - 10; // Center the 20x20 node
                int y = centerY + skill.y - 10;

                boolean unlocked = classData.isSkillUnlocked(skill.id);
                boolean affordable = classData.getSkillPoints() >= skill.cost;
                boolean parentUnlocked = skill.parentId == null || classData.isSkillUnlocked(skill.parentId);
                boolean tierUnlocked = isTierUnlocked(skill.tier);

                boolean available = !unlocked && affordable && parentUnlocked && tierUnlocked;

                int color = 0xFF555555; // Locked / Unavailable
                if (unlocked)
                    color = 0xFF55FF55; // Green
                else if (available)
                    color = 0xFFFFFF55; // Yellow
                else if (!tierUnlocked)
                    color = 0xAA222222; // Faded/Dark

                context.fill(x, y, x + 20, y + 20, color);
                context.drawBorder(x, y, 20, 20, 0xFF000000);

                // Tier text for debug/clarity
                if (!tierUnlocked) {
                    // context.drawText(textRenderer, "T" + skill.tier, x + 4, y + 6, 0x888888,
                    // false);
                }

                // Check Hover
                if (mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 20) {
                    hoveredSkill = skill;
                }
            }
        }

        // Render Tooltip
        if (hoveredSkill != null) {
            java.util.List<Text> tooltip = new java.util.ArrayList<>();
            tooltip.add(Text.of("§e" + hoveredSkill.name + " §7(Tier " + hoveredSkill.tier + ")"));
            tooltip.add(Text.of("§7" + hoveredSkill.description));
            tooltip.add(Text.of("§bCost: " + hoveredSkill.cost + " SP"));

            boolean unlocked = classData.isSkillUnlocked(hoveredSkill.id);
            boolean tierUnlocked = isTierUnlocked(hoveredSkill.tier);

            if (unlocked)
                tooltip.add(Text.of("§a[UNLOCKED]"));
            else if (!tierUnlocked)
                tooltip.add(Text.of("§c[LOCKED: Complete Tier " + (hoveredSkill.tier - 1) + "]"));
            else if (classData.getSkillPoints() < hoveredSkill.cost)
                tooltip.add(Text.of("§c[NOT ENOUGH POINTS]"));
            else if (hoveredSkill.parentId != null && !classData.isSkillUnlocked(hoveredSkill.parentId))
                tooltip.add(Text.of("§c[LOCKED BY PARENT]"));
            else
                tooltip.add(Text.of("§e[CLICK TO UNLOCK]"));

            context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private boolean isTierCompleted(int tier) {
        if (tier < 1)
            return true;
        // Check if ALL skills in this tier are unlocked
        for (net.willowins.animewitchery.component.SkillRegistry.SkillDef skill : net.willowins.animewitchery.component.SkillRegistry.SKILLS
                .values()) {
            if (skill.tier == tier && shouldShowSkill(skill)) {
                if (!classData.isSkillUnlocked(skill.id)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Can I access this tier? (Requires Previous Tier Completion)
    private boolean isTierUnlocked(int tier) {
        if (tier <= 1)
            return true;
        return isTierCompleted(tier - 1);
    }

    private boolean shouldShowSkill(net.willowins.animewitchery.component.SkillRegistry.SkillDef skill) {
        // Show core skills AND class skills
        return skill.id.startsWith("core_") || skill.id.startsWith(classData.getPrimaryClass().toLowerCase());
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) { // Left drag
            this.scrollX += deltaX;
            this.scrollY += deltaY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left Click
            int centerX = (int) (this.width / 2 + scrollX);
            int centerY = (int) (this.height / 2 + scrollY);

            for (net.willowins.animewitchery.component.SkillRegistry.SkillDef skill : net.willowins.animewitchery.component.SkillRegistry.SKILLS
                    .values()) {
                if (shouldShowSkill(skill)) {
                    int x = centerX + skill.x - 10;
                    int y = centerY + skill.y - 10;

                    if (mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 20) {
                        // Only attempt unlock if not dragging significantly
                        attemptUnlock(skill);
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void attemptUnlock(net.willowins.animewitchery.component.SkillRegistry.SkillDef skill) {
        boolean unlocked = classData.isSkillUnlocked(skill.id);
        boolean affordable = classData.getSkillPoints() >= skill.cost;
        boolean parentUnlocked = skill.parentId == null || classData.isSkillUnlocked(skill.parentId);
        boolean tierUnlocked = isTierUnlocked(skill.tier);

        if (!unlocked && affordable && parentUnlocked && tierUnlocked) {
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                    net.willowins.animewitchery.networking.ModPackets.UNLOCK_SKILL_ID,
                    net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create().writeString(skill.id));
            // Play sound?
            net.minecraft.client.MinecraftClient.getInstance().getSoundManager().play(
                    net.minecraft.client.sound.PositionedSoundInstance.master(
                            net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    private void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        // Bresenham's Line Algorithm directly into fill? No, that's too slow for Java
        // layer maybe?
        // Actually, just drawing a thin rotated rect is complex without rotation
        // support in fill.
        // Let's use specific diagonal logic or just simple stepping.

        // Simple manual line drawing for now (inefficient but safe)
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            context.fill(x1, y1, x1 + 1, y1 + 1, color);
            if (x1 == x2 && y1 == y2)
                break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
        }
    }
}
