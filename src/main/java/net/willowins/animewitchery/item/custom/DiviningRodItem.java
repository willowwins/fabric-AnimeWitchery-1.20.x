package net.willowins.animewitchery.item.custom;

import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiviningRodItem extends Item {
    public DiviningRodItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        BlockPos pos = user.getBlockPos();
        int radius = 10;
        java.util.Set<BlockPos> foundOres = new java.util.HashSet<>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos check = pos.add(x, y, z);
                    if (isValuable(world, check)) {
                        foundOres.add(check);
                    }
                }
            }
        }

        if (!world.isClient) {
            if (!foundOres.isEmpty()) {
                // Find nearest for chat message
                BlockPos nearestOre = null;
                double minDist = Double.MAX_VALUE;
                for (BlockPos ore : foundOres) {
                    double dist = ore.getSquaredDistance(pos);
                    if (dist < minDist) {
                        minDist = dist;
                        nearestOre = ore;
                    }
                }
                user.sendMessage(Text.literal("Found valuable ore at (" + nearestOre.getX() + ", " + nearestOre.getY()
                        + ", " + nearestOre.getZ() + ")").formatted(Formatting.GOLD), true);
            } else {
                user.sendMessage(Text.literal("No valuable ore nearby.").formatted(Formatting.GRAY), true);
            }
            user.getItemCooldownManager().set(this, 50);
            if (!user.getAbilities().creativeMode) {
                user.getStackInHand(hand).decrement(1);
            }
        } else {
            // Client Side Rendering
            if (!foundOres.isEmpty()) {
                net.willowins.animewitchery.client.DiviningRodRenderer.highlight(foundOres);
                user.playSound(net.minecraft.sound.SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.0f);
            }
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private boolean isValuable(World world, BlockPos pos) {
        var block = world.getBlockState(pos).getBlock();
        return block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE ||
                block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE ||
                block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE ||
                block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE ||
                block == Blocks.ANCIENT_DEBRIS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.animewitchery.divining_rod.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
