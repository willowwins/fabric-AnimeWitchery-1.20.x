package net.willowins.animewitchery;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.*;
import net.willowins.animewitchery.screen.*;
import net.willowins.animewitchery.screen.AlchemyTableScreenHandler;
import net.minecraft.entity.player.PlayerInventory;

import static net.willowins.animewitchery.AnimeWitchery.MOD_ID;

public class ModScreenHandlers {
    public static ScreenHandlerType<ItemActionScreenHandler> ITEM_ACTION_SCREEN_HANDLER;
    public static ScreenHandlerType<PlayerUseDispenserScreenHandler> PLAYER_USE_DISPENSER_SCREEN_HANDLER;
    public static ScreenHandlerType<AutoCrafterScreenHandler> AUTO_CRAFTER_SCREEN_HANDLER;
    public static ScreenHandlerType<GenericContainerScreenHandler> DISPENSER_HANDLER;
    public static ScreenHandlerType<BlockMinerScreenHandler> BLOCK_MINER_SCREEN_HANDLER;
    public static ScreenHandlerType<BlockPlacerScreenHandler> BLOCK_PLACER_SCREEN_HANDLER;
    public static ScreenHandlerType<GrowthAcceleratorScreenHandler> GROWTH_ACCELERATOR_SCREEN_HANDLER;
    public static ScreenHandlerType<AlchemyTableScreenHandler> ALCHEMY_TABLE_SCREEN_HANDLER;
    public static ScreenHandlerType<GrandShulkerBoxScreenHandler> GRAND_SHULKER_BOX_SCREEN_HANDLER;
    public static ScreenHandlerType<AdvancedSpellbookScreenHandler> ADVANCED_SPELLBOOK_SCREEN_HANDLER;
    public static ScreenHandlerType<SoulJarScreenHandler> SOUL_JAR_SCREEN_HANDLER;

    public static void registerAll() {

        DISPENSER_HANDLER = ScreenHandlerRegistry.registerSimple(
                new Identifier("animewitchery", "dispenser_handler"),
                (syncId, playerInventory) -> GenericContainerScreenHandler.createGeneric9x1(syncId, playerInventory));
        ITEM_ACTION_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("animewitchery", "item_action"),
                (syncId, playerInventory, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    World world = playerInventory.player.getWorld();
                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof ItemActionBlockEntity entity) {
                        return new ItemActionScreenHandler(ITEM_ACTION_SCREEN_HANDLER, syncId, playerInventory, entity);
                    }
                    return null;
                });
        GROWTH_ACCELERATOR_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER,
                new Identifier(MOD_ID, "growth_accelerator"),
                new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    World world = inventory.player.getWorld();
                    BlockEntity be = world.getBlockEntity(pos);
                    if (!(be instanceof GrowthAcceleratorBlockEntity accelerator)) {
                        throw new IllegalStateException("Expected GrowthAcceleratorBlockEntity at " + pos);
                    }
                    return new GrowthAcceleratorScreenHandler(syncId, inventory, accelerator);
                }));
        AUTO_CRAFTER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("animewitchery", "auto_crafter"),
                (syncId, playerInventory, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    BlockEntity be = playerInventory.player.getWorld().getBlockEntity(pos);

                    if (!(be instanceof AutoCrafterBlockEntity autoCrafterBE)) {
                        throw new IllegalStateException("Block entity is not an AutoCrafterBlockEntity!");
                    }

                    return new AutoCrafterScreenHandler(AUTO_CRAFTER_SCREEN_HANDLER, syncId, playerInventory,
                            autoCrafterBE);
                });
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
                });
        BLOCK_PLACER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("animewitchery", "block_placer"),
                (syncId, inventory, buf) -> new BlockPlacerScreenHandler(syncId, inventory, buf));

        ALCHEMY_TABLE_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("animewitchery", "alchemy_table"),
                (syncId, playerInventory, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    World world = playerInventory.player.getWorld();
                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof AlchemyTableBlockEntity alchemyTable) {
                        return new AlchemyTableScreenHandler(syncId, playerInventory, alchemyTable);
                    }
                    return null;
                });

        GRAND_SHULKER_BOX_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(
                new Identifier("animewitchery", "grand_shulker_box"),
                (syncId, playerInventory, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    World world = playerInventory.player.getWorld();
                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof GrandShulkerBoxBlockEntity grandBox) {
                        return new GrandShulkerBoxScreenHandler(GRAND_SHULKER_BOX_SCREEN_HANDLER, syncId,
                                playerInventory, grandBox);
                    }
                    return null;
                });

        ADVANCED_SPELLBOOK_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
                new Identifier("animewitchery", "advanced_spellbook"),
                (syncId, playerInventory) -> new AdvancedSpellbookScreenHandler(syncId, playerInventory,
                        new SimpleInventory(9), ItemStack.EMPTY));

        SOUL_JAR_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(
                new Identifier(MOD_ID, "soul_jar"),
                (syncId, playerInventory) -> new SoulJarScreenHandler(syncId, playerInventory));
    }
}
