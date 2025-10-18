package net.willowins.animewitchery.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.willowins.animewitchery.item.custom.ResonantGreatSwordItem;
import net.willowins.animewitchery.particle.ModParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class GreatswordSweepingMixin {

    @ModifyVariable(
        method = "attack",
        at = @At(value = "STORE", ordinal = 0),
        ordinal = 4
    )
    private float addBonusToSweepingDamage(float sweepingDamage) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();
        
        if (stack.getItem() instanceof ResonantGreatSwordItem) {
            float charge = stack.getOrCreateNbt().getFloat("charge");
            
            if (charge > 0) {
                // Add bonus damage to sweeping attacks (same multiplier as main attack)
                float bonusDamage = charge * 2.0f; // DAMAGE_MULTIPLIER
                
                // Spawn particles for sweeping targets
                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ModParticles.LASER_PARTICLE,
                        player.getX(), player.getY() + 1, player.getZ(),
                        5, 1.5, 0.5, 1.5, 0.1);
                }
                
                return sweepingDamage + bonusDamage;
            }
        }
        
        return sweepingDamage;
    }
}

