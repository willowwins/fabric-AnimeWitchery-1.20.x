package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ButcherApronItem extends ArmorItem {

    public ButcherApronItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            // Must be in Chest slot
            if (player.getEquippedStack(EquipmentSlot.CHEST) == stack) {
                // Enhanced Smell: Highlight nearby entities
                if (player.age % 20 == 0) { // check every second
                    List<LivingEntity> nearby = world.getEntitiesByClass(LivingEntity.class,
                            new Box(player.getBlockPos()).expand(10), // 10 block radius
                            e -> e != player && !e.hasStatusEffect(StatusEffects.GLOWING));

                    for (LivingEntity e : nearby) {
                        // Apply glowing for 2 seconds (refreshable)
                        e.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 40, 0, false, false, false));
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Scent Tracker:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Outlines nearby entities.").formatted(Formatting.GOLD));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
