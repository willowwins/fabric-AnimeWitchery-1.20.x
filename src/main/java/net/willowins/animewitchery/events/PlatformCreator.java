package net.willowins.animewitchery.events;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.enchantments.ModEnchantments;

public class PlatformCreator {

    public static void tryCreatePlatform(PlayerEntity player) {
        World world = player.getWorld();

        // Check if player is wearing proper enchanted boots
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        if (!(boots.getItem() instanceof ArmorItem armorItem)
                || armorItem.getSlotType() != EquipmentSlot.FEET
                || EnchantmentHelper.getLevel(ModEnchantments.BOOT_ENCHANT, boots) <= 0) {
            // Simply do nothing — silently cancel
            return;
        }

        // Create floating platform
        BlockPos playerPos = player.getBlockPos();
        BlockPos start = playerPos.add(-1, -1, -1);
        BlockState blockState = ModBlocks.FLOAT_BLOCK.getDefaultState();

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                BlockPos pos = start.add(x, 0, z);
                if (world.getBlockState(pos).isOf(Blocks.AIR)) {
                    world.setBlockState(pos, blockState);
                }
            }
        }
    }
}
