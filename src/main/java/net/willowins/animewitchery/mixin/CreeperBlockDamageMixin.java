package net.willowins.animewitchery.mixin;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.World;
import net.willowins.animewitchery.util.ModGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreeperEntity.class)
public class CreeperBlockDamageMixin {
    
    /**
     * Intercept the creeper explosion's block destruction mode.
     * If the gamerule is false, prevent block damage but still damage entities.
     */
    @Redirect(
        method = "explode",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"
        )
    )
    private net.minecraft.world.explosion.Explosion redirectExplosion(
            World world,
            net.minecraft.entity.Entity entity,
            double x,
            double y,
            double z,
            float power,
            World.ExplosionSourceType explosionSourceType) {
        
        // Check the gamerule
        boolean allowBlockDamage = world.getGameRules().getBoolean(ModGameRules.DO_CREEPER_BLOCK_DAMAGE);
        
        // If block damage is disabled, create explosion with damage entities but no blocks
        // NONE still damages entities, it just doesn't break blocks or cause fire
        if (!allowBlockDamage) {
            // Create explosion that damages entities but doesn't destroy blocks
            return world.createExplosion(entity, x, y, z, power, false, World.ExplosionSourceType.NONE);
        }
        
        // Otherwise use the original mode (usually MOB which breaks blocks)
        return world.createExplosion(entity, x, y, z, power, explosionSourceType);
    }
}

