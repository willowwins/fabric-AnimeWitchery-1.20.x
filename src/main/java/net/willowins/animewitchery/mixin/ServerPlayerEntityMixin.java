package net.willowins.animewitchery.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.willowins.animewitchery.util.IKeepInventoryCharmUser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "copyFrom", at = @At("HEAD"))
    private void copyInventoryFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        System.out.println("ANIMEWITCHERY DEBUG: copyFrom called. Alive: " + alive);
        if (oldPlayer instanceof IKeepInventoryCharmUser charmUser) {
            boolean shouldKeep = charmUser.shouldKeepInventory();
            System.out.println("ANIMEWITCHERY DEBUG: Old player has interface. shouldKeepInventory: " + shouldKeep);

            if (shouldKeep) {
                ServerPlayerEntity newPlayer = (ServerPlayerEntity) (Object) this;

                // Copy main inventory
                newPlayer.getInventory().clone(oldPlayer.getInventory());
                System.out.println("ANIMEWITCHERY DEBUG: Inventory cloned to new player.");

                // Optionally handle XP
                newPlayer.experienceLevel = oldPlayer.experienceLevel;
                newPlayer.experienceProgress = oldPlayer.experienceProgress;
                newPlayer.totalExperience = oldPlayer.totalExperience;
                newPlayer.setScore(oldPlayer.getScore());
            }
        } else {
            System.out.println(
                    "ANIMEWITCHERY DEBUG: Old player does NOT implement IKeepInventoryCharmUser (Mixin failed?)");
        }
    }
}
