package net.willowins.animewitchery.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.willowins.animewitchery.world.dimension.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ParadiseLostMixin {

    @Shadow
    public abstract void setWeather(int clearDuration, int rainDuration, boolean raining, boolean thundering);

    // 1. Force Weather (Always Snowing/Raining)
    @Inject(method = "tick", at = @At("TAIL"))
    private void forceParadiseWeather(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        if (world.getRegistryKey().equals(ModDimensions.PARADISELOSTDIM_LEVEL_KEY)) {
            // Aggressively force Weather (Always Snowing/Raining)
            // This decouples it from Overworld sync or random changes
            world.setWeather(0, 6000, true, false);
            world.setRainGradient(1.0F);
            world.setThunderGradient(0.0F);
        }
    }

    // 2. Reduce Snow Accumulation Rate (Slower Ground Snow)
    // Targeting ServerWorld.tickChunk's call to random.nextInt(16)
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private int slowSnowRate(Random instance, int bound) {
        ServerWorld world = (ServerWorld) (Object) this;
        if (world.getRegistryKey().equals(ModDimensions.PARADISELOSTDIM_LEVEL_KEY) && bound == 16) {
            // Default is 1/16. Let's make it 1/100 for "very slow".
            return instance.nextInt(200);
        }
        return instance.nextInt(bound);
    }

    // 3. No Hostile Mobs
    // Intercept spawnEntity to block Hostile entities
    @Inject(method = "spawnEntity", at = @At("HEAD"), cancellable = true)
    private void preventHostileSpawns(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerWorld world = (ServerWorld) (Object) this;
        if (world.getRegistryKey().equals(ModDimensions.PARADISELOSTDIM_LEVEL_KEY)) {
            if (entity instanceof HostileEntity) {
                cir.setReturnValue(false);
            }
        }
    }
}
