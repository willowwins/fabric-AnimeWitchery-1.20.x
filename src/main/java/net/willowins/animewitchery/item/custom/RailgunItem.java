package net.willowins.animewitchery.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.renderer.RailgunRenderer;
import net.willowins.animewitchery.networking.ModPackets;
import net.willowins.animewitchery.particle.ModParticles;
import net.willowins.animewitchery.sound.ModSounds;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class RailgunItem extends Item implements GeoItem {
    AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
    public RailgunItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.LASER_CHARGE, SoundCategory.PLAYERS, 1, 1);
        }
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.getOrCreateNbt().putFloat("charge", 0.0f);
        for (PlayerEntity player : world.getPlayers()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(ModSounds.LASER_CHARGE.getId(), SoundCategory.PLAYERS);
                serverPlayer.networkHandler.sendPacket(stopSoundS2CPacket);
            }
        }
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (entity instanceof PlayerEntity player) {
            if (!world.isClient) {
                if (player.getMainHandStack().isOf(this) && player.isUsingItem()) {
                    int i = this.getMaxUseTime(stack) - player.getItemUseTimeLeft();
                    player.sendMessage(Text.of(String.valueOf(getPullProgress(i))), true);
                    stack.getOrCreateNbt().putFloat("charge", getPullProgress(i));

                    if (stack.getOrCreateNbt().getFloat("charge") == 1.0f) {
                        player.getItemCooldownManager().set(this, 30);
                        shootLaser(player.getRotationVector(), world, player.getPos().add(0,1,0), player);
                        player.stopUsingItem();
                    }
                    if (stack.getOrCreateNbt().getFloat("charge") > 0.0f) {
                         for (PlayerEntity playerEntity : world.getPlayers()) {
                             if (playerEntity instanceof ServerPlayerEntity serverPlayer) {
                                 ServerPlayNetworking.send(serverPlayer, ModPackets.LASER_CHARGE, new PacketByteBuf(PacketByteBufs
                                         .create()
                                         .writeDouble((player.getX() + (1.25) * player.getRotationVector().x))
                                         .writeDouble((player.getY() + 1.5 + (1.25) * player.getRotationVector().y))
                                         .writeDouble((player.getZ() + (1.25) * player.getRotationVector().z))
                                         .writeFloat(stack.getOrCreateNbt().getFloat("charge"))
                                 ));

                             }
                         }
                        for (int z = 0; z < 100; z++) {
                            Vec3d pos = player.getPos();
                            Vec3d look = player.getRotationVector();
                            glowEntities(world, 1.5, new BlockPos((int) (pos.getX() + (z*look.x)), (int) (pos.getY() + (z*look.y)), (int) (pos.getZ() + (z*look.z))), player);
                            if (!world.getBlockState(new BlockPos((int) (pos.getX() + (z*look.x)), (int) (pos.getY() + (z*look.y)), (int) (pos.getZ() + (z*look.z)))).isOf(Blocks.AIR)) {
                                break;
                            }
                        }
                    }
                } else {
                    stack.getOrCreateNbt().putFloat("charge", 0.0f);
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void shootLaser(Vec3d look, World world, Vec3d pos, PlayerEntity owner) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, owner.getX(), owner.getY(), owner.getZ(), ModSounds.LASER_SHOOT, SoundCategory.PLAYERS, 1, 1);
        }
        if (world instanceof  ServerWorld serverWorld) {
            for (int i = 1; i <= 3; i++) {
                serverWorld.spawnParticles(ModParticles.LASER_PARTICLE,
                        (pos.getX() + (4 * i * look.x)),
                        (pos.getY() + (4 * i * look.y)),
                        (pos.getZ() + (4 * i * look.z)),
                        1,
                        0, 0, 0, 0
                );
            }
        }
        for (int i = 0; i < 100; i++) {
            findEntities(world, 1.5, new BlockPos((int) (pos.getX() + (i*look.x)), (int) (pos.getY() + (i*look.y)), (int) (pos.getZ() + (i*look.z))), owner);
            if (!world.getBlockState(new BlockPos((int) (pos.getX() + (i*look.x)), (int) (pos.getY() + (i*look.y)), (int) (pos.getZ() + (i*look.z)))).isOf(Blocks.AIR)) {
                for (PlayerEntity playerEntity : world.getPlayers()) {
                    if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
                        ServerPlayNetworking.send(serverPlayerEntity, ModPackets.LASER_HIT, new PacketByteBuf(PacketByteBufs
                                .create()
                                .writeDouble((pos.getX() + (i*look.x - 2*look.x)))
                                .writeDouble((pos.getY() + (i*look.y - 2*look.y)))
                                .writeDouble((pos.getZ() + (i*look.z - 2*look.z)))
                        ));
                    }
                }
                break;
            }
        }
        for (PlayerEntity player : world.getPlayers()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                for (int i = 10; i < 1000; i++) {
                    ServerPlayNetworking.send(serverPlayer, ModPackets.LASER_BEAM, new PacketByteBuf(PacketByteBufs
                            .create()
                            .writeDouble((pos.getX() + (((double) i / 10) * look.x)))
                            .writeDouble((pos.getY() + (((double) i / 10) * look.y)))
                            .writeDouble((pos.getZ() + (((double) i / 10) * look.z)))
                    ));
                    world.addParticle(ParticleTypes.CLOUD, (pos.getX() + (((double) i / 10) * look.x)), (pos.getY() + (((double) i / 10) * look.y)), (pos.getZ() + (((double) i / 10) * look.z)), 0, 0, 0);
                    if (!world.getBlockState(new BlockPos((int) (pos.getX() + (((double) i /10)*look.x)), (int) (pos.getY() + (((double) i /10)*look.y)), (int) (pos.getZ() + (((double) i /10)*look.z)))).isOf(Blocks.AIR)) {

                        break;
                    }
                }
            }
        }
    }

    private void findEntities(World world, double radius, BlockPos pos, PlayerEntity owner) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Box box = new Box(
                pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius
        );

        List<LivingEntity> player = serverWorld.getEntitiesByClass(LivingEntity.class, box, entity -> true);


        for (LivingEntity target : player) {
            if (target != owner) {
                target.kill();
            }
        }


    }

    private void glowEntities(World world, double radius, BlockPos pos, PlayerEntity owner) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Box box = new Box(
                pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius
        );

        List<LivingEntity> player = serverWorld.getEntitiesByClass(LivingEntity.class, box, entity -> true);


        for (LivingEntity target : player) {
            if (target != owner) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 0));
            }
        }


    }

    private static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 50;
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
        return UseAction.NONE;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private RailgunRenderer renderer;

            public BuiltinModelItemRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new RailgunRenderer();
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
