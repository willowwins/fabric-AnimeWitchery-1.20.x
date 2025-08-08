package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ActiveObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.client.sky.SkyRitualRenderer;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;

public class BarrierCircleBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

    public BarrierCircleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BarrierCircleBlockEntity circleEntity) {
                ItemStack heldItem = player.getStackInHand(hand);
                
                // Stage 2: Barrier Catalyst defines circle type
                if (heldItem.isOf(ModItems.BARRIER_CATALYST)) {
                    if (circleEntity.getStage() == BarrierCircleBlockEntity.CircleStage.BASIC) {
                        System.out.println("BarrierCircle: Advancing from BASIC to DEFINED");
                        circleEntity.setCircleType(BarrierCircleBlockEntity.CircleType.BARRIER);
                        circleEntity.setStage(BarrierCircleBlockEntity.CircleStage.DEFINED);
                        return ActionResult.SUCCESS;
                    }
                }
                
                // Stage 3: Magic Chalk completes the circle (from DEFINED to COMPLETE)
                if (heldItem.isOf(ModItems.MAGIC_CHALK)) {
                    if (circleEntity.getStage() == BarrierCircleBlockEntity.CircleStage.DEFINED) {
                        System.out.println("BarrierCircle: Advancing from DEFINED to COMPLETE");
                        circleEntity.setStage(BarrierCircleBlockEntity.CircleStage.COMPLETE);
                        return ActionResult.SUCCESS;
                    } else if (circleEntity.getStage() == BarrierCircleBlockEntity.CircleStage.BASIC) {
                        System.out.println("BarrierCircle: Already in BASIC stage, use Barrier Catalyst next");
                        return ActionResult.SUCCESS;
                    } else if (circleEntity.getStage() == BarrierCircleBlockEntity.CircleStage.COMPLETE) {
                        System.out.println("BarrierCircle: Already complete!");
                        return ActionResult.SUCCESS;
                    }
                }
                
                // RITUAL ACTIVATION: Alchemy Catalyst activates the ritual
                if (heldItem.isOf(ModItems.ALCHEMICAL_CATALYST)) {
                    if (circleEntity.getStage() == BarrierCircleBlockEntity.CircleStage.COMPLETE) {
                        System.out.println("BarrierCircle: Attempting ritual activation...");
                        if (checkRitualObelisks(world, pos)) {
                            System.out.println("BarrierCircle: RITUAL ACTIVATED! All obelisks found!");
                            activateRitual(world, pos, player);
                            return ActionResult.SUCCESS;
                        } else {
                            System.out.println("BarrierCircle: Ritual failed - missing obelisks");
                            // Play failure sound
                            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.BLOCKS, 0.5f, 0.5f);
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    private boolean checkRitualObelisks(World world, BlockPos circlePos) {
        // Check for 4 obelisks in cardinal directions around the circle
        // North, South, East, West at specific distances
        
        BlockPos northPos = circlePos.north(5);  // 5 blocks north
        BlockPos southPos = circlePos.south(5);  // 5 blocks south  
        BlockPos eastPos = circlePos.east(5);    // 5 blocks east
        BlockPos westPos = circlePos.west(5);    // 5 blocks west
        
        boolean hasNorth = world.getBlockState(northPos).isOf(ModBlocks.OBELISK);
        boolean hasSouth = world.getBlockState(southPos).isOf(ModBlocks.OBELISK);
        boolean hasEast = world.getBlockState(eastPos).isOf(ModBlocks.OBELISK);
        boolean hasWest = world.getBlockState(westPos).isOf(ModBlocks.OBELISK);
        
        System.out.println("BarrierCircle: Checking obelisks - N:" + hasNorth + " S:" + hasSouth + " E:" + hasEast + " W:" + hasWest);
        
        return hasNorth && hasSouth && hasEast && hasWest;
    }
    
    private void activateRitual(World world, BlockPos circlePos, PlayerEntity player) {
        // Consume the catalyst
        player.getStackInHand(Hand.MAIN_HAND).decrement(1);
        
        // Spawn activation particles
        spawnRitualActivationParticles(world, circlePos);
        
        // Play activation sound
        world.playSound(null, circlePos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.5f);
        
        // Start the sequential ritual activation
        startSequentialRitual(world, circlePos);
        
        System.out.println("BarrierCircle: RITUAL SUCCESSFULLY ACTIVATED!");
    }
    
    private void startSequentialRitual(World world, BlockPos circlePos) {
        // Start the sequential activation
        // Obelisk 1: North (immediate)
        activateObeliskWithLightning(world, circlePos.north(5), "NORTH", circlePos);
        
        // Schedule the next obelisk activation
        world.scheduleBlockTick(circlePos, this, 20);
    }
    
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        // Handle the scheduled ticks for sequential activation
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BarrierCircleBlockEntity circleEntity) {
            int step = circleEntity.getRitualActivationStep();
            
            switch (step) {
                case 0: // First scheduled tick - activate East
                    activateObeliskWithLightning(world, pos.east(5), "EAST", pos);
                    circleEntity.advanceRitualStep();
                    world.scheduleBlockTick(pos, this, 20);
                    break;
                case 1: // Second scheduled tick - activate South
                    activateObeliskWithLightning(world, pos.south(5), "SOUTH", pos);
                    circleEntity.advanceRitualStep();
                    world.scheduleBlockTick(pos, this, 20);
                    break;
                case 2: // Third scheduled tick - activate West
                    activateObeliskWithLightning(world, pos.west(5), "WEST", pos);
                    circleEntity.advanceRitualStep();
                    // Start the final ritual effect!
                    startFinalRitualEffect(world, pos);
                    break;
                case 3: // Fourth scheduled tick - shoot the giant beam
                    shootGiantBeam(world, pos);
                    circleEntity.advanceRitualStep();
                    // Ritual is now completely finished!
                    SkyRitualRenderer.addActiveRitual(pos);

                    break;
            }
        }
    }
    
    private void activateObeliskWithLightning(World world, BlockPos obeliskPos, String direction, BlockPos circlepos) {
        // Summon lightning at the obelisk
        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.setPosition(obeliskPos.getX() + 0.5, obeliskPos.getY() + 3, obeliskPos.getZ() + 0.5);
            world.spawnEntity(lightning);
        }
        
        // Play lightning sound
        world.playSound(null, obeliskPos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 1.0f, 1.0f);
        
        // Activate the obelisk
        world.setBlockState(obeliskPos, ModBlocks.ACTIVE_OBELISK.getDefaultState());

        // Set the linked ritual position for the obelisk
        BlockEntity blockEntity = world.getBlockEntity(obeliskPos);
        if (blockEntity instanceof ActiveObeliskBlockEntity activeObelisk) {
            activeObelisk.setLinkedRitual(circlepos);
        }

        // Spawn activation particles
        for (int i = 0; i < 20; i++) {
            double x = obeliskPos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
            double y = obeliskPos.getY() + 1.0 + world.random.nextDouble() * 3.0;
            double z = obeliskPos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
            
            double vx = (world.random.nextDouble() - 0.5) * 0.3;
            double vy = world.random.nextDouble() * 0.5;
            double vz = (world.random.nextDouble() - 0.5) * 0.3;
            
            world.addParticle(ParticleTypes.PORTAL, x, y, z, vx, vy, vz);
        }
    }
    
    private void spawnRitualActivationParticles(World world, BlockPos pos) {
        // Spawn a burst of magical particles at the circle center
        for (int i = 0; i < 50; i++) {
            double x = pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
            double y = pos.getY() + 0.1 + world.random.nextDouble() * 2.0;
            double z = pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
            
            double vx = (world.random.nextDouble() - 0.5) * 0.2;
            double vy = world.random.nextDouble() * 0.3;
            double vz = (world.random.nextDouble() - 0.5) * 0.2;
            
            world.addParticle(ParticleTypes.PORTAL, x, y, z, vx, vy, vz);
        }
    }

    private void startFinalRitualEffect(World world, BlockPos circlePos) {
        // Phase 1: Gather particles from obelisks to center
        // (Now handled by Lodestone particles in the renderer)
        
        // Phase 2: After gathering, shoot the giant beam
        world.scheduleBlockTick(circlePos, this, 200); // 10 seconds later (200 ticks)
    }
    
    private void shootGiantBeam(World world, BlockPos circlePos) {
        // Phase 2: Shoot a massive beam upward from the center
        // (Now handled by Lodestone particles in the renderer)
        
        // Play powerful beam sound
        world.playSound(null, circlePos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 3.0f, 0.5f);
        
        System.out.println("BarrierCircle: MASSIVE BEAM ACTIVATED! RITUAL COMPLETE!");
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.BARRIER_CIRCLE_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            // Block is being replaced/destroyed
            if (!world.isClient()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof BarrierCircleBlockEntity circleEntity) {
                    if (circleEntity.isRitualActive()) {
                        System.out.println("BarrierCircle: Circle broken during active ritual - deactivating!");
                        circleEntity.deactivateRitual();
                    }
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.BARRIER_CIRCLE_BLOCK_ENTITY, BarrierCircleBlockEntity::tick);
    }
} 