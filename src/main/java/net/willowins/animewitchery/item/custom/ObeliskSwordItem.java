package net.willowins.animewitchery.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.willowins.animewitchery.item.renderer.ObeliskSwordRenderer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObeliskSwordItem extends SwordItem implements GeoItem {
    private static final double PHASE_RANGE = 20.0;      // max blink distance (blocks)
    private static final int COOLDOWN_TICKS = 20 * 8;   // 8s cooldown

    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public ObeliskSwordItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle_controller", 0,
                state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));

        controllers.add(new AnimationController<>(this, "swing_controller", state -> PlayState.STOP)
                .triggerableAnim("Swing", RawAnimation.begin().thenPlay("Swing")));
    }

    /** Call this on the SERVER to sync the swing animation to all clients. */
    public void playSwing(ServerPlayerEntity player, ItemStack stack) {
        ServerWorld server = (ServerWorld) player.getWorld();
        long id = GeoItem.getOrAssignId(stack, server);
        this.triggerAnim(player, id, "swing_controller", "Swing");
    }

    /** Right-click to blink forward. */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            ServerPlayerEntity sp = (ServerPlayerEntity) user;

            if (sp.getItemCooldownManager().isCoolingDown(this))
                return TypedActionResult.pass(stack);

            Vec3d dest = findBlinkDestinationViaRaycast((ServerWorld) world, sp, PHASE_RANGE);
            if (dest != null && dest.squaredDistanceTo(sp.getPos()) > 0.01) {
                // FX at origin
                ((ServerWorld) world).spawnParticles(ParticleTypes.PORTAL,
                        sp.getX(), sp.getBodyY(0.5), sp.getZ(), 24, 0.5, 0.5, 0.5, 0.15);
                world.playSound(null, sp.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                        SoundCategory.PLAYERS, 1.0f, 1.0f);

                // Teleport
                sp.teleport((ServerWorld) world, dest.x, dest.y, dest.z, sp.getYaw(), sp.getPitch());

                // FX at destination
                ((ServerWorld) world).spawnParticles(ParticleTypes.PORTAL,
                        dest.x, dest.y + sp.getHeight() * 0.5, dest.z, 24, 0.5, 0.5, 0.5, 0.15);
                world.playSound(null, sp.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                        SoundCategory.PLAYERS, 1.0f, 1.2f);

                playSwing(sp, stack);
                sp.getItemCooldownManager().set(this, COOLDOWN_TICKS);
            }
        }

        user.swingHand(hand, true);
        return TypedActionResult.success(stack, world.isClient);
    }

    private static final double SAFETY = 0.35;     // step back from hit face
    private static final double BACKOFF_STEP = 0.25;
    private static final int MAX_BACKOFF_STEPS = 24; // up to ~6 blocks

    private static Vec3d findBlinkDestinationViaRaycast(ServerWorld world, PlayerEntity p, double maxRange) {
        // Eye → end
        Vec3d eye = p.getCameraPosVec(1.0f);
        Vec3d look = p.getRotationVec(1.0f).normalize();
        Vec3d end = eye.add(look.multiply(maxRange));

        // Block raycast (collider shapes; ignore fluids)
        RaycastContext ctx = new RaycastContext(
                eye, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, p);
        BlockHitResult hr = world.raycast(ctx);

        Vec3d hitPoint = (hr.getType() == HitResult.Type.MISS)
                ? end
                : hr.getPos().subtract(look.multiply(SAFETY));

        // Convert eye → feet
        double eyeToFeet = p.getEyeHeight(p.getPose());
        Vec3d targetFeet = new Vec3d(hitPoint.x, hitPoint.y - eyeToFeet, hitPoint.z);

        // Ensure space for full BB; if not, back off along the ray until it fits
        return backoffToClearance(world, p, targetFeet, look);
    }

    private static Vec3d backoffToClearance(ServerWorld world, PlayerEntity p, Vec3d feet, Vec3d lookDir) {
        // Clamp Y within world bounds
        int minY = world.getBottomY();
        int maxY = world.getTopY() - 1;

        Vec3d dir = lookDir.normalize();
        Vec3d candidate = new Vec3d(feet.x, Math.max(minY, Math.min(maxY, feet.y)), feet.z);

        for (int i = 0; i <= MAX_BACKOFF_STEPS; i++) {
            Vec3d delta = candidate.subtract(p.getPos());
            Box targetBox = p.getBoundingBox().offset(delta);

            if (world.isSpaceEmpty(p, targetBox))
                return candidate;

            // step backwards slightly toward the player
            candidate = candidate.subtract(dir.multiply(BACKOFF_STEP));
            candidate = new Vec3d(candidate.x, Math.max(minY, Math.min(maxY, candidate.y)), candidate.z);
        }
        return null; // no safe spot found
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableCache;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private ObeliskSwordRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (renderer == null) renderer = new ObeliskSwordRenderer();
                return renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // Title
        tooltip.add(Text.literal("Phase Step")
                .formatted(Formatting.DARK_PURPLE, Formatting.BOLD));
    
        // Summary or details (Shift)
        if (net.minecraft.client.gui.screen.Screen.hasShiftDown()) {
            tooltip.add(Text.literal("Right-click: Blink forward up to 20 blocks.")
                    .formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Stops at obstacles; won't place you inside blocks.")
                    .formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Cooldown: 8s")
                    .formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.literal("Right-click: Short-range blink.")
                    .formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Hold \u21E7 Shift for details")
                    .formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        }
    
        // Advanced (F3+H)
        if (context.isAdvanced()) {
            tooltip.add(Text.literal(String.format("Range %.1f | Backoff %.2f | Steps %d",
                    PHASE_RANGE, BACKOFF_STEP, MAX_BACKOFF_STEPS))
                    .formatted(Formatting.DARK_GRAY));
        }
    
        super.appendTooltip(stack, world, tooltip, context);
    }
    
}
