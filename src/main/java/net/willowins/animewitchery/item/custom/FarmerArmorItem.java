package net.willowins.animewitchery.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import net.minecraft.entity.EquipmentSlot;

public class FarmerArmorItem extends ArmorItem {
    public FarmerArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player && slot == EquipmentSlot.CHEST.getEntitySlotId()) {
            // Passive growth
            if (player.age % 20 == 0) { // Every second
                growCropsNearby(world, player.getBlockPos(), 5);
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void growCropsNearby(World world, BlockPos center, int range) {
        if (!(world instanceof ServerWorld serverWorld))
            return;

        // Pick a random spot in range to try bonemeal
        for (int i = 0; i < 3; i++) { // Try 3 times per second
            int x = center.getX() + world.random.nextInt(range * 2 + 1) - range;
            int y = center.getY(); // Only same level? or +/- 1
            int z = center.getZ() + world.random.nextInt(range * 2 + 1) - range;
            BlockPos pos = new BlockPos(x, y, z);

            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof CropBlock || state.getBlock() instanceof Fertilizable) {
                if (state.getBlock() instanceof Fertilizable fertilizable) {
                    if (fertilizable.isFertilizable(world, pos, state, world.isClient)) {
                        if (world.random.nextInt(10) == 0) { // 10% chance
                            fertilizable.grow(serverWorld, world.random, pos, state);
                        }
                    }
                }
            }
        }
    }
}
