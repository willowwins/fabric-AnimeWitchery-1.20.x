package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CopperWarhammerItem extends SwordItem {
    public CopperWarhammerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient) {
            ServerWorld world = (ServerWorld) attacker.getWorld();
            BlockPos targetPos = target.getBlockPos();

            // "when its trainin causes i lightning strike on hit"
            // Checking for rain and sky exposure (standard lightning requirement)
            if (world.isRaining() && world.isSkyVisible(targetPos)) {
                LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                if (lightning != null) {
                    lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
                    world.spawnEntity(lightning);
                }
            }
        }
        return super.postHit(stack, target, attacker);
    }
}
