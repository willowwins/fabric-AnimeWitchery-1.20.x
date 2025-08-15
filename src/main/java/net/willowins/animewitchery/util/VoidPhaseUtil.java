package net.willowins.animewitchery.util;

import net.minecraft.util.math.MathHelper;

public final class VoidPhaseUtil {
    private VoidPhaseUtil() {}

    // Timings in ticks (20 ticks = 1 second)
    public static final float RISE_TIME_T = 40f;   // 0 → 1   (~2.0s)
    public static final float HOLD_MAX_T  = 200f;  // hold 1  (~10s)
    public static final float FALL_TIME_T = 40f;   // 1 → 0   (~2.0s)
    public static final float HOLD_MIN_T  = 40f;   // hold 0  (~2.0s)

    /** Optional amplifier scaling — set to 1.0f to disable. */
    public static float amplifierScale(int amplifier) {
        // Example: each amplifier level stretches cycle by +10%
        return (float) Math.pow(1.10, amplifier);
    }

    /** Smoothstep easing for ramp segments (0..1 in, 0..1 out). */
    public static float ease(float x) {
        x = MathHelper.clamp(x, 0f, 1f);
        return x * x * (3f - 2f * x);
    }

    /**
     * Phase in [0..1] following: rise → hold(1) → fall → hold(0), with optional amplifier scaling.
     * @param ageTicks entity age in ticks
     * @param amplifier status effect amplifier (0-based)
     */
    public static float computePhase(long ageTicks, int amplifier) {
        float scale = amplifierScale(amplifier); // 1.0f if you don’t want scaling
        float rise  = RISE_TIME_T * scale;
        float hold1 = HOLD_MAX_T  * scale;
        float fall  = FALL_TIME_T * scale;
        float hold0 = HOLD_MIN_T  * scale;

        float cycle = rise + hold1 + fall + hold0;
        float t = (ageTicks % (long)cycle);

        if (t < rise) {
            return ease(t / rise);               // 0 → 1
        }
        t -= rise;

        if (t < hold1) {
            return 1f;                           // hold at 1
        }
        t -= hold1;

        if (t < fall) {
            return 1f - ease(t / fall);          // 1 → 0
        }
        return 0f;                                // hold at 0
    }

    /** Convenience threshold check for gameplay toggles. */
    public static boolean isInVoidWindow(float phase, float threshold) {
        return phase > threshold;
    }
}
