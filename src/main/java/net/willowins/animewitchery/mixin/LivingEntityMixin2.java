package net.willowins.animewitchery.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin2 {

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void tryUseCustomTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object)this instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) (Object) this;

        World world = player.getWorld();

        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getStackInHand(hand);

            if (stack.getItem() == ModItems.MOD_TOTEM) {
                stack.decrement(1);
                player.setHealth(1.0F);
                player.clearStatusEffects();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                world.sendEntityStatus(player, (byte) 35);

                // Play sound and particles
                if (!world.isClient()) {
                    player.playSound(SoundEvents.ITEM_TOTEM_USE, 1.0F, 1.0F);
                }

                // Poison the attacker
                Entity attacker = source.getAttacker();
                if (attacker instanceof LivingEntity livingAttacker) {
                    livingAttacker.addStatusEffect(new StatusEffectInstance(ModEffect.MARKED, 100, 0));
                }

                // Shockwave effect: teleport all other players without poison
                if (!world.isClient) {
                    for (PlayerEntity otherPlayer : world.getPlayers()) {
                        if (otherPlayer == player) continue;

                        if (otherPlayer.hasStatusEffect(ModEffect.MARKED)) continue;

                        if (otherPlayer instanceof ServerPlayerEntity serverPlayer) {
                            if (!serverPlayer.isAlive() || serverPlayer.networkHandler == null) continue;

                            BlockPos spawnPos = serverPlayer.getSpawnPointPosition();
                            RegistryKey<World> spawnWorld = serverPlayer.getSpawnPointDimension();

                            if (!serverPlayer.isAlive()) return;
                            if (spawnPos != null && spawnWorld != null) {
                                ServerWorld dimension = serverPlayer.getServer().getWorld(spawnWorld);
                                if (dimension != null) {
                                    serverPlayer.teleport(dimension,
                                            spawnPos.getX() + 0.5,
                                            spawnPos.getY(),
                                            spawnPos.getZ() + 0.5,
                                            serverPlayer.getYaw(),
                                            serverPlayer.getPitch());
                                }
                            }
                        }
                    }
                }

                cir.setReturnValue(true);
                return;
            }
        }
    }}