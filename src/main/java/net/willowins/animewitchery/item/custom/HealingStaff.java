package net.willowins.animewitchery.item.custom;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.util.Hand;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.item.renderer.HealingStaffRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HealingStaff extends Item implements GeoItem {
    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    private static final int USE_MANA_COST = 100;

    public HealingStaff(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity entity, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = getPullProgress(i, stack);

        if (entity instanceof PlayerEntity player) {
            if (f == 1.0f) {
                // Try to pay the cost using total mana (player + catalysts)
                if (!tryConsumeFromPlayerAndCatalysts(player, USE_MANA_COST)) {
                    player.sendMessage(
                            Text.literal("You need " + USE_MANA_COST + " mana (or catalysts) to use the staff")
                                    .formatted(net.minecraft.util.Formatting.RED),
                            true);
                    return;
                }

                // Perform effect
                asNearbyPlayers(world, 20, entity.getBlockPos(), player);

                world.playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                        SoundCategory.PLAYERS, 1.0f, 1.0f);
                world.playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_AMETHYST_CLUSTER_FALL,
                        SoundCategory.PLAYERS, 1.0f, 1.0f);

                for (int z = 0; z < 100; z++) {
                    world.addParticle(ParticleTypes.END_ROD,
                            player.getPos().x, player.getPos().y + 0.9, player.getPos().z,
                            Math.random() - Math.random(),
                            Math.random() - Math.random(),
                            Math.random() - Math.random());
                }

                player.getItemCooldownManager().set(ModItems.HEALING_STAFF, 100);
            }
        }

        super.onStoppedUsing(stack, world, entity, remainingUseTicks);
    }

    private static float getPullProgress(int useTicks, ItemStack stack) {
        float f = (float) useTicks / 10f;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    private void asNearbyPlayers(World world, double radius, BlockPos pos, PlayerEntity user) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        Box box = new Box(
                pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius);

        List<PlayerEntity> players = serverWorld.getEntitiesByClass(PlayerEntity.class, box, ent -> true);
        List<HostileEntity> mobs = serverWorld.getEntitiesByClass(HostileEntity.class, box, ent -> true);

        for (PlayerEntity target : players) {
            if (target != user) {
                if (hasEquipped(target, ModItems.SILVER_PENDANT)) {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 120, 1));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 120, 1));
                } else {
                    target.addStatusEffect(new StatusEffectInstance(ModEffect.MARKED, 100, 0));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 100, 1));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0));
                }
            }
        }

        for (MobEntity entity : mobs) {
            if (entity.isUndead()) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 120, 255));
            } else {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 120, 255));
            }
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20, 255));
            entity.addStatusEffect(new StatusEffectInstance(ModEffect.MARKED, 120, 255));
            entity.addStatusEffect(new StatusEffectInstance(ModEffect.BOUND, 120, 255));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 120, 0));
        }
    }

    public boolean hasEquipped(PlayerEntity player, Item item) {
        Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(player);
        if (trinketComponent.isPresent()) {
            return trinketComponent.get().isEquipped(stack -> stack.isOf(item));
        }
        return false;
    }

    // ==== New helper logic: attempt consume from player then catalysts ====
    private boolean tryConsumeFromPlayerAndCatalysts(PlayerEntity player, int cost) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        int playerMana = mana.getMana();
        if (playerMana >= cost) {
            mana.consume(cost);
            return true;
        }

        int remaining = cost - playerMana;
        if (playerMana > 0) {
            mana.consume(playerMana);
        }

        // Drain from catalysts in inventory
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof AlchemicalCatalystItem) {
                int stored = AlchemicalCatalystItem.getStoredMana(stack);
                if (stored <= 0)
                    continue;
                int take = Math.min(stored, remaining);
                AlchemicalCatalystItem.setStoredMana(stack, stored - take);
                remaining -= take;
                if (remaining <= 0) {
                    break;
                }
            }
        }

        return (remaining <= 0);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private HealingStaffRenderer renderer;

            public BuiltinModelItemRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new HealingStaffRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Idle", 0,
                state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
