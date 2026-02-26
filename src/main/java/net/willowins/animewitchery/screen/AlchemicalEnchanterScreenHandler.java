package net.willowins.animewitchery.screen;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.random.Random;
import net.willowins.animewitchery.ModScreenHandlers;
import net.willowins.animewitchery.block.entity.AlchemicalEnchanterBlockEntity;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.item.custom.ResonantlCatalystItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import com.google.common.collect.Lists;
import java.util.List;

public class AlchemicalEnchanterScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final Inventory inventory;
    private final PlayerInventory playerInventory;
    private final Random random = Random.create();
    private final net.minecraft.screen.Property seed = net.minecraft.screen.Property.create();
    public final List<EnchantmentLevelEntry> availableEnchantments = Lists.newArrayList();
    private final AlchemicalEnchanterBlockEntity blockEntity;

    public AlchemicalEnchanterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY, new SimpleInventory(2));
    }

    public AlchemicalEnchanterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY, inventory);
    }

    public AlchemicalEnchanterScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context,
            Inventory inventory) {
        super(ModScreenHandlers.ALCHEMICAL_ENCHANTER_SCREEN_HANDLER, syncId);
        this.context = context;
        this.playerInventory = playerInventory;
        this.inventory = inventory;
        this.blockEntity = (inventory instanceof AlchemicalEnchanterBlockEntity)
                ? (AlchemicalEnchanterBlockEntity) inventory
                : null;
        inventory.onOpen(playerInventory.player);

        // UI Slots
        this.addSlot(new Slot(inventory, 0, 155, 75) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });

        this.addSlot(new Slot(inventory, 1, 177, 75) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.ALCHEMICAL_CATALYST) || stack.isOf(ModItems.RESONANT_CATALYST);
            }
        });

        // Hotbar (9 slots) - Shifted lower to avoid radial overlap
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 94 + i * 18, 175));
        }

        this.addProperty(seed).set(playerInventory.player.getEnchantmentTableSeed());
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.ALCHEMICAL_ENCHANTER);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);

        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();

            if (slot < 2) {
                // From UI to Hotbar
                if (!this.insertItem(itemStack2, 2, 11, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From Hotbar to UI
                if (itemStack2.isOf(ModItems.ALCHEMICAL_CATALYST)) {
                    if (!this.insertItem(itemStack2, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            if (this.blockEntity != null && !playerInventory.player.getWorld().isClient) {
                this.availableEnchantments.clear();
                this.availableEnchantments.addAll(this.blockEntity.getAvailableEnchantments());
                this.sendContentUpdates();
                this.blockEntity.syncToPlayer(this.playerInventory.player);
            }
        }
    }

    public boolean enchantItem(PlayerEntity player, int buttonId) {
        ItemStack itemToEnchant = this.inventory.getStack(0);
        ItemStack catalyst = this.inventory.getStack(1);

        if (itemToEnchant.isEmpty() || catalyst.isEmpty()) {
            return false;
        }

        // Decode: id = (enchantIndex * 100) + selectedLevel (1-indexed)
        int enchantIndex = buttonId / 100;
        int levelToApply = buttonId % 100;

        if (enchantIndex < 0 || enchantIndex >= availableEnchantments.size())
            return false;
        Enchantment selectedEnchant = availableEnchantments.get(enchantIndex).enchantment;
        int maxAllowed = availableEnchantments.get(enchantIndex).level;

        if (levelToApply < 1 || levelToApply > maxAllowed)
            return false;

        boolean isResonant = catalyst.isOf(ModItems.RESONANT_CATALYST);
        // Scaling cost: 10 + (level-1)*5
        int requiredLevel = 10 + (levelToApply - 1) * 5;

        if (player.experienceLevel < requiredLevel) {
            return false;
        }

        // Apply cost first
        if (isResonant) {
            int mana = ResonantlCatalystItem.getStoredMana(catalyst);
            int manaCost = requiredLevel * 100;
            if (mana < manaCost)
                return false;
            ResonantlCatalystItem.setStoredMana(catalyst, mana - manaCost);
        } else {
            catalyst.decrement(1);
        }

        // Check for existing enchantment and update level
        java.util.Map<Enchantment, Integer> enchants = net.minecraft.enchantment.EnchantmentHelper.get(itemToEnchant);
        enchants.put(selectedEnchant, levelToApply);
        net.minecraft.enchantment.EnchantmentHelper.set(enchants, itemToEnchant);

        player.applyEnchantmentCosts(itemToEnchant, requiredLevel);
        this.seed.set(player.getEnchantmentTableSeed());
        this.inventory.markDirty();
        this.onContentChanged(this.inventory);

        if (!player.getWorld().isClient) {
            player.getWorld().playSound(null, player.getBlockPos(),
                    SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    SoundCategory.BLOCKS, 1.0f, 1.0f);
        }

        return true;
    }

    public void updateClientEnchantmentLists(List<EnchantmentLevelEntry> lists) {
        this.availableEnchantments.clear();
        this.availableEnchantments.addAll(lists);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0) {
            return this.enchantItem(player, id);
        }
        return false;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, net.minecraft.screen.slot.SlotActionType actionType,
            PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        this.onContentChanged(this.inventory);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}
