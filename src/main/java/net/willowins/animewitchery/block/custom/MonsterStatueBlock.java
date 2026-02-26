package net.willowins.animewitchery.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.entity.custom.MonsterStatueBlockEntity;
import net.willowins.animewitchery.sound.ModSounds;
import org.jetbrains.annotations.Nullable;

public class MonsterStatueBlock extends Block implements BlockEntityProvider {
    public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 30, 14);

    public MonsterStatueBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(ACTIVATED, false)
                .with(FACING, net.minecraft.util.math.Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED, FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MonsterStatueBlockEntity(pos, state);
    }

    @Override
    public net.minecraft.block.BlockRenderType getRenderType(BlockState state) {
        return net.minecraft.block.BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.block.entity.BlockEntityTicker<T> getTicker(World world,
            BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
        return !world.isClient ? checkType(type, ModBlockEntities.MONSTER_STATUE_BLOCK_ENTITY,
                MonsterStatueBlockEntity::tick) : null;
    }

    private static <E extends BlockEntity, A extends BlockEntity> net.minecraft.block.entity.BlockEntityTicker<A> checkType(
            net.minecraft.block.entity.BlockEntityType<A> givenType,
            net.minecraft.block.entity.BlockEntityType<E> expectedType,
            net.minecraft.block.entity.BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (net.minecraft.block.entity.BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);

        // Tag check would be better, but explicit check for consistency with request
        if (stack.getItem().toString().contains("wool")) {
            if (!world.isClient) {
                boolean newState = !state.get(ACTIVATED);
                world.setBlockState(pos, state.with(ACTIVATED, newState));
                world.playSound(null, pos, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (newState) {
                    // Immediate feedback sound
                    world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 0.5f);
                }
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
