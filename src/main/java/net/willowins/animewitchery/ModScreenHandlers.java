package net.willowins.animewitchery;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.AutoCrafterBlockEntity;
import net.willowins.animewitchery.block.entity.BlockMinerBlockEntity;
import net.willowins.animewitchery.block.entity.BlockPlacerBlockEntity;
import net.willowins.animewitchery.block.entity.ItemActionBlockEntity;
import net.willowins.animewitchery.screen.*;
import net.minecraft.entity.player.PlayerInventory;


public class ModScreenHandlers {
    public static ScreenHandlerType<ItemActionScreenHandler> ITEM_ACTION_SCREEN_HANDLER;
    public static ScreenHandlerType<PlayerUseDispenserScreenHandler> PLAYER_USE_DISPENSER_SCREEN_HANDLER;
    public static ScreenHandlerType<AutoCrafterScreenHandler> AUTO_CRAFTER_SCREEN_HANDLER;
    public static ScreenHandlerType<GenericContainerScreenHandler> DISPENSER_HANDLER;
    public static ScreenHandlerType<BlockMinerScreenHandler> BLOCK_MINER_SCREEN_HANDLER;
    public static ScreenHandlerType<BlockPlacerScreenHandler> BLOCK_PLACER_SCREEN_HANDLER;

    public static void registerAll() {



        DISPENSER_HANDLER = ScreenHandlerRegistry.registerSimple(
                new Identifier("animewitchery", "dispenser_handler"),
                (syncId, playerInventory) -> GenericContainerScreenHandler.createGeneric9x1(syncId, playerInventory)
        );
        ITEM_ACTION_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("animewitchery", "item_action"),
                (syncId, playerInventory, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    World world = playerInventory.player.getWorld();
                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof ItemActionBlockEntity entity) {
                        return new ItemActionScreenHandler(ITEM_ACTION_SCREEN_HANDLER, syncId, playerInventory, entity);
                    }
                    return null;
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
        BLOCK_MINER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("animewitchery", "block_miner"),
                (syncId, playerInventory, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    World world = playerInventory.player.getWorld();
                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof BlockMinerBlockEntity miner) {
                        return new BlockMinerScreenHandler(syncId, playerInventory, miner);
                    }
                    return null;
                }
        );   BLOCK_PLACER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("animewitchery", "block_placer"),
                (syncId, inventory, buf) -> new BlockPlacerScreenHandler(syncId, inventory, buf)
        );
    }
}

