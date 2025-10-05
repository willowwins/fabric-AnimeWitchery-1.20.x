package net.willowins.animewitchery.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.item.renderer.RailgunRenderer;
import net.willowins.animewitchery.networking.ModPackets;
import net.willowins.animewitchery.particle.ModParticles;
import net.willowins.animewitchery.sound.ModSounds;
import org.joml.Vector3f;
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
    private static final int CHARGE_TIME = 60; // 3 seconds
    private static final int REQUIRED_MANA = 50000;
    private static final DustParticleEffect PURPLE_BEAM = new DustParticleEffect(new Vector3f(0.6f, 0.0f, 0.8f), 1.0f);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public RailgunItem(Settings settings) {
        super(settings);
    }

    // === Start charging ===
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // Already charged → fire
        if (isCharged(itemStack)) {
            if (!world.isClient) {
                fireLaser(world, user);
                return TypedActionResult.success(itemStack);
            }
            return TypedActionResult.consume(itemStack);
        }

        user.setCurrentHand(hand);
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(),
                    ModSounds.LASER_CHARGE, SoundCategory.PLAYERS, 1, 1);
        }
        return TypedActionResult.consume(itemStack);
    }

    // === Stop charging ===
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        // Stop charge sound
        for (PlayerEntity p : world.getPlayers()) {
            if (p instanceof ServerPlayerEntity sp)
                sp.networkHandler.sendPacket(new StopSoundS2CPacket(ModSounds.LASER_CHARGE.getId(), SoundCategory.PLAYERS));
        }

        int used = getMaxUseTime(stack) - remainingUseTicks;
        if (getPullProgress(used) >= 1.0f) {
            ItemStack offhand = player.getOffHandStack();
            if (offhand.isOf(ModItems.FUEL_ROD) &&
                    net.willowins.animewitchery.item.custom.FuelRodItem.getStoredMana(offhand) >= REQUIRED_MANA) {

                // Consume rod immediately after charging
                net.willowins.animewitchery.item.custom.FuelRodItem.setStoredMana(offhand, 0);
                offhand.decrement(1);

                // Mark charged
                stack.getOrCreateNbt().putBoolean("charged", true);
                if (!world.isClient)
                    player.sendMessage(Text.literal("🔋 Railgun charged — ready to fire."), true);

                player.getItemCooldownManager().set(this, 20);
            } else {
                if (!world.isClient)
                    player.sendMessage(Text.literal("❌ Fuel Rod requires 50,000 mana."), true);
            }
        }
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    // === Fire Laser ===
    private void fireLaser(World world, PlayerEntity player) {
        if (!isCharged(player.getMainHandStack())) return;

        ItemStack stack = player.getMainHandStack();
        stack.getOrCreateNbt().putBoolean("charged", false); // Reset

        Vec3d pos = player.getPos().add(0, 1, 0);
        Vec3d look = player.getRotationVector();

        // Recoil
        player.setVelocity(player.getRotationVector().multiply(-3, -3, -3));
        player.velocityModified = true;

        // Fire sound
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.LASER_SHOOT, SoundCategory.PLAYERS, 1.5f, 1.0f);

            // Beam core flash
            for (int i = 1; i <= 3; i++) {
                serverWorld.spawnParticles(ModParticles.LASER_PARTICLE,
                        pos.x + (4 * i * look.x),
                        pos.y + (4 * i * look.y),
                        pos.z + (4 * i * look.z),
                        5, 0, 0, 0, 0.02);
            }

            // Lodestone-style purple + reverse portal beam
            for (int i = 10; i < 1000; i++) {
                double bx = pos.x + (((double) i / 10) * look.x);
                double by = pos.y + (((double) i / 10) * look.y);
                double bz = pos.z + (((double) i / 10) * look.z);

                serverWorld.spawnParticles(PURPLE_BEAM, bx, by, bz, 1, 0, 0, 0, 0);
                serverWorld.spawnParticles(ParticleTypes.REVERSE_PORTAL, bx, by, bz, 2, 0, 0, 0, 0);

                if (!world.getBlockState(BlockPos.ofFloored(bx, by, bz)).isOf(Blocks.AIR)) break;
            }

            // Damage entities
            for (int i = 0; i < 100; i++) {
                Vec3d checkVec = pos.add(look.multiply(i));
                BlockPos checkPos = BlockPos.ofFloored(checkVec);
                if (!world.getBlockState(checkPos).isOf(Blocks.AIR)) break;
                findEntities(world, 1.5, checkPos, player);
                glowEntities(world, 1.5, checkPos, player);
            }

            // Overheated fuel rod
            player.giveItemStack(ModItems.OVERHEATED_FUEL_ROD.getDefaultStack());
            player.getItemCooldownManager().set(this, 40);
        }
    }

    // === Entity interaction ===
    private void findEntities(World world, double radius, BlockPos pos, PlayerEntity owner) {
        if (!(world instanceof ServerWorld sw)) return;
        Box box = new Box(pos).expand(radius);
        List<LivingEntity> targets = sw.getEntitiesByClass(LivingEntity.class, box, e -> e != owner);
        for (LivingEntity t : targets) {
            t.damage(t.getDamageSources().magic(), 50.0f);
            t.addStatusEffect(new StatusEffectInstance(ModEffect.MARKED, 200, 0));
        }
    }

    private void glowEntities(World world, double radius, BlockPos pos, PlayerEntity owner) {
        if (!(world instanceof ServerWorld sw)) return;
        Box box = new Box(pos).expand(radius);
        List<LivingEntity> targets = sw.getEntitiesByClass(LivingEntity.class, box, e -> e != owner);
        for (LivingEntity t : targets) {
            t.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 40, 0, false, false));
        }
    }

    // === Utility ===
    private static float getPullProgress(int useTicks) {
        return Math.min((float) useTicks / CHARGE_TIME, 1.0F);
    }

    private static boolean isCharged(ItemStack stack) {
        return stack.getOrCreateNbt().getBoolean("charged");
    }

    // === Vanilla ===
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    // === Renderer ===
    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private RailgunRenderer renderer;
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (renderer == null) renderer = new RailgunRenderer();
                return renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() { return renderProvider; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Idle", 0,
                state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
