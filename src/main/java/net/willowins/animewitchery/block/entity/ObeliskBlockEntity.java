package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ObeliskBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    // Texture variant (0-6 for obelisk.png through obelisk7.png)
    private int textureVariant = 0;
    private static final int MAX_VARIANTS = 8;

    public ObeliskBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OBELISK_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Idle", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    public int getTextureVariant() {
        return textureVariant;
    }
    
    public void setTextureVariant(int variant) {
        this.textureVariant = variant;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    public void cycleTextureVariant() {
        textureVariant = (textureVariant + 1) % MAX_VARIANTS;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("textureVariant", textureVariant);
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        textureVariant = nbt.getInt("textureVariant");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt(); // Send full NBT to client when chunk is loaded
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ObeliskBlockEntity entity) {
        // Tick logic for the sleeping obelisk
        // Currently just idle animation
    }
} 