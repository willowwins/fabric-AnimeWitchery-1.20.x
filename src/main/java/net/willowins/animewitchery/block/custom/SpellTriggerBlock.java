package net.willowins.animewitchery.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.SpellTriggerBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellTriggerBlock extends BlockWithEntity {
    
    public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");
    
    public SpellTriggerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ACTIVATED, false));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }
    
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        // Work like a pressure plate - check every tick if entities are on top
        if (!world.isClient) {
            this.checkPressed(world, pos, state);
        }
    }
    
    /**
     * Checks if entities are standing on the block (like a pressure plate)
     */
    private void checkPressed(World world, BlockPos pos, BlockState state) {
        // Get entities standing on the block
        Box box = Box.from(Vec3d.ofBottomCenter(pos)).expand(0.125, 0.001, 0.125).stretch(0, 0.25, 0);
        List<? extends Entity> entities = world.getNonSpectatingEntities(Entity.class, box);
        
        boolean shouldBeActivated = !entities.isEmpty();
        boolean isActivated = state.get(ACTIVATED);
        
        if (shouldBeActivated && !isActivated) {
            // Activate the spell trigger
            if (world.getBlockEntity(pos) instanceof SpellTriggerBlockEntity blockEntity) {
                if (blockEntity.hasSpell()) {
                    // Find a player entity to cast from
                    PlayerEntity player = null;
                    for (Entity entity : entities) {
                        if (entity instanceof PlayerEntity p) {
                            player = p;
                            break;
                        }
                    }
                    
                    if (player != null) {
                        String spell = blockEntity.getEncodedSpell();
                        player.sendMessage(Text.translatable("message.animewitchery.spell_trigger_activated", spell), true);
                        
                        // Cast the spell from the player's position
                        net.willowins.animewitchery.util.SpellCaster.castSpell(player, world, spell);
                        
                        AnimeWitchery.LOGGER.info("Spell trigger block activated with spell: {} by player: {}", 
                            spell, player.getName().getString());
                    }
                }
            }
            
            world.setBlockState(pos, state.with(ACTIVATED, true));
            world.scheduleBlockTick(pos, this, 20); // 1 second delay before it can trigger again
        } else if (!shouldBeActivated && isActivated) {
            // Deactivate when no entities
            world.setBlockState(pos, state.with(ACTIVATED, false));
        }
    }
    
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (state.get(ACTIVATED)) {
            world.setBlockState(pos, state.with(ACTIVATED, false));
        }
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, 
                             Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.PASS;
        }
        
        ItemStack heldItem = player.getStackInHand(hand);
        
        // Check if player is trying to encode a spell with a wand
        if (heldItem.getItem().toString().contains("wand") && heldItem.hasNbt()) {
            NbtCompound nbt = heldItem.getNbt();
            if (nbt.contains("encoded_spell")) {
                String spell = nbt.getString("encoded_spell");
                
                if (world.getBlockEntity(pos) instanceof SpellTriggerBlockEntity blockEntity) {
                    blockEntity.encodeSpell(spell);
                    player.sendMessage(Text.translatable("message.animewitchery.spell_trigger_encoded", spell), true);
                    return ActionResult.SUCCESS;
                }
            }
        }
        
        // Show current spell if any
        if (world.getBlockEntity(pos) instanceof SpellTriggerBlockEntity blockEntity) {
            if (blockEntity.hasSpell()) {
                String spell = blockEntity.getEncodedSpell();
                player.sendMessage(Text.translatable("message.animewitchery.spell_trigger_current_spell", spell), true);
            } else {
                player.sendMessage(Text.translatable("message.animewitchery.spell_trigger_no_spell"), true);
            }
        }
        
        return ActionResult.PASS;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SpellTriggerBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
