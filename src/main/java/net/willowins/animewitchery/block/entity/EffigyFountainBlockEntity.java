package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class EffigyFountainBlockEntity extends BlockEntity{


    protected final PropertyDelegate propertyDelegate;
    private int progress =0;
    private int maxProgress =72;

    public EffigyFountainBlockEntity( BlockPos pos, BlockState state) {
        super(ModBlockEntities.EFFIGY_FOUNTAIN_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> EffigyFountainBlockEntity.this.progress;
                    case 1 -> EffigyFountainBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> EffigyFountainBlockEntity.this.progress = value;
                    case 1 -> EffigyFountainBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

}
