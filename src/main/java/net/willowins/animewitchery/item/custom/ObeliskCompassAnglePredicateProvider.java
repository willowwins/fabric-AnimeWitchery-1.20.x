package net.willowins.animewitchery.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

@Environment(EnvType.CLIENT)
public class ObeliskCompassAnglePredicateProvider implements ClampedModelPredicateProvider {

    private final WeakHashMap<ItemStack, CachedAngle> cache = new WeakHashMap<>();

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (entity == null || world == null) return 0f;

        CachedAngle cached = cache.computeIfAbsent(stack, s -> new CachedAngle());
        long time = world.getTime();

        // Avoid reprocessing more than once per tick
        if (cached.lastTick == time) return (float) cached.angle;
        cached.lastTick = time;

        double angle; // final normalized [0,1)

        ObeliskCompassItem.TargetData targetData = ObeliskCompassItem.getTarget(stack);
        if (targetData != null && targetData.dimension().equals(world.getRegistryKey().getValue())) {
            // ===== Calculate true compass heading =====
            BlockPos target = targetData.pos();
            Vec3d playerPos = entity.getPos();

            double dx = target.getX() + 0.5 - playerPos.x;
            double dz = target.getZ() + 0.5 - playerPos.z;

            double targetAngle = Math.atan2(dz, dx); // radians
            double playerYaw = Math.toRadians(MathHelper.wrapDegrees(entity.getYaw()));

            // Difference between facing and target direction (north = 0)
            double diff = targetAngle - playerYaw - Math.PI / 2;

            // Normalize to 0–1 for JSON predicates (1 revolution)
            angle = MathHelper.floorMod(0.5 - diff / (2 * Math.PI), 1.0);
        } else {
            // No target or wrong dimension → spin slowly
            angle = MathHelper.floorMod(time / 100.0, 1.0);
        }

        // ===== Smooth interpolation (gentle motion) =====
        double delta = angle - cached.rotation;
        delta = MathHelper.floorMod(delta + 0.5, 1.0) - 0.5;
        cached.rotation += delta * 0.15; // smoothing factor
        cached.angle = cached.rotation;

        // Final clamp to [0,1)
        float result = (float) MathHelper.floorMod(cached.angle, 1.0);
        return result;
    }

    private static class CachedAngle {
        double rotation;
        double angle;
        long lastTick;
    }

    // ===== Registration Helper =====
    public static void registerModelPredicate(Item item) {
        ModelPredicateProviderRegistry.register(
                item,
                new Identifier("angle"),
                new ObeliskCompassAnglePredicateProvider()
        );
    }
}
