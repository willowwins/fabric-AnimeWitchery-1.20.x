package net.willowins.animewitchery.block.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransmutationPyreBlockEntity extends BlockEntity {
    private int woodCount = 0;
    private static final int MAX_WOOD = 128; // 2 stacks
    private boolean isActive = false;
    private int processingTicks = 0;
    private static final int PROCESS_TIME = 100; // 5 seconds of beam

    public TransmutationPyreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRANSMUTATION_PYRE_BLOCK_ENTITY, pos, state);
    }

    public ActionResult onUse(PlayerEntity player, Hand hand) {
        if (world == null || world.isClient)
            return ActionResult.SUCCESS;

        ItemStack stack = player.getStackInHand(hand);

        // Accept wood if not full and input is wood
        if (!isActive && woodCount < MAX_WOOD && (stack.isIn(ItemTags.LOGS) || stack.isIn(ItemTags.PLANKS))) {
            int needed = MAX_WOOD - woodCount;
            int toTake = Math.min(needed, stack.getCount());

            stack.decrement(toTake);
            woodCount += toTake;

            player.sendMessage(Text.of("Pyre: " + woodCount + "/" + MAX_WOOD + " Wood"), true);
            world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1f, 1f);

            markDirty();
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);

            // Auto-activate if full and multiblock is valid
            if (woodCount >= MAX_WOOD) {
                if (checkMultiblock()) {
                    startTransmutation();
                } else {
                    player.sendMessage(Text.of("Structure incomplete! Place 3x3 Magma Blocks below."), true);
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private boolean checkMultiblock() {
        if (world == null)
            return false;

        // Check 3x3 Magma Block platform directly below
        BlockPos centerBelow = pos.down();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = centerBelow.add(x, 0, z);
                if (!world.getBlockState(checkPos).isOf(Blocks.MAGMA_BLOCK)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void startTransmutation() {
        this.isActive = true;
        this.processingTicks = PROCESS_TIME;
        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TransmutationPyreBlockEntity entity) {
        if (world.isClient)
            return;

        if (entity.isActive) {
            entity.processingTicks--;

            // Sound effect every second
            if (entity.processingTicks % 20 == 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 1f, 1f);
            }

            if (entity.processingTicks <= 0) {
                entity.completeTransmutation();
            }
            // Mark dirty to ensure client knows about active state for rendering
            // (In a real mod, sync active state less frequently implies better performance,
            // but for beam visuals we need state synced)
        }
    }

    private void completeTransmutation() {
        this.isActive = false;
        this.woodCount = 0; // Consume all wood

        // Reward: 3 Random Ingots
        List<Item> rewards = new ArrayList<>();
        rewards.add(Items.IRON_INGOT);
        rewards.add(Items.GOLD_INGOT);
        rewards.add(Items.COPPER_INGOT);
        rewards.add(ModItems.SILVER); // Assuming ModItems.SILVER is the ingot

        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            Item reward = rewards.get(random.nextInt(rewards.size()));
            ItemStack drop = new ItemStack(reward);

            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, drop);
            itemEntity.setVelocity((random.nextDouble() - 0.5) * 0.2, 0.5, (random.nextDouble() - 0.5) * 0.2);
            world.spawnEntity(itemEntity);
        }

        world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1f, 1f);
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("woodCount", woodCount);
        nbt.putBoolean("isActive", isActive);
        nbt.putInt("processingTicks", processingTicks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.woodCount = nbt.getInt("woodCount");
        this.isActive = nbt.getBoolean("isActive");
        this.processingTicks = nbt.getInt("processingTicks");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
