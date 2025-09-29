package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.enchantments.ModEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlastingBreakHandler {

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerWorld serverWorld)) return true;

            ItemStack tool = player.getMainHandStack();
            if (EnchantmentHelper.getLevel(ModEnchantments.BLAST_ENCHANT, tool) <= 0) return true;
            if (!tool.isSuitableFor(state)) return true;

            // If you want Silk Touch to skip smelting, uncomment:
            // if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) > 0) return true;

            // Compute vanilla drops first (respects Fortune/Silk Touch/etc.)
            List<ItemStack> originalDrops = Block.getDroppedStacks(state, serverWorld, pos, blockEntity, player, tool);

            // Break block WITHOUT vanilla drops
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
            boolean removed = serverWorld.breakBlock(pos, false, player); // false => no drops
            if (!removed) return true;

            DynamicRegistryManager drm = serverWorld.getRegistryManager();

            List<ItemStack> finalDrops = new ArrayList<>();
            float totalXp = 0f;

            for (ItemStack drop : originalDrops) {
                if (drop.isEmpty()) continue;

                // Try to find a BLASTING recipe for ONE of this item
                ItemStack one = drop.copy();
                one.setCount(1);

                Optional<BlastingRecipe> match = serverWorld.getRecipeManager()
                        .getFirstMatch(RecipeType.BLASTING, new SimpleInventory(one), serverWorld);

                if (match.isPresent()) {
                    BlastingRecipe recipe = match.get();

                    // 1.20.x Yarn: getOutput(drm); (on 1.20.5+ this became getResult(drm))
                    ItemStack resultPerOne = recipe.getOutput(drm).copy(); // swap to recipe.getResult(drm) if your mappings require
                    if (!resultPerOne.isEmpty()) {
                        int totalCount = resultPerOne.getCount() * drop.getCount();
                        ItemStack out = resultPerOne.copy();
                        out.setCount(totalCount);
                        finalDrops.add(out);

                        totalXp += recipe.getExperience() * drop.getCount();
                        continue;
                    }
                }

                // No blasting recipe -> keep original drop
                finalDrops.add(drop.copy());
            }

            // Spawn our items and XP
            for (ItemStack out : finalDrops) {
                if (out.isEmpty()) continue;
                ItemEntity entity = new ItemEntity(serverWorld,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        out);
                entity.setToDefaultPickupDelay();
                serverWorld.spawnEntity(entity);
            }

            if (totalXp > 0f) {
                ExperienceOrbEntity.spawn(serverWorld, pos.toCenterPos(), Math.round(totalXp));
            }

            // Cancel vanilla (we handled drops)
            return false;
        });
    }
}
