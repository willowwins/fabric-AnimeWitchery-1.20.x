package net.willowins.animewitchery.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.willowins.animewitchery.item.custom.KeepInventoryCharmItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Set;

@Mixin(LivingEntity.class)
public abstract class KeepInventoryCharmMixin {

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void shouldUseKeepInventoryCharm(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Only applies to players
        if (!(entity instanceof PlayerEntity player))
            return;

        if (player.getWorld().isClient)
            return;

        ItemStack charmStack = ItemStack.EMPTY;

        // Check slots for Charm
        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() instanceof KeepInventoryCharmItem) {
                charmStack = stack;
                break;
            }
        }

        // If not in hands, check inventory
        if (charmStack.isEmpty()) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (!stack.isEmpty() && stack.getItem() instanceof KeepInventoryCharmItem) {
                    charmStack = stack;
                    break;
                }
            }
        }

        if (!charmStack.isEmpty()) {
            // Consume Charm
            charmStack.decrement(1);

            // Full Heal
            player.setHealth(player.getMaxHealth());
            player.clearStatusEffects();
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.REGENERATION, 900, 1));
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.ABSORPTION, 100, 1));
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE, 800, 0));

            // Full Hunger
            player.getHungerManager().setFoodLevel(20);
            player.getHungerManager().setSaturationLevel(10.0f);

            // Teleport to Spawn
            if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                net.minecraft.util.math.BlockPos spawnPos = serverPlayer.getSpawnPointPosition();
                float spawnAngle = serverPlayer.getSpawnAngle();

                net.minecraft.registry.RegistryKey<net.minecraft.world.World> spawnDim = serverPlayer
                        .getSpawnPointDimension();

                if (spawnPos != null && spawnDim != null) {
                    net.minecraft.server.world.ServerWorld targetWorld = serverPlayer.getServer().getWorld(spawnDim);
                    if (targetWorld != null) {
                        player.teleport(targetWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                                Set.of(), spawnAngle, 0.0f);
                    }
                } else {
                    // Fallback to Overworld Spawn
                    net.minecraft.server.world.ServerWorld overworld = serverPlayer.getServer()
                            .getWorld(net.minecraft.world.World.OVERWORLD);
                    if (overworld != null) {
                        net.minecraft.util.math.BlockPos worldSpawn = overworld.getSpawnPos();
                        player.teleport(overworld, worldSpawn.getX() + 0.5, worldSpawn.getY(), worldSpawn.getZ() + 0.5,
                                Set.of(), 0.0f, 0.0f);
                    }
                }
            }

            // Play Totem Animation
            player.getWorld().sendEntityStatus(player, (byte) 35);

            System.out.println(
                    "ANIMEWITCHERY DEBUG: KeepInventoryCharm activated! Prevented death and teleported player: "
                            + player.getName().getString());
            cir.setReturnValue(true);
        }
    }
}
