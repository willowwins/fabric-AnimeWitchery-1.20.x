package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.willowins.animewitchery.component.IClassComponent;
import net.willowins.animewitchery.mana.ModComponents;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkillOrbItem extends Item {
    private final int minLevel;
    private final int maxLevel;
    private final int points;

    public SkillOrbItem(Settings settings, int minLevel, int maxLevel, int points) {
        super(settings);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.points = points;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            IClassComponent classData = ModComponents.CLASS_DATA.get(user);
            int currentLevel = classData.getLevel();

            // Validation: Range Check
            if (currentLevel >= minLevel && currentLevel <= maxLevel) {
                // Level Up!
                classData.addLevel(1);
                classData.addSkillPoints(points); // Add points (e.g. 1 per level)

                // Consume item
                if (!user.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                user.sendMessage(Text.of("§aLeveled Up! §fCurrent Level: " + classData.getLevel()), true);
                return TypedActionResult.success(stack);
            } else {
                if (currentLevel > maxLevel) {
                    user.sendMessage(Text.of("§cYou are too strong for this orb! §7(Max Level: " + maxLevel + ")"),
                            true);
                } else {
                    user.sendMessage(
                            Text.of("§cYou are not strong enough for this orb! §7(Min Level: " + minLevel + ")"), true);
                }
                return TypedActionResult.fail(stack);
            }
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.of("§7Usable Level Range: §f" + minLevel + " - " + maxLevel));
        tooltip.add(Text.of("§7Grants: §f" + points + " Skill Point"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
