package net.willowins.animewitchery.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.willowins.animewitchery.fluid.ModFluids;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class StarlightItemEntityMixin extends Entity {

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    public abstract void setStack(ItemStack stack);

    @Shadow
    private UUID thrower;

    public StarlightItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void animeWitchery$tick(CallbackInfo ci) {
        if (this.getWorld().isClient)
            return;

        // Check if in Starlight fluid
        // We can check if the fluid at the entity's position is Starlight
        if (this.getWorld().getFluidState(this.getBlockPos()).getFluid() == ModFluids.STILL_STARLIGHT ||
                this.getWorld().getFluidState(this.getBlockPos()).getFluid() == ModFluids.FLOWING_STARLIGHT) {

            ItemStack stack = this.getStack();

            // Gold Nugget Logic
            if (stack.getItem() == Items.GOLD_NUGGET) {
                handleGoldNugget();
            }
            // Ancient Debris Logic
            else if (stack.getItem() == Items.ANCIENT_DEBRIS) {
                handleAncientDebris();
            }
            // Clock Logic
            else if (stack.getItem() == Items.CLOCK) {
                handleClock();
            }
        }
    }

    private void handleGoldNugget() {
        if (this.thrower != null) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            Entity throwerEntity = serverWorld.getEntity(this.thrower);

            if (throwerEntity instanceof LivingEntity livingThrower) {
                double positiveChance = 0.50; // 50% base chance for positive
                double negativeChance = 0.15; // 15% base chance for negative

                // Modify chances based on Luck/Unluck
                int luck = 0;
                int unluck = 0;

                if (livingThrower.hasStatusEffect(StatusEffects.LUCK)) {
                    luck = livingThrower.getStatusEffect(StatusEffects.LUCK).getAmplifier() + 1;
                }
                if (livingThrower.hasStatusEffect(StatusEffects.UNLUCK)) {
                    unluck = livingThrower.getStatusEffect(StatusEffects.UNLUCK).getAmplifier() + 1;
                }

                // Luck increases positive, decreases negative
                positiveChance += (0.05 * luck);
                negativeChance -= (0.05 * luck);

                // Unluck decreases positive, increases negative
                positiveChance -= (0.05 * unluck);
                negativeChance += (0.05 * unluck);

                // Clamp values
                positiveChance = Math.max(0, Math.min(1, positiveChance));
                negativeChance = Math.max(0, Math.min(1, negativeChance));

                double roll = this.random.nextDouble();

                if (roll < positiveChance) {
                    applyRandomPositiveEffect(livingThrower);
                    spawnSuccessParticles();
                } else if (roll < positiveChance + negativeChance) {
                    applyRandomNegativeEffect(livingThrower);
                    spawnFailParticles(); // Use fail particles for negative effect too? Or maybe distinct ones?
                                          // Sticking to fail/smoke for now or maybe something else?
                    // Let's use smoke for "bad thing happened", maybe dragon breath for negative?
                    // For now, let's just use smoke for negative as well, distinct from "nothing
                    // happened"?
                    // Actually, "nothing happened" is the rest of the probability.
                    // The request implies 15% for negative.
                    // Let's spawn smoke for negative as well to indicate "not good".
                } else {
                    // Nothing happens
                    spawnFailParticles();
                }
            }
        }

        // Consume item
        this.getStack().decrement(1);
        if (this.getStack().isEmpty()) {
            this.discard();
        }
    }

    private void applyRandomPositiveEffect(LivingEntity entity) {
        StatusEffect[] positiveEffects = {
                StatusEffects.SPEED,
                StatusEffects.HASTE,
                StatusEffects.STRENGTH,
                StatusEffects.REGENERATION,
                StatusEffects.RESISTANCE,
                StatusEffects.FIRE_RESISTANCE,
                StatusEffects.ABSORPTION,
                StatusEffects.SATURATION,
                StatusEffects.LUCK
        };

        StatusEffect effect = positiveEffects[this.random.nextInt(positiveEffects.length)];
        entity.addStatusEffect(new StatusEffectInstance(effect, 600, 0)); // 30 seconds

        this.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private void applyRandomNegativeEffect(LivingEntity entity) {
        StatusEffect[] negativeEffects = {
                StatusEffects.SLOWNESS,
                StatusEffects.MINING_FATIGUE,
                StatusEffects.NAUSEA,
                StatusEffects.BLINDNESS,
                StatusEffects.HUNGER,
                StatusEffects.WEAKNESS,
                StatusEffects.POISON,
                StatusEffects.WITHER
        };

        StatusEffect effect = negativeEffects[this.random.nextInt(negativeEffects.length)];
        entity.addStatusEffect(new StatusEffectInstance(effect, 300, 0)); // 15 seconds

        this.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ENTITY_WITCH_CELEBRATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private void handleAncientDebris() {
        // Transform into Haloic Scrap
        ItemStack newStack = new ItemStack(ModItems.HALOIC_SCRAP, this.getStack().getCount());
        this.setStack(newStack);

        // Effects
        spawnSuccessParticles();
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private void spawnSuccessParticles() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 0.5, this.getZ(), 10, 0.2, 0.2,
                    0.2, 0.1);
        }
    }

    private void handleClock() {
        // Transform into Weather Changer (WEATHERITEM)
        ItemStack newStack = new ItemStack(ModItems.WEATHERITEM, this.getStack().getCount());
        this.setStack(newStack);

        // Effects
        spawnSuccessParticles();
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private void spawnFailParticles() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 5, 0.1, 0.1,
                    0.1, 0.05);
        }
    }
}
