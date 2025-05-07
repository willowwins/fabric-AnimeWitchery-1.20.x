package net.willowins.animewitchery.item.custom;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.item.renderer.HealingStaffRenderer;
import net.willowins.animewitchery.sound.ModSounds;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HealingStaff extends Item implements GeoItem {
    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);


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
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = getPullProgress(i, stack);
        if (user instanceof PlayerEntity player) {
            if (f == 1.0f) {
                asNearbyPlayers(world, 20, user.getBlockPos(), player);
                world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_AMETHYST_CLUSTER_FALL, SoundCategory.PLAYERS, 1.0f, 1.0f);
                for (int z = 0; z < 100; z++) {
                    world.addParticle(ParticleTypes.END_ROD, player.getPos().x, player.getPos().y+0.9, player.getPos().z, 1*Math.random()-1*Math.random(),1*Math.random()-1*Math.random(),1*Math.random()-1*Math.random());
                }
                player.getItemCooldownManager().set(ModItems.HEALING_STAFF, 100);
            }
        }
            super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }


    private static float getPullProgress(int useTicks, ItemStack stack) {
        float f = (float)useTicks / 25;
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
        if (!(world instanceof ServerWorld serverWorld)) return;

        Box box = new Box(
                pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius
        );

        List<PlayerEntity> player = serverWorld.getEntitiesByClass(PlayerEntity.class, box, entity -> true);
        List<HostileEntity> mobs = serverWorld.getEntitiesByClass(HostileEntity.class, box, entity -> true);


        for (PlayerEntity target : player) {
            if (target != user) {
                if (hasEquipped(target, ModItems.SILVER_PENDANT)) {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 80, 1));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 80, 1));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 80, 1));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 80, 0));
                } else {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 80, 1));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, 1));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 80, 1));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 80, 0));

                }
            }
        }

        for (MobEntity entity : mobs) {
            if (entity.isUndead()){
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 80, 255));

            } else {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 80, 255));
            }
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, 255));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 80, 0));

        }


    }

    public boolean hasEquipped(PlayerEntity player, Item item) {
        Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(player);

        if (trinketComponent.isPresent()) {
            return trinketComponent.get().isEquipped(stack -> stack.isOf(item));
        }

        return false;
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
        controllerRegistrar.add(new AnimationController<>(this, "Idle", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
