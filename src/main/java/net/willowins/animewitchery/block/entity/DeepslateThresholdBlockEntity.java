package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DeepslateThresholdBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DeepslateThresholdBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DEEPSLATE_THRESHOLD_ENTITY, pos, state);
    }

    private java.util.UUID pocketUuid;
    private int pocketId = -1;
    private net.minecraft.util.math.BlockPos targetPos;
    private String targetDim;

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // No animation for now, just static model
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, state -> {
            return software.bernie.geckolib.core.object.PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setPocketUuid(java.util.UUID uuid) {
        this.pocketUuid = uuid;
        markDirty();
    }

    public java.util.UUID getPocketUuid() {
        return pocketUuid;
    }

    public void setPocketId(int id) {
        this.pocketId = id;
        markDirty();
    }

    public int getPocketId() {
        return pocketId;
    }

    public void setTargetLocation(net.minecraft.util.math.BlockPos pos, String dim) {
        this.targetPos = pos;
        this.targetDim = dim;
        markDirty();
    }

    public net.minecraft.util.math.BlockPos getTargetPos() {
        return targetPos;
    }

    public String getTargetDim() {
        return targetDim;
    }

    @Override
    public void readNbt(net.minecraft.nbt.NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.containsUuid("PocketUuid")) {
            this.pocketUuid = nbt.getUuid("PocketUuid");
        }
        if (nbt.contains("PocketId")) {
            this.pocketId = nbt.getInt("PocketId");
        }
        if (nbt.contains("TargetX")) {
            this.targetPos = new net.minecraft.util.math.BlockPos(nbt.getInt("TargetX"), nbt.getInt("TargetY"),
                    nbt.getInt("TargetZ"));
        }
        if (nbt.contains("TargetDim")) {
            this.targetDim = nbt.getString("TargetDim");
        }
        System.out.println("DeepslateEntity ReadNBT: ID=" + pocketId + " UUID=" + pocketUuid + " Target=" + targetPos);
    }

    @Override
    public void writeNbt(net.minecraft.nbt.NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.pocketUuid != null) {
            nbt.putUuid("PocketUuid", this.pocketUuid);
        }
        nbt.putInt("PocketId", this.pocketId);
        if (this.targetPos != null) {
            nbt.putInt("TargetX", this.targetPos.getX());
            nbt.putInt("TargetY", this.targetPos.getY());
            nbt.putInt("TargetZ", this.targetPos.getZ());
        }
        if (this.targetDim != null) {
            nbt.putString("TargetDim", this.targetDim);
        }
        System.out.println("DeepslateEntity WriteNBT: ID=" + pocketId + " UUID=" + pocketUuid + " Target=" + targetPos);
    }
}
