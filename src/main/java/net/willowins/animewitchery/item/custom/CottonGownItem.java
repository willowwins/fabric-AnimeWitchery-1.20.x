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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CottonGownItem extends ArmorItem {

    public CottonGownItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            // Check Chest slot
            if (player.getEquippedStack(EquipmentSlot.CHEST) == stack) {
                // Photosynthesis: Check Sunlight
                BlockPos pos = player.getBlockPos().up();
                int light = world.getLightLevel(LightType.SKY, pos);
                boolean isDay = world.isDay();

                // Also check if in "Nature" biome? (Tag: #minecraft:is_forest etc).
                // Let's stick to Light for "Photosynthesis".

                if (light > 10 && isDay && world.isSkyVisible(pos)) {
                    // Heal / Regen
                    if (player.age % 100 == 0) { // Every 5s
                        player.heal(1.0f);
                        player.getHungerManager().add(1, 0.5f);

                        // Repair item?
                        if (stack.isDamaged()) {
                            stack.setDamage(stack.getDamage() - 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Photosynthesis:").formatted(Formatting.GRAY));
        tooltip.add(
                Text.literal("Regenerates Health, Hunger, and Durability in Sunlight.").formatted(Formatting.GREEN));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
