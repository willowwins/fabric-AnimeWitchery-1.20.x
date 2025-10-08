package net.willowins.animewitchery.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.custom.ManaRocketItem;
import net.willowins.animewitchery.mana.ManaHelper;
import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.mana.IManaComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    private static final double BEAM_LENGTH = 80.0;
    private static final double AOE_RADIUS = 6.0;
    private static final int MANA_COST = 5000;
    private static final float BASE_DAMAGE = 30.0f;

    @Inject(method = "getProjectiles", at = @At("RETURN"), cancellable = true)
    private void animewitchery$allowManaRocket(CallbackInfoReturnable<Predicate<ItemStack>> cir) {
        Predicate<ItemStack> original = cir.getReturnValue();
        cir.setReturnValue(stack -> original.test(stack) || stack.getItem() instanceof ManaRocketItem);
    }

    @Inject(method = "loadProjectiles", at = @At("HEAD"), cancellable = true)
    private static void animewitchery$loadFromPresenceOnly(LivingEntity shooter, ItemStack crossbow, CallbackInfoReturnable<Boolean> cir) {
        if (!(shooter instanceof PlayerEntity player)) return;

        // Check if any Mana Rocket exists in inventory/offhand
        ItemStack found = ItemStack.EMPTY;
        if (player.getOffHandStack().getItem() instanceof ManaRocketItem) {
            found = player.getOffHandStack();
        } else {
            for (ItemStack s : player.getInventory().main) {
                if (s.getItem() instanceof ManaRocketItem) {
                    found = s;
                    break;
                }
            }
        }

        if (found.isEmpty()) {
            return;  // no rocket — let vanilla do normal loading
        }

        // Try draining mana from all sources
        if (!ManaHelper.consumeCostFromPlayerAndCatalysts(player, MANA_COST)) {
            if (!player.getWorld().isClient) {
                player.sendMessage(Text.literal("⚠ Not enough mana to arm the rocket!"), true);
                player.getWorld().playSound(null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.7f, 0.8f);
            }
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        // Build a “charged projectile” entry without removing from inventory
        NbtList charged = new NbtList();
        ItemStack one = found.copy();
        one.setCount(1);
        NbtCompound tag = new NbtCompound();
        one.writeNbt(tag);
        charged.add(tag);

        crossbow.getOrCreateNbt().put("ChargedProjectiles", charged);
        CrossbowItem.setCharged(crossbow, true);

        cir.setReturnValue(true);
        cir.cancel();
    }

    @Inject(method = "shootAll", at = @At("HEAD"), cancellable = true)
    private static void animewitchery$shootManaRocket(World world, LivingEntity shooter, Hand hand,
                                                      ItemStack crossbow, float speed, float divergence, CallbackInfo ci) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        boolean hasRocket = false;
        List<ItemStack> charged = new ArrayList<>();
        NbtCompound nbt = crossbow.getNbt();
        if (nbt != null && nbt.contains("ChargedProjectiles", 9)) {
            NbtList list = nbt.getList("ChargedProjectiles", 10);
            for (int i = 0; i < list.size(); i++) {
                ItemStack s = ItemStack.fromNbt(list.getCompound(i));
                charged.add(s);
                if (s.getItem() instanceof ManaRocketItem) hasRocket = true;
            }
        }
        if (!hasRocket) return;

        ci.cancel();

        Vec3d origin = shooter.getEyePos();
        Vec3d look   = shooter.getRotationVector().normalize();
        Vec3d end    = origin.add(look.multiply(BEAM_LENGTH));

        serverWorld.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 3.0f, 1.0f);
        int particleCount = (int) (BEAM_LENGTH * 2.5);
        for (int i = 0; i < particleCount; i++) {
            double t = i / 2.5;
            Vec3d pos = origin.add(look.multiply(t));
            serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        }

        var hit = world.raycast(new RaycastContext(
                origin, end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                shooter
        ));

        Vec3d impact = (hit.getType() == HitResult.Type.MISS) ? end : hit.getPos();

        LivingEntity directTarget = null;
        List<LivingEntity> potential = serverWorld.getEntitiesByClass(
                LivingEntity.class,
                new Box(origin, end).expand(1.0),
                e -> e != shooter && e.isAlive()
        );

        double minDist = Double.MAX_VALUE;
        for (LivingEntity e : potential) {
            double d = origin.distanceTo(e.getPos());
            if (d < minDist && d <= BEAM_LENGTH) {
                minDist = d;
                directTarget = e;
            }
        }

        if (directTarget != null) {
            directTarget.damage(serverWorld.getDamageSources().sonicBoom(shooter), BASE_DAMAGE);
            impact = directTarget.getPos();
        }

        List<LivingEntity> nearby = serverWorld.getEntitiesByClass(
                LivingEntity.class,
                new Box(impact.subtract(AOE_RADIUS, AOE_RADIUS, AOE_RADIUS),
                        impact.add(AOE_RADIUS, AOE_RADIUS, AOE_RADIUS)),
                e -> e != shooter && e.isAlive()
        );

        for (LivingEntity target : nearby) {
            target.damage(serverWorld.getDamageSources().sonicBoom(shooter), BASE_DAMAGE);
            Vec3d push = target.getPos().subtract(impact).normalize().multiply(1.5);
            target.addVelocity(push.x, 0.6, push.z);
            target.velocityModified = true;

            serverWorld.spawnParticles(
                    ParticleTypes.EXPLOSION,
                    target.getX(), target.getY() + 1, target.getZ(),
                    6, 0.2, 0.2, 0.2, 0.05);
        }

        // Decrement one “charge” from the NBT (not the inventory item)
        boolean removed = false;
        NbtList rebuilt = new NbtList();
        for (ItemStack s : charged) {
            if (!removed && s.getItem() instanceof ManaRocketItem) {
                removed = true;
                continue;
            }
            NbtCompound t = new NbtCompound();
            s.writeNbt(t);
            rebuilt.add(t);
        }
        crossbow.getOrCreateNbt().put("ChargedProjectiles", rebuilt);
        CrossbowItem.setCharged(crossbow, !rebuilt.isEmpty());
    }
}
