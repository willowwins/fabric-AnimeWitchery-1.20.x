package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.willowins.animewitchery.item.custom.SoulJarItem;
import net.willowins.animewitchery.ModScreenHandlers;

public class SoulJarScreenHandler extends ScreenHandler {
    private final PlayerInventory playerInventory;

    // We can pass the screen handler type via constructor or separate registration
    public SoulJarScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(ModScreenHandlers.SOUL_JAR_SCREEN_HANDLER, syncId, playerInventory);
    }

    public SoulJarScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory) {
        super(type, syncId);
        this.playerInventory = playerInventory;

        layoutPlayerInventory(playerInventory);
    }

    private void layoutPlayerInventory(PlayerInventory playerInventory) {
        // Player Inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 50)); // Adjusted Y for
                                                                                                      // custom GUI size
            }
        }

        // Hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142 + 50)); // Adjusted Y
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        ItemStack main = player.getMainHandStack();
        ItemStack off = player.getOffHandStack();
        return main.getItem() instanceof SoulJarItem || off.getItem() instanceof SoulJarItem;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        // Simple transfer logic - we don't have container slots, so just return empty
        return ItemStack.EMPTY;
    }
}
