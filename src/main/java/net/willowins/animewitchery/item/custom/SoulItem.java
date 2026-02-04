package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulItem extends Item {
    public SoulItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("EntityData")) {
            NbtCompound data = nbt.getCompound("EntityData");

            // Name
            if (nbt.contains("EntityName")) {
                tooltip.add(Text.literal("Entity: " + nbt.getString("EntityName")).formatted(Formatting.GRAY));
            }

            // Health
            if (data.contains("Health")) {
                float health = data.getFloat("Health");
                tooltip.add(Text.literal("Health: " + String.format("%.1f", health)).formatted(Formatting.RED));
            }

            // Equipment
            if (data.contains("HandItems")) {
                NbtList hands = data.getList("HandItems", NbtElement.COMPOUND_TYPE);
                // Main Hand
                if (hands.size() > 0 && !hands.getCompound(0).isEmpty()) {
                    ItemStack handStack = ItemStack.fromNbt(hands.getCompound(0));
                    if (!handStack.isEmpty()) {
                        tooltip.add(Text.literal("Main Hand: ").formatted(Formatting.GOLD)
                                .append(handStack.getName().copy().formatted(Formatting.WHITE)));
                    }
                }
                // Off Hand
                if (hands.size() > 1 && !hands.getCompound(1).isEmpty()) {
                    ItemStack handStack = ItemStack.fromNbt(hands.getCompound(1));
                    if (!handStack.isEmpty()) {
                        tooltip.add(Text.literal("Off Hand: ").formatted(Formatting.GOLD)
                                .append(handStack.getName().copy().formatted(Formatting.WHITE)));
                    }
                }
            }

            if (data.contains("ArmorItems")) {
                NbtList armor = data.getList("ArmorItems", NbtElement.COMPOUND_TYPE);
                // Armor slots: 0=feet, 1=legs, 2=chest, 3=head
                String[] labels = { "Feet: ", "Legs: ", "Chest: ", "Head: " };
                for (int i = 0; i < armor.size() && i < labels.length; i++) {
                    if (!armor.getCompound(i).isEmpty()) {
                        ItemStack armorStack = ItemStack.fromNbt(armor.getCompound(i));
                        if (!armorStack.isEmpty()) {
                            tooltip.add(Text.literal(labels[i]).formatted(Formatting.BLUE)
                                    .append(armorStack.getName().copy().formatted(Formatting.WHITE)));
                        }
                    }
                }
            }
        } else {
            tooltip.add(Text.literal("Empty Soul").formatted(Formatting.DARK_GRAY));
        }
    }
}
