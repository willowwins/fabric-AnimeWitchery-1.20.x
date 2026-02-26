package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OathbreakerItem extends SwordItem {
    public OathbreakerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user.isSneaking()) {
            ItemStack stack = user.getStackInHand(hand);
            boolean active = isActive(stack);
            setActive(stack, !active);
            user.sendMessage(Text.literal("Oathbreaker Mode: " + (!active ? "Active" : "Inactive"))
                    .formatted(!active ? Formatting.RED : Formatting.GRAY), true);
            return TypedActionResult.success(stack);
        }
        return super.use(world, user, hand);
    }

    public static boolean isActive(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.getBoolean("Active");
    }

    public static void setActive(ItemStack stack, boolean active) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean("Active", active);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(
                Text.literal("Shift-Right Click to Toggle Mode").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        if (isActive(stack)) {
            tooltip.add(Text.literal("Active").formatted(Formatting.RED));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
