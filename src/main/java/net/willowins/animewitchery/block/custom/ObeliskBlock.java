package net.willowins.animewitchery.block.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.networking.ModPackets;
import net.willowins.animewitchery.sound.ModSounds;
import net.willowins.animewitchery.world.ObeliskBreakState;
import net.willowins.animewitchery.world.ObeliskRegistry;
import net.willowins.animewitchery.world.ObeliskWorldListener;
import org.jetbrains.annotations.Nullable;

public class ObeliskBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 32, 16);

    public ObeliskBlock(Settings settings) {
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

    /* === Registry Integration === */
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            ObeliskRegistry.get(serverWorld).register(pos);
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (world instanceof ServerWorld serverWorld) {
            // Remove from registry
            ObeliskRegistry.get(serverWorld).unregister(pos);

            // Handle global “first break” effect
            ObeliskBreakState breakState = ObeliskBreakState.get(serverWorld);
            if (!breakState.hasPlayedOnce()) {
                breakState.markAsPlayed();

                for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                    player.playSound(ModSounds.OBELISK_BREAK_ONCE, SoundCategory.AMBIENT, 1.0f, 1.0f);
                    player.networkHandler.sendPacket(new TitleS2CPacket(
                            Text.literal("§c§lHe is now aware...")
                    ));
                    ServerPlayNetworking.send(player, ModPackets.OBELISK_SHAKE, PacketByteBufs.create());
                }
            }
        }
        super.onBroken(world, pos, state);
    }

    /* === Interaction Logic === */
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            // Handle chisel usage
            if (player.getStackInHand(hand).isOf(ModItems.CHISEL)) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof ObeliskBlockEntity obelisk) {
                    obelisk.cycleTextureVariant();
                    world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 0.5f, 1.2f);
                    player.sendMessage(Text.literal("§7§oThe obelisk's surface shifts to reveal a new pattern..."), true);
                    return ActionResult.SUCCESS;
                }
            }

            // Apply visual and auditory effects
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SLOWNESS, 200, 10));
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.BLINDNESS, 100, 10));

            String[] messages = {
                    "§c§lBreak the seal, and you break the world.",
                    "§7§oYou are not ready. Yet you proceed...",
                    "§7§oYou hear your name... but no one speaks it."
            };
            String selectedMessage = messages[world.random.nextInt(messages.length)];
            player.sendMessage(Text.literal(selectedMessage), true);

            // Play corresponding sound
            if (selectedMessage.equals(messages[0])) {
                world.playSound(null, pos, ModSounds.OBELISK_MESSAGE1, SoundCategory.AMBIENT, 1f, 1f);
            } else if (selectedMessage.equals(messages[1])) {
                world.playSound(null, pos, ModSounds.OBELISK_MESSAGE2, SoundCategory.AMBIENT, 1f, 1f);
            }

            // Ambient effect
            world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.AMBIENT, 0.5f, 0.8f);

            // Smoke particles
            for (int i = 0; i < 20; i++) {
                double x = pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                double y = pos.getY() + 1.0 + world.random.nextDouble() * 3.0;
                double z = pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.1, 0);
            }
        }

        return ActionResult.success(world.isClient);
    }

    /* === Block Entity Handling === */
    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.OBELISK_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.OBELISK_BLOCK_ENTITY, ObeliskBlockEntity::tick);
    }
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        if (!world.isClient && world instanceof ServerWorld sw) {
            // Queue registration for this obelisk
            net.willowins.animewitchery.world.ObeliskWorldListener.queueRegister(sw, pos);
        }

        // Keep your tick schedule logic
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 80);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient && !state.isOf(newState.getBlock()) && world instanceof ServerWorld sw) {
            net.willowins.animewitchery.world.ObeliskRegistry.get(sw).unregister(pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

}
