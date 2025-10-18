package net.willowins.animewitchery.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.willowins.animewitchery.world.dimension.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class PortalTeleportMixin {
    
    @Shadow
    public abstract World getWorld();
    
    @Shadow
    public abstract Vec3d getPos();
    
    @Shadow
    public abstract float getYaw();
    
    @Shadow
    public abstract float getPitch();
    
    @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
    private void onGetTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        Entity entity = (Entity) (Object) this;
        World sourceWorld = entity.getWorld();
        
        if (sourceWorld == null) return;
        
        RegistryKey<World> sourceDim = sourceWorld.getRegistryKey();
        RegistryKey<World> destDim = destination.getRegistryKey();
        
        // Only handle Paradise Lost <-> Overworld portals
        boolean isParadiseLostPortal = 
            (sourceDim == World.OVERWORLD && destDim == ModDimensions.PARADISELOSTDIM_LEVEL_KEY) ||
            (sourceDim == ModDimensions.PARADISELOSTDIM_LEVEL_KEY && destDim == World.OVERWORLD);
        
        if (!isParadiseLostPortal) {
            return; // Let other portal types work normally
        }
        
        // Debug output
        System.out.println("[PortalTeleportMixin] Intercepting teleport from " + sourceDim.getValue() + " to " + destDim.getValue());
        System.out.println("[PortalTeleportMixin] Entity position: " + entity.getPos());
        
        // Keep the exact same coordinates and orientation
        Vec3d pos = entity.getPos();
        float yaw = entity.getYaw();
        float pitch = entity.getPitch();
        
        // Create teleport target at the same position
        TeleportTarget target = new TeleportTarget(pos, Vec3d.ZERO, yaw, pitch);
        System.out.println("[PortalTeleportMixin] Teleporting to: " + pos);
        cir.setReturnValue(target);
    }
}
