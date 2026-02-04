package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulJarItem extends Item {
    public SoulJarItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack stack = user.getStackInHand(hand);
            if (user.getMainHandStack().getItem() instanceof SummonerStaffItem) {
                return TypedActionResult.pass(stack);
            }
            user.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return Text.literal("Soul Jar");
                }

                @Nullable
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    SimpleInventory inventory = new SimpleInventory(27);
                    inventory.addListener(i -> saveInventory(stack, i));
                    loadInventory(stack, inventory);

                    return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory,
                            inventory, 3) {
                        @Override
                        public void onClosed(PlayerEntity player) {
                            super.onClosed(player);
                            saveInventory(stack, inventory);
                        }

                        @Override
                        public ItemStack quickMove(PlayerEntity player, int slot) {
                            // Basic implementation for now
                            return super.quickMove(player, slot);
                        }
                    };
                }
            });
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public static void loadInventory(ItemStack stack, SimpleInventory inventory) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains("Items")) {
            NbtList list = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound itemNbt = list.getCompound(i);
                int slot = itemNbt.getInt("Slot");
                if (slot >= 0 && slot < inventory.size()) {
                    inventory.setStack(slot, ItemStack.fromNbt(itemNbt));
                }
            }
        }
    }

    public static void saveInventory(ItemStack stack, Inventory inventory) {
        NbtList list = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putInt("Slot", i);
                itemStack.writeNbt(itemNbt);
                list.add(itemNbt);
            }
        }
        stack.getOrCreateNbt().put("Items", list);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("Items")) {
            NbtList list = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            tooltip.add(Text.literal("Contains " + list.size() + " items").formatted(Formatting.GRAY));
        }
    }

    /**
     * Attempts to capture the given entity into the Soul Jar.
     * Checks checks for space and adds the soul if possible.
     * Does NOT handle discarding the entity - caller must do that if true is
     * returned.
     * 
     * @return true if captured successfully
     */
    public static boolean captureEntity(PlayerEntity user, net.minecraft.entity.LivingEntity entity,
            ItemStack jarStack) {
        SimpleInventory inv = new SimpleInventory(27);
        loadInventory(jarStack, inv);

        if (!hasSpace(inv)) {
            return false;
        }

        NbtCompound data = new NbtCompound();
        entity.saveNbt(data);

        ItemStack soulStack = new ItemStack(net.willowins.animewitchery.item.ModItems.SOUL);
        NbtCompound soulNbt = soulStack.getOrCreateNbt();
        soulNbt.put("EntityData", data);
        if (entity.hasCustomName()) {
            soulNbt.putString("EntityName", entity.getCustomName().getString());
        } else {
            soulNbt.putString("EntityName", entity.getType().getName().getString());
        }

        addToInv(inv, soulStack);
        saveInventory(jarStack, inv);
        return true;
    }

    public static boolean hasSpace(SimpleInventory inv) {
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).isEmpty())
                return true;
        }
        return false;
    }

    public static void addToInv(SimpleInventory inv, ItemStack stack) {
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).isEmpty()) {
                inv.setStack(i, stack);
                return;
            }
        }
    }
}
