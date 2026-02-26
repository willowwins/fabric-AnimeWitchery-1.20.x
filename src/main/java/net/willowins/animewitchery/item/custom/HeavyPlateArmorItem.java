package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeavyPlateArmorItem extends ArmorItem {

    public HeavyPlateArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            // Apply effects if wearing ANY piece? Or full set?
            // "Resistance to damage but slightly slower"
            // Let's make it per-piece for simplicity, or check if equipped.
            // If checking every tick, we should be careful.

            // Checking if THIS item is equipped
            // We use Slot ID mapping for Armor
            // HEAD: 3, CHEST: 2, LEGS: 1, BOOTS: 0 (EquipmentSlot indices are different)
            // safer to check:
            if (player.getEquippedStack(this.getSlotType()) == stack) {
                // Apply Resistance 1 and Slowness 1 (short duration to refresh)
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 0, false, false, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, 0, false, false, false));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Juggernaut:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Increases Resistance.").formatted(Formatting.BLUE));
        tooltip.add(Text.literal("Decreases Movement Speed.").formatted(Formatting.RED));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
