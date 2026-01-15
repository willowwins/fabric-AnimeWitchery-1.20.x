package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SoulJarItem extends Item {
    public SoulJarItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            // Open GUI
            user.openHandledScreen(new net.minecraft.screen.SimpleNamedScreenHandlerFactory(
                    (syncId, inventory, player) -> new net.willowins.animewitchery.screen.SoulJarScreenHandler(syncId,
                            inventory),
                    Text.literal("Soul Jar")));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public static void addSoul(ItemStack jar, String entityId, NbtCompound entityData) {
        NbtCompound nbt = jar.getOrCreateNbt();
        NbtList souls = nbt.getList("Souls", NbtElement.COMPOUND_TYPE);

        NbtCompound soulEntry = new NbtCompound();
        soulEntry.putString("EntityId", entityId);
        soulEntry.put("Data", entityData);
        // Add name for display?
        if (entityData.contains("CustomName")) {
            soulEntry.putString("Name", entityData.getString("CustomName"));
        } else {
            // Try to get a translation key or just store the ID
            soulEntry.putString("Name", Text.translatable("entity." + entityId.replace(":", ".")).getString());
        }

        souls.add(soulEntry);
        nbt.put("Souls", souls);
    }

    public static Optional<NbtCompound> getNextSoul(ItemStack jar) {
        NbtCompound nbt = jar.getOrCreateNbt();
        NbtList souls = nbt.getList("Souls", NbtElement.COMPOUND_TYPE);

        if (souls.isEmpty())
            return Optional.empty();

        return Optional.of(souls.getCompound(0));
    }

    public static void removeNextSoul(ItemStack jar) {
        removeSoulAtIndex(jar, 0);
    }

    public static void removeSoulAtIndex(ItemStack jar, int index) {
        NbtCompound nbt = jar.getOrCreateNbt();
        NbtList souls = nbt.getList("Souls", NbtElement.COMPOUND_TYPE);

        if (index >= 0 && index < souls.size()) {
            souls.remove(index);
            nbt.put("Souls", souls);
        }
    }

    public static void swapSouls(ItemStack jar, int index1, int index2) {
        NbtCompound nbt = jar.getOrCreateNbt();
        NbtList souls = nbt.getList("Souls", NbtElement.COMPOUND_TYPE);

        if (index1 >= 0 && index1 < souls.size() && index2 >= 0 && index2 < souls.size()) {
            NbtElement temp = souls.get(index1).copy(); // copy to be safe
            souls.set(index1, souls.get(index2));
            souls.set(index2, temp);
            nbt.put("Souls", souls);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("Souls")) {
            NbtList souls = nbt.getList("Souls", NbtElement.COMPOUND_TYPE);
            tooltip.add(Text.literal("Souls stored: " + souls.size()).formatted(Formatting.GRAY));

            // Show first few souls
            for (int i = 0; i < Math.min(souls.size(), 3); i++) {
                NbtCompound soul = souls.getCompound(i);
                String name = soul.getString("Name");
                if (name.isEmpty())
                    name = soul.getString("EntityId");
                tooltip.add(Text.literal("- " + name).formatted(Formatting.DARK_AQUA));
            }
            if (souls.size() > 3) {
                tooltip.add(Text.literal("...").formatted(Formatting.DARK_AQUA));
            }
        } else {
            tooltip.add(Text.literal("Empty").formatted(Formatting.GRAY));
        }
    }
}
