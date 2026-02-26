package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.willowins.animewitchery.effect.ModEffects;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssassinHoodItem extends ArmorItem {

    public AssassinHoodItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            // Check if equipped in Head slot
            if (player.getEquippedStack(EquipmentSlot.HEAD) == stack) {
                // Check Full Set
                ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
                ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
                ItemStack feet = player.getEquippedStack(EquipmentSlot.FEET);

                boolean hasSet = chest.getItem() == ModItems.ASSASSIN_CHESTPLATE &&
                        legs.getItem() == ModItems.ASSASSIN_LEGGINGS &&
                        feet.getItem() == ModItems.ASSASSIN_BOOTS;

                // Check Dual Daggers
                ItemStack mainHand = player.getMainHandStack();
                ItemStack offHand = player.getOffHandStack();

                boolean hasDaggers = mainHand.getItem() == ModItems.ASSASSIN_DAGGER &&
                        offHand.getItem() == ModItems.ASSASSIN_DAGGER;

                // Check if crouching AND NOT REVEALED
                if (hasSet && hasDaggers && player.isSneaking() && !player.hasStatusEffect(ModEffects.REVEALED)) {
                    // Apply Invis (Ambient=false, ShowParticles=false)
                    player.addStatusEffect(
                            new StatusEffectInstance(StatusEffects.INVISIBILITY, 10, 0, false, false, false));
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Shadow Veil:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Grants Invisibility while crouching.").formatted(Formatting.DARK_PURPLE));
        tooltip.add(Text.literal("Requires: Full Set + Dual Daggers.").formatted(Formatting.RED));
        tooltip.add(Text.literal("Hides nametag and emits no particles.").formatted(Formatting.DARK_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
