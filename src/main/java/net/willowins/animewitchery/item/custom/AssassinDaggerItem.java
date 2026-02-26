package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssassinDaggerItem extends SwordItem {

    public AssassinDaggerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    // Custom logic will be handled in ClassCombatHandler, but we add a tooltip
    // here.
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Assassin's Art:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Deals 1.5x Damage from behind or while invisible.").formatted(Formatting.DARK_RED));
        tooltip.add(Text.literal("Attacking removes invisibility for 15s.").formatted(Formatting.RED));
        super.appendTooltip(stack, world, tooltip, context);
    }

    // Helper method to check if attack is "from behind"
    public static boolean isBehind(LivingEntity attacker, LivingEntity target) {
        Vec3d lookVec = target.getRotationVector();
        Vec3d attackVec = attacker.getPos().subtract(target.getPos()).normalize();
        // Dot product: < 0 means in front (facing opposite directions), > 0 means
        // behind (facing same direction)
        // Actually, if attacker is behind, they are looking in the SAME direction as
        // the target.
        // Wait, vector math:
        // Target Look: (0, 0, 1) [South]
        // Attacker Look: (0, 0, 1) [South] -> Attacker is behind looking at target's
        // back.
        // Dot product of Look vectors > 0.5 (approx 60 deg cone).
        return attacker.getRotationVector().dotProduct(target.getRotationVector()) > 0.5;
    }
}
