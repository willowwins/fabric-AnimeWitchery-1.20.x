package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GrandShulkerBoxBlockEntity extends net.minecraft.block.entity.ShulkerBoxBlockEntity
        implements ExtendedScreenHandlerFactory, GeoBlockEntity {

    // We'll override the inventory size to 54 slots instead of 27
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean isOpen = false;
    
    // Override the inventory to have 54 slots
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);

    public GrandShulkerBoxBlockEntity(BlockPos pos, BlockState state) {
        super(getColorFromState(state), pos, state);
    }
    
    private static net.minecraft.util.DyeColor getColorFromState(BlockState state) {
        if (state.getBlock() instanceof net.willowins.animewitchery.block.custom.GrandShulkerBoxBlock grandBox) {
            return grandBox.getColor();
        }
        return net.minecraft.util.DyeColor.PURPLE; // Fallback
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Grand Shulker Box");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        // Set open state and mark dirty to sync to client
        isOpen = true;
        this.markDirty();
        updateAnimationState();
        
        // Play shulker box open sound
        if (!world.isClient) {
            world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_SHULKER_BOX_OPEN, 
                    net.minecraft.sound.SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
        
        return new net.willowins.animewitchery.screen.GrandShulkerBoxScreenHandler(
                net.willowins.animewitchery.ModScreenHandlers.GRAND_SHULKER_BOX_SCREEN_HANDLER, 
                syncId, 
                playerInventory, 
                this
        );
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        // Custom serialization to support 8x stack sizes
        NbtList itemsList = new NbtList();
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.inventory.get(i);
            if (!stack.isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) i);
                stack.writeNbt(itemNbt);
                // Store the actual count separately to bypass validation
                itemNbt.putInt("ActualCount", stack.getCount());
                itemsList.add(itemNbt);
            }
        }
        nbt.put("GrandItems", itemsList);
        nbt.putBoolean("isOpen", this.isOpen);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        // Custom deserialization to support 8x stack sizes
        this.inventory.clear();
        NbtList itemsList = nbt.getList("GrandItems", 10); // 10 = NbtCompound type
        for (int i = 0; i < itemsList.size(); i++) {
            NbtCompound itemNbt = itemsList.getCompound(i);
            int slot = itemNbt.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.inventory.size()) {
                ItemStack stack = ItemStack.fromNbt(itemNbt);
                // Restore the actual count (bypassing the default validation)
                if (itemNbt.contains("ActualCount")) {
                    stack.setCount(itemNbt.getInt("ActualCount"));
                }
                this.inventory.set(slot, stack);
            }
        }
        this.isOpen = nbt.getBoolean("isOpen");
    }

    @Override
    public int size() {
        return 54; // Grand Shulker Box has 54 slots (6 rows of 9)
    }
    
    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        // Don't clamp - let the slot handle max stack sizes (8x normal)
        // The slot's getMaxItemCount will enforce the correct limit
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }
    
    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public int getMaxCountPerStack() {
        // Return a high value to allow 8x stacks (max vanilla item is 64, so 64*8 = 512)
        return 512;
    }

    /**
     * Get the number of non-empty slots
     */
    public int getOccupiedSlots() {
        int count = 0;
        for (int i = 0; i < this.size(); i++) {
            if (!this.getStack(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public BlockEntityType<?> getType() {
        return net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_ENTITY;
    }

    // GeckoLib methods
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (isOpen) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("open"));
            } else {
                // Play close animation when closing
                return state.setAndContinue(RawAnimation.begin().thenPlay("close"));
            }
        }));
    }
    
    // Force animation update when state changes
    public void updateAnimationState() {
        if (world != null && !world.isClient) {
            // Send update packet to client
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
        // Mark the block entity as dirty to sync the state
        this.markDirty();
    }

    public boolean isOpen() {
        return this.isOpen;
    }

}
