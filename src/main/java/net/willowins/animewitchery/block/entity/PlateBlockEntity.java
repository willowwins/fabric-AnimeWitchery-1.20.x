package net.willowins.animewitchery.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.recipe.AlchemyRecipe;
import net.willowins.animewitchery.recipe.ModRecipes;
import net.willowins.animewitchery.util.ImplementedInventory;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import static net.willowins.animewitchery.block.custom.PlateBlock.HAS_ITEM;

public class PlateBlockEntity extends BlockEntity implements ImplementedInventory{

    // Inventory slots: 0=Output (center), 1-10=Input slots (counter-clockwise from top)
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);


    public PlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLATE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }


    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        this.markDirty();
        this.syncAndFlag();
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = ImplementedInventory.super.removeStack(slot);
        this.inventory.set(slot, ItemStack.EMPTY);
        this.markDirty();
        this.syncAndFlag();
        return result;
    }

    private void syncAndFlag() {
        if (this.world == null) return;

        // 1) Flip the HAS_ITEM blockstate if needed (forces client re-render)
        boolean hasItem = !this.inventory.get(0).isEmpty();
        BlockState state = this.getCachedState();
        if (state.contains(HAS_ITEM) && state.get(HAS_ITEM) != hasItem) {
            // Flag 3 = notify clients + re-render
            this.world.setBlockState(this.pos, state.with(HAS_ITEM, hasItem), Block.NOTIFY_ALL);
            state = this.getCachedState(); // refresh local
        } else {
            // If state didn't change, still poke clients to re-read BE NBT
            this.world.updateListeners(this.pos, state, state, Block.NOTIFY_LISTENERS);
        }

        // Force update listeners
        this.world.updateListeners(this.pos, state, state, Block.NOTIFY_ALL);


        // 2) Ensure BE update packet goes out
        if (!this.world.isClient) {
            ((ServerWorld)this.world).getChunkManager().markForUpdate(this.pos);
        }
    }



    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        Inventories.writeNbt(nbt, inventory);
        return nbt;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return pos.isWithinDistance(player.getBlockPos(), 4.5);
    }
}
