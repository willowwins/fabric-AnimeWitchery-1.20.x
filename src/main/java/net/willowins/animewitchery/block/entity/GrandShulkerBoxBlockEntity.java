package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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
        // Trigger open animation
        triggerAnim("controller", "open");
        isOpen = true;
        
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
        // The parent ShulkerBoxBlockEntity handles inventory serialization
        nbt.putBoolean("isOpen", this.isOpen);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        // The parent ShulkerBoxBlockEntity handles inventory deserialization
        this.isOpen = nbt.getBoolean("isOpen");
    }

    @Override
    public int size() {
        return 54; // Grand Shulker Box has 54 slots (6 rows of 9)
    }

    // All inventory methods are inherited from ShulkerBoxBlockEntity

    public int getMaxCountPerStack() {
        return 256;
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
                return state.setAndContinue(RawAnimation.begin().thenPlay("close"));
            }
        }));
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
