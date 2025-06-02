package net.willowins.animewitchery.block.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.util.FakeServerPlayer;
import net.willowins.animewitchery.screen.PlayerUseDispenserScreenHandler;

import java.util.UUID;

    public class PlayerUseDispenserBlockEntity extends BlockEntity implements SidedInventory, ExtendedScreenHandlerFactory {

        private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

        public PlayerUseDispenserBlockEntity(BlockPos pos, BlockState state) {
            super(ModBlockEntities.PLAYER_USE_DISPENSER_BLOCK_ENTITY, pos, state);
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            buf.writeBlockPos(this.getPos());
        }

        public void dispenseBlock() {
            if (world == null || world.isClient) return;
            if (!(world instanceof ServerWorld)) return;

            Direction facing = this.getCachedState().get(Properties.FACING);
            BlockPos targetPos = this.pos.offset(facing);
            BlockState targetBlockState = world.getBlockState(targetPos);

            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.get(i);

                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof BlockItem) {
                        // Only place block if target block is replaceable or air
                        if (targetBlockState.isAir() || targetBlockState.isReplaceable()) {
                            Block blockToPlace = ((BlockItem) stack.getItem()).getBlock();
                            BlockState blockState = blockToPlace.getDefaultState();

                            world.setBlockState(targetPos, blockState);
                            stack.decrement(1);
                            if (stack.isEmpty()) {
                                inventory.set(i, ItemStack.EMPTY);
                            }
                            this.markDirty();
                            return;
                        }
                    } else {
                        if (useItemOnBlock(stack, targetPos, facing)) {
                            stack.decrement(1);
                            if (stack.isEmpty()) {
                                inventory.set(i, ItemStack.EMPTY);
                            }
                            this.markDirty();
                            return;
                        }
                    }
                }
            }
        }

        private boolean useItemOnBlock(ItemStack stack, BlockPos targetPos, Direction facing) {
            if (world == null || world.isClient) return false;
            if (!(world instanceof ServerWorld serverWorld)) return false;

            if (stack.isOf(Items.WOODEN_HOE)) {
                BlockState targetState = world.getBlockState(targetPos);
                if (targetState.isOf(Blocks.SHROOMLIGHT)) {


                    // Drop your custom item
                    ItemStack spores = new ItemStack(Items.BLAZE_POWDER); // Change to ModItems.GLOWING_SPORES if needed
                    net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
                            world,
                            targetPos.getX() + 0.5,
                            targetPos.getY() + 0.5,
                            targetPos.getZ() + 0.5,
                            spores
                    );
                    world.spawnEntity(itemEntity);

                    // Do NOT consume or damage the hoe
                    this.markDirty();
                    return true;
                }
            }


        // for custom Blaze Powder Sac on Amethyst Cluster condition
            if (stack.isOf(ModItems.BLAZE_SACK)) {
                BlockState targetState = world.getBlockState(targetPos);
                if (targetState.isOf(Blocks.AMETHYST_CLUSTER)) {
                    // Break the amethyst cluster (drops amethyst by default, unless dropLoot is false)
                    world.breakBlock(targetPos, false); // Set to 'true' if you want normal loot too

                    // Drop the Alchemical Catalyst at the position
                    ItemStack catalyst = new ItemStack(ModItems.ALCHEMICAL_CATALYST);
                    net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
                            world,
                            targetPos.getX() + 0.5,
                            targetPos.getY() + 0.5,
                            targetPos.getZ() + 0.5,
                            catalyst
                    );
                    world.spawnEntity(itemEntity);
                    // Consume one blaze powder sac from the stack
                    stack.decrement(1);

                    this.markDirty();
                    return true;
                }
            }

            // --- The rest of your fake player interaction code ---

            MinecraftServer server = serverWorld.getServer();
            if (server == null) return false;

            GameProfile fakeProfile = new GameProfile(UUID.randomUUID(), "FakePlayer");
            FakeServerPlayer fakePlayer = new FakeServerPlayer(server, serverWorld, fakeProfile);

            Vec3d playerPos = Vec3d.ofCenter(targetPos).subtract(Vec3d.of(facing.getVector()).multiply(1.5));
            fakePlayer.updatePosition(playerPos.x, playerPos.y, playerPos.z);

            Vec3d targetVec = Vec3d.ofCenter(targetPos);
            Vec3d diff = targetVec.subtract(playerPos);
            float yaw = (float)(Math.atan2(diff.z, diff.x) * 180 / Math.PI) - 90F;
            float pitch = (float)(-(Math.atan2(diff.y, Math.sqrt(diff.x*diff.x + diff.z*diff.z)) * 180 / Math.PI));
            fakePlayer.setYaw(yaw);
            fakePlayer.setPitch(pitch);
            fakePlayer.setSneaking(false);

            Hand hand = Hand.MAIN_HAND;
            Vec3d hitVec = Vec3d.ofCenter(targetPos).add(Vec3d.of(facing.getVector()).multiply(0.5));
            BlockHitResult hitResult = new BlockHitResult(hitVec, facing, targetPos, false);

            fakePlayer.setStackInHand(hand, stack.copy());

            var result = fakePlayer.interactionManager.interactBlock(fakePlayer, world, stack, hand, hitResult);

            if (result.isAccepted()) {
                ItemStack held = fakePlayer.getStackInHand(hand);
                stack.setCount(held.getCount());

                this.markDirty();
                return true;
            }

            return false;
        }




        // --- Inventory methods ---

        @Override
        public int size() {
            return inventory.size();
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : inventory) {
                if (!stack.isEmpty()) return false;
            }
            return true;
        }

        @Override
        public ItemStack getStack(int slot) {
            return inventory.get(slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            return Inventories.splitStack(inventory, slot, amount);
        }

        @Override
        public ItemStack removeStack(int slot) {
            return Inventories.removeStack(inventory, slot);
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            inventory.set(slot, stack);
            if (stack.getCount() > getMaxCountPerStack()) {
                stack.setCount(getMaxCountPerStack());
            }
        }

        @Override
        public void clear() {
            inventory.clear();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            if (world == null) return false;
            if (world.getBlockEntity(pos) != this) return false;
            return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
        }

        // --- SidedInventory methods ---

        @Override
        public int[] getAvailableSlots(Direction side) {
            int[] slots = new int[inventory.size()];
            for (int i = 0; i < slots.length; i++) slots[i] = i;
            return slots;
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, Direction dir) {
            return true;
        }

        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            return true;
        }

        // --- GUI / Menu logic ---

        @Override
        public Text getDisplayName() {
            return Text.literal("Player Use Dispenser");
        }

        @Override
        public ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory playerInventory, PlayerEntity player) {
            return new PlayerUseDispenserScreenHandler(syncId, playerInventory, this);
        }
        @Override
        public void readNbt(NbtCompound tag) {
            super.readNbt(tag);
            // Load inventory from NBT
            Inventories.readNbt(tag, inventory);
        }

        @Override
        public void writeNbt(NbtCompound tag) {
            super.writeNbt(tag);
            // Save inventory to NBT
            Inventories.writeNbt(tag, inventory);
        }
}