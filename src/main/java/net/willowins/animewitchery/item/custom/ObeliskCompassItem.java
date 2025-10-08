package net.willowins.animewitchery.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.world.ObeliskRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObeliskCompassItem extends Item {

    private static final String ANGLE_KEY = "ObeliskAngle";
    private static final String TARGET_KEY = "HasTarget";
    private static final String DIM_KEY = "ObeliskDimension";
    private static final float SMOOTH_FACTOR = 0.15f;

    public ObeliskCompassItem(Settings settings) {
        super(settings);
    }

    /** Registers a client-side predicate for visual rotation */
    @Environment(EnvType.CLIENT)
    public static void ensureClientPredicateRegistered(Item item) {
        Identifier predicateId = new Identifier("angle");
        try {
            ModelPredicateProviderRegistry.get(item, predicateId);
        } catch (Exception e) {
            ModelPredicateProviderRegistry.register(item, predicateId, new ObeliskCompassAnglePredicateProvider());
        }
    }

    /** Updates the compass every tick if it has a target */
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player)) return;
        if (!world.isClient && world instanceof ServerWorld sw) {
            updateCompassAngle(stack, player, sw);
        }
    }

    private void updateCompassAngle(ItemStack stack, PlayerEntity player, ServerWorld world) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (!nbt.getBoolean(TARGET_KEY)) return;

        BlockPos target = new BlockPos(
                nbt.getInt("ObeliskX"),
                nbt.getInt("ObeliskY"),
                nbt.getInt("ObeliskZ")
        );

        double dx = target.getX() + 0.5 - player.getX();
        double dz = target.getZ() + 0.5 - player.getZ();

        // Horizontal-only bearing
        double rawAngle = Math.toDegrees(Math.atan2(dz, dx)) - player.getYaw() - 90.0;
        float newAngle = (float) ((rawAngle % 360 + 360) % 360);
        float oldAngle = nbt.contains(ANGLE_KEY) ? nbt.getFloat(ANGLE_KEY) : newAngle;
        nbt.putFloat(ANGLE_KEY, smoothAngle(oldAngle, newAngle, SMOOTH_FACTOR));
    }

    private float smoothAngle(float oldAngle, float newAngle, float factor) {
        float delta = (newAngle - oldAngle) % 360f;
        if (delta < -180f) delta += 360f;
        if (delta > 180f) delta -= 360f;
        return (oldAngle + delta * factor + 360f) % 360f;
    }

    /** Right-click triggers search in *current dimension only* */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack);

        if (world instanceof ServerWorld sw) {
            resetCompass(stack);
            user.sendMessage(Text.literal("🔮 Searching for Obelisk...").formatted(Formatting.GRAY), true);
            user.playSound(SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, 1.0F, 1.0F);

            BlockPos nearest = ObeliskRegistry.get(sw).findNearest(user.getBlockPos(), 20000);
            if (nearest != null) {
                setTarget(stack, nearest, sw.getRegistryKey().getValue());
                user.sendMessage(Text.literal("✨ Obelisk located!").formatted(Formatting.GREEN), true);
            } else {
                user.sendMessage(Text.literal("⚠️ No Obelisks registered in this dimension.").formatted(Formatting.RED), true);
            }
        }

        return TypedActionResult.success(stack);
    }

    /** Clears any stored search data */
    public static void resetCompass(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.remove("ObeliskX");
        nbt.remove("ObeliskY");
        nbt.remove("ObeliskZ");
        nbt.remove(DIM_KEY);
        nbt.putBoolean(TARGET_KEY, false);
    }

    /** Writes target data */
    public static void setTarget(ItemStack stack, BlockPos pos, Identifier dimension) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("ObeliskX", pos.getX());
        nbt.putInt("ObeliskY", pos.getY());
        nbt.putInt("ObeliskZ", pos.getZ());
        nbt.putString(DIM_KEY, dimension.toString());
        nbt.putBoolean(TARGET_KEY, true);
    }

    /** Tooltip display for direction and distance */
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!stack.hasNbt()) {
            tooltip.add(Text.literal("§7Inactive").formatted(Formatting.DARK_GRAY));
            return;
        }

        NbtCompound nbt = stack.getNbt();
        if (!nbt.getBoolean(TARGET_KEY)) {
            tooltip.add(Text.literal("§7No Obelisk locked").formatted(Formatting.GRAY));
            return;
        }

        tooltip.add(Text.literal(String.format("§bDirection: %.1f°", nbt.getFloat(ANGLE_KEY))).formatted(Formatting.AQUA));

        if (world != null && world.isClient) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                Vec3d pos = player.getPos();
                double dx = nbt.getInt("ObeliskX") + 0.5 - pos.x;
                double dy = nbt.getInt("ObeliskY") + 0.5 - pos.y;
                double dz = nbt.getInt("ObeliskZ") + 0.5 - pos.z;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                tooltip.add(Text.literal(String.format("§7Distance: %.1fm", dist)).formatted(Formatting.GRAY));
            }
        }
    }

    /** Data record for structured access */
    /** Retrieve target data stored in NBT (for model predicate use) */
    public static @Nullable TargetData getTarget(ItemStack stack) {
        if (!stack.hasNbt()) return null;

        NbtCompound nbt = stack.getNbt();
        if (!nbt.contains("ObeliskX") || !nbt.contains("ObeliskY") || !nbt.contains("ObeliskZ") || !nbt.contains(DIM_KEY))
            return null;

        BlockPos pos = new BlockPos(
                nbt.getInt("ObeliskX"),
                nbt.getInt("ObeliskY"),
                nbt.getInt("ObeliskZ")
        );

        Identifier dim = new Identifier(nbt.getString(DIM_KEY));
        return new TargetData(pos, dim);
    }

    /** Simple record for stored obelisk data */
    public record TargetData(BlockPos pos, Identifier dimension) {}

}
