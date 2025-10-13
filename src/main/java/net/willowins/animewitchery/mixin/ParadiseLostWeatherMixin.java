package net.willowins.animewitchery.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ParadiseLostWeatherMixin {
    
    private int weatherCheckCounter = 0;
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void forceSnowInParadiseLost(CallbackInfo ci) {
        ServerWorld world = (ServerWorld) (Object) this;
        
        // Check if this is the Paradise Lost dimension
        Identifier dimensionId = world.getRegistryKey().getValue();
        if (dimensionId.toString().equals("animewitchery:paradiselostdim")) {
            // Check every 20 ticks (1 second) to reduce overhead
            weatherCheckCounter++;
            if (weatherCheckCounter >= 20) {
                weatherCheckCounter = 0;
                
                // Force it to be raining/snowing constantly
                if (!world.isRaining() || world.getRainGradient(1.0f) < 1.0f || world.getThunderGradient(1.0f) > 0.0f) {
                    // Set weather: clearWeatherTime=0, rainTime=999999, raining=true, thundering=false
                    world.setWeather(0, 999999, true, false);
                }
            }
        }
    }
}

