package net.willowins.animewitchery;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.block.entity.AutoCrafterBlockEntity;
import net.willowins.animewitchery.block.entity.PlayerUseDispenserBlockEntity;
import net.willowins.animewitchery.screen.AutoCrafterScreenHandler;
import net.willowins.animewitchery.screen.PlayerUseDispenserScreenHandler;

public class ModScreenHandlers {

    public static ScreenHandlerType<PlayerUseDispenserScreenHandler> PLAYER_USE_DISPENSER_SCREEN_HANDLER;
    public static ScreenHandlerType<AutoCrafterScreenHandler> AUTO_CRAFTER_SCREEN_HANDLER;

    public static void registerAll() {
        PLAYER_USE_DISPENSER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("animewitchery", "player_use_dispenser"),
                (syncId, playerInventory, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    BlockEntity blockEntity = playerInventory.player.getWorld().getBlockEntity(pos);
                    if (blockEntity instanceof PlayerUseDispenserBlockEntity dispenser) {
                        return new PlayerUseDispenserScreenHandler(syncId, playerInventory, dispenser);
                    }
                    throw new IllegalStateException("Block entity is not PlayerUseDispenserBlockEntity!");
                }
        );
        AUTO_CRAFTER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("animewitchery", "auto_crafter"),
                (syncId, playerInventory, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    BlockEntity be = playerInventory.player.getWorld().getBlockEntity(pos);

                    if (!(be instanceof AutoCrafterBlockEntity autoCrafterBE)) {
                        throw new IllegalStateException("Block entity is not an AutoCrafterBlockEntity!");
                    }

                    return new AutoCrafterScreenHandler(AUTO_CRAFTER_SCREEN_HANDLER, syncId, playerInventory, autoCrafterBE);
                }
        );

    }
}
