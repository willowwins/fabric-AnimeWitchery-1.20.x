package net.willowins.animewitchery.block.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.willowins.animewitchery.networking.ModPackets;
import net.willowins.animewitchery.sound.ModSounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import net.willowins.animewitchery.world.ObeliskBreakState;
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

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 80);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            // Give the player slowness and blindness effects
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.SLOWNESS, 
                200, // 10 seconds (200 ticks)
                10    // Level 2 slowness
            ));
            
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.BLINDNESS, 
                100, // 5 seconds (100 ticks)
                10    // Level 1 blindness
            ));
            
            // Display a title message to the player
            String[] messages = {
                "§c§lBreak the seal, and you break the world.",
                "§7§oYou are not ready. Yet you proceed...",
                "§7§oYou hear your name... but no one speaks it.",
            };
            
            String selectedMessage = messages[world.random.nextInt(messages.length)];
            player.sendMessage(net.minecraft.text.Text.literal(selectedMessage), true);
            
            // Send as title for center screen display
            //            ((net.minecraft.server.network.ServerPlayerEntity) player).networkHandler.sendPacket(
            //                new TitleS2CPacket(Text.literal(selectedMessage))
            //            );

            // depending on the message, play a different sound
            if (selectedMessage.equals("§c§lBreak the seal, and you break the world.")) {
                world.playSound(null, pos, ModSounds.OBELISK_MESSAGE1, SoundCategory.AMBIENT, 1f, 1f);
            } else if (selectedMessage.equals("§7§oYou are not ready. Yet you proceed...")) {
                world.playSound(null, pos, ModSounds.OBELISK_MESSAGE2, SoundCategory.AMBIENT, 1f, 1f);
            } 
            
            
            // Play a spooky sound
            world.playSound(null, pos, net.minecraft.sound.SoundEvents.ENTITY_ENDERMAN_TELEPORT, 
                SoundCategory.AMBIENT, 0.5f, 0.8f);
            
            // Spawn some particles for effect
            for (int i = 0; i < 20; i++) {
                double x = pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                double y = pos.getY() + 1.0 + world.random.nextDouble() * 3.0;
                double z = pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.1, 0);
            }
        }
        
        return ActionResult.success(world.isClient);
    }



    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        // Play the global obelisk break sound only once per world
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
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

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.OBELISK_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.OBELISK_BLOCK_ENTITY, ObeliskBlockEntity::tick);
    }
} 