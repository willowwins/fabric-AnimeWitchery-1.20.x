package net.willowins.animewitchery.item.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.world.ObeliskRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ObeliskCompassItem extends Item {

    private static final String ANGLE_KEY = "ObeliskAngle";
    private static final String TARGET_KEY = "HasTarget";
    private static final String DIM_KEY = "ObeliskDimension";
    private static final String LOCKED_KEY = "LockedObelisk";
    private static final float SMOOTH_FACTOR = 0.15f;

    public ObeliskCompassItem(Settings settings) {
        super(settings);
    }

    /** Registers the model predicate for in-hand rotation */
    @Environment(EnvType.CLIENT)
    public static void ensureClientPredicateRegistered(Item item) {
        Identifier predicateId = new Identifier("angle");
        try {
            ModelPredicateProviderRegistry.get(item, predicateId);
        } catch (Exception e) {
            ModelPredicateProviderRegistry.register(item, predicateId, new ObeliskCompassAnglePredicateProvider());
        }
    }

    /** Updates the compass angle every tick (for HUD and held item rotation) */
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

    /** Handles right-click behavior: search for Obelisk or lock permanently */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack compass = user.getStackInHand(hand);
        ItemStack offhand = user.getOffHandStack();

        // --- Locking behavior with honeycomb in off-hand ---
        if (!world.isClient && offhand.isOf(Items.HONEYCOMB)) {
            NbtCompound nbt = compass.getOrCreateNbt();

            if (nbt.getBoolean(LOCKED_KEY)) {
                user.sendMessage(Text.literal("§eThis compass is already permanently bound.").formatted(Formatting.YELLOW), true);
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.PLAYERS, 0.6f, 1.5f);
                return TypedActionResult.fail(compass);
            }

            if (!nbt.getBoolean(TARGET_KEY)) {
                user.sendMessage(Text.literal("§7The compass must first be bound to an Obelisk.").formatted(Formatting.GRAY), true);
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.PLAYERS, 0.6f, 0.6f);
                return TypedActionResult.fail(compass);
            }

            nbt.putBoolean(LOCKED_KEY, true);
            compass.setNbt(nbt);
            offhand.decrement(1);

            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 1.0f, 1.0f);
            user.sendMessage(Text.literal("§6Obelisk Compass permanently bound!").formatted(Formatting.GOLD), true);

            return TypedActionResult.success(compass);
        }

        // --- Regular use: find nearest Obelisk ---
        if (world.isClient) return TypedActionResult.success(compass);
        if (!(world instanceof ServerWorld sw)) return TypedActionResult.success(compass);

        NbtCompound nbt = compass.getOrCreateNbt();

        if (nbt.getBoolean(LOCKED_KEY)) {
            user.sendMessage(Text.literal("§6The compass is sealed by ancient wax and cannot be rebound.").formatted(Formatting.GOLD), true);
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_CHAIN_STEP, SoundCategory.PLAYERS, 0.8f, 0.8f);
            return TypedActionResult.fail(compass);
        }

        resetCompass(compass);
        user.sendMessage(Text.literal("🔮 Searching for Obelisk...").formatted(Formatting.GRAY), true);
        user.playSound(SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, 1.0F, 1.0F);

        BlockPos nearest = ObeliskRegistry.get(sw).findNearest(user.getBlockPos(), 20000);
        if (nearest != null) {
            setTarget(compass, nearest, sw.getRegistryKey().getValue());
            user.sendMessage(Text.literal("✨ Obelisk located!").formatted(Formatting.GREEN), true);
        } else {
            user.sendMessage(Text.literal("⚠️ No Obelisks registered in this dimension.").formatted(Formatting.RED), true);
        }

        return TypedActionResult.success(compass);
    }

    /** Clears NBT target data */
    public static void resetCompass(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.remove("ObeliskX");
        nbt.remove("ObeliskY");
        nbt.remove("ObeliskZ");
        nbt.remove(DIM_KEY);
        nbt.putBoolean(TARGET_KEY, false);
    }

    /** Stores a target Obelisk’s coordinates and dimension */
    public static void setTarget(ItemStack stack, BlockPos pos, Identifier dimension) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt("ObeliskX", pos.getX());
        nbt.putInt("ObeliskY", pos.getY());
        nbt.putInt("ObeliskZ", pos.getZ());
        nbt.putString(DIM_KEY, dimension.toString());
        nbt.putBoolean(TARGET_KEY, true);
    }

    /** Tooltip — coordinates and dimension only */
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();

        if (nbt == null || !nbt.getBoolean(TARGET_KEY)) {
            tooltip.add(Text.literal("§7No Obelisk bound").formatted(Formatting.GRAY));
        } else {
            int x = nbt.getInt("ObeliskX");
            int y = nbt.getInt("ObeliskY");
            int z = nbt.getInt("ObeliskZ");
            String dim = nbt.getString(DIM_KEY);

            tooltip.add(Text.literal(String.format("§bX: %d  Y: %d  Z: %d", x, y, z)).formatted(Formatting.AQUA));
            tooltip.add(Text.literal("§7Dim: " + dim).formatted(Formatting.GRAY));
        }

        if (nbt != null && nbt.getBoolean(LOCKED_KEY)) {
            tooltip.add(Text.literal("§6Permanently Bound").formatted(Formatting.GOLD));
        }
    }

    /** Show enchantment glint when locked */
    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().getBoolean(LOCKED_KEY);
    }

    /** Retrieve target data (for model predicate use) */
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

    /** Compact record to store Obelisk data */
    public record TargetData(BlockPos pos, Identifier dimension) {}
}
