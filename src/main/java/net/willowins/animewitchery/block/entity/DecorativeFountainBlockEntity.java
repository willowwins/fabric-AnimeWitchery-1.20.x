package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;

public class DecorativeFountainBlockEntity extends BlockEntity{


    protected final PropertyDelegate propertyDelegate;
    private int progress =0;
    private int maxProgress =72;

    public DecorativeFountainBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DECORATIVE_FOUNTAIN_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> DecorativeFountainBlockEntity.this.progress;
                    case 1 -> DecorativeFountainBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> DecorativeFountainBlockEntity.this.progress = value;
                    case 1 -> DecorativeFountainBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

}
