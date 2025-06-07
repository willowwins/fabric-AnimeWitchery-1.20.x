package net.willowins.animewitchery.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.ModScreenHandlers;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.screen.BlockMinerScreenHandler;

public class BlockMinerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, SidedInventory {
    public static BlockEntityType<BlockMinerBlockEntity> BLOCK_MINER_BLOCK_ENTITY;

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private boolean wasPowered = false;

    public BlockMinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOCK_MINER_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockMinerBlockEntity be) {
        if (world.isClient) return;

        boolean isPowered = world.isReceivingRedstonePower(pos);
        if (isPowered && !be.wasPowered) {
            be.mineBlock(world, pos);
        }
        be.wasPowered = isPowered;
    }

    public static void registerBlockEntities() {
        BLOCK_MINER_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(AnimeWitchery.MOD_ID, "block_miner"),
                BlockEntityType.Builder.create(BlockMinerBlockEntity::new, ModBlocks.BLOCK_MINER).build(null)
        );
    }

    private void mineBlock(World world, BlockPos pos) {
        Direction facing = getCachedState().get(Properties.HORIZONTAL_FACING);
        BlockPos targetPos = pos.offset(facing);
        BlockState targetState = world.getBlockState(targetPos);

        if (!(world instanceof ServerWorld serverWorld)) return;

        float hardness = targetState.getHardness(world, targetPos);

        // Check if block hardness is >= 0 (breakable)
        if (hardness < 0) return;

        // Check if the block is obsidian or hardness <= 5 (diamond pickaxe breakable)
        boolean canMineObsidian = targetState.isOf(net.minecraft.block.Blocks.OBSIDIAN);

        if (hardness <= 5.0F || canMineObsidian) {
            var drops = Block.getDroppedStacks(targetState, serverWorld, targetPos, world.getBlockEntity(targetPos));
            world.breakBlock(targetPos, false);
            for (ItemStack stack : drops) {
                insertStack(stack);
            }
        }
    }

    private void insertStack(ItemStack stack) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slotStack = inventory.get(i);
            if (slotStack.isEmpty()) {
                inventory.set(i, stack);
                markDirty();
                return;
            } else if (ItemStack.canCombine(slotStack, stack)) {
                int combined = slotStack.getCount() + stack.getCount();
                int max = Math.min(64, slotStack.getMaxCount());
                if (combined <= max) {
                    slotStack.increment(stack.getCount());
                    markDirty();
                    return;
                } else {
                    int diff = max - slotStack.getCount();
                    slotStack.increment(diff);
                    stack.decrement(diff);
                }
            }
        }
        // Drop leftovers
        if (!stack.isEmpty()) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Block Miner");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BlockMinerScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = Inventories.splitStack(inventory, slot, amount);
        if (!stack.isEmpty()) markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = Inventories.removeStack(inventory, slot);
        if (!stack.isEmpty()) markDirty();
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        inventory.clear();
        markDirty();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return false; // Only mined items inserted automatically
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        super.writeNbt(nbt);
    }
    public void dropInventory(World world, BlockPos pos) {
        if (world == null || world.isClient()) return;
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                world.spawnEntity(itemEntity);
            }
        }
    }
}
