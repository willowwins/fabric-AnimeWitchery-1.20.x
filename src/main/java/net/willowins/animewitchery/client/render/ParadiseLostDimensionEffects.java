package net.willowins.animewitchery.client.render;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class ParadiseLostDimensionEffects extends DimensionEffects {

    public ParadiseLostDimensionEffects() {
        super(Float.NaN, false, SkyType.NONE, false, false); // SkyType.NONE because hasSkylight=false
    }

    @Override
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        // Return original color (no override)
        return color;
    }

    @Override
    public boolean useThickFog(int camX, int camY) {
        return false; // No thick fog
    }

    @Nullable
    @Override
    public float[] getFogColorOverride(float skyAngle, float tickDelta) {
        return null; // Standard sky color blending
    }
}
