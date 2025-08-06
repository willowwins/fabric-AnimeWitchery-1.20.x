package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.recipe.AlchemyRecipe;
import net.willowins.animewitchery.recipe.ModRecipes;
import net.willowins.animewitchery.util.ImplementedInventory;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AlchemyTableBlockEntity extends BlockEntity implements GeoBlockEntity, ImplementedInventory, SidedInventory, ExtendedScreenHandlerFactory {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    // Inventory slots: 0=Output (center), 1-10=Input slots (counter-clockwise from top)
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(11, ItemStack.EMPTY);
    
    // Progress tracking
    private int progress = 0;
    private int maxProgress = 200; // Default 10 seconds
    private boolean isProcessing = false;
    private boolean isActivated = false; // Whether the table has been activated with catalyst
    private AlchemyRecipe currentRecipe = null;
    
    // Property delegate for syncing progress to client
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> isProcessing ? 1 : 0;
                case 3 -> isActivated ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                case 2 -> isProcessing = value == 1;
                case 3 -> isActivated = value == 1;
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public AlchemyTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALCHEMY_TABLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Idle", 0, state -> {
            // TODO: Add processing animation when available
            // if (isProcessing) {
            //     return state.setAndContinue(RawAnimation.begin().thenLoop("processing"));
            // } else {
            //     return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            // }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.animewitchery.alchemy_table");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public @Nullable net.minecraft.screen.ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory playerInventory, PlayerEntity player) {
        return new net.willowins.animewitchery.screen.AlchemyTableScreenHandler(syncId, playerInventory, this);
    }

    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("progress", progress);
        nbt.putInt("maxProgress", maxProgress);
        nbt.putBoolean("isProcessing", isProcessing);
        nbt.putBoolean("isActivated", isActivated);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("progress");
        maxProgress = nbt.getInt("maxProgress");
        isProcessing = nbt.getBoolean("isProcessing");
        isActivated = nbt.getBoolean("isActivated");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("progress", progress);
        nbt.putInt("maxProgress", maxProgress);
        nbt.putBoolean("isProcessing", isProcessing);
        nbt.putBoolean("isActivated", isActivated);
        return nbt;
    }

    public static void tick(World world, BlockPos pos, BlockState state, AlchemyTableBlockEntity entity) {
        if (world.isClient) return;

        // Only process if activated and we have a valid recipe
        if (entity.isActivated && entity.hasRecipe()) {
            // Get the current recipe and update max progress
            AlchemyRecipe recipe = entity.findRecipe();
            if (recipe != null && entity.currentRecipe != recipe) {
                entity.currentRecipe = recipe;
                entity.maxProgress = recipe.getProcessingTime();
            }
            
            entity.isProcessing = true;
            entity.progress++;
            
            // Spawn portal particles when processing
            if (world instanceof ServerWorld serverWorld) {
                spawnPortalParticles(serverWorld, pos, entity.progress, entity.maxProgress);
            }
            
            if (entity.progress >= entity.maxProgress) {
                entity.craftItem();
                entity.isActivated = false; // Deactivate after crafting
            }
        } else {
            entity.isProcessing = false;
            entity.progress = 0;
            entity.currentRecipe = null;
        }
        
        entity.markDirty();
    }

    private static void spawnPortalParticles(ServerWorld world, BlockPos pos, int progress, int maxProgress) {
        // Calculate center position of the block (slightly above)
        Vec3d center = Vec3d.ofCenter(pos).add(0, 0.8, 0);
        
        // Calculate intensity based on progress (more particles as it gets closer to completion)
        float intensity = (float) progress / maxProgress;
        
        // Check if we're in the final 50% for the inward spiral effect
        if (intensity >= 0.5f) {
            // Final 50% - inward spiral with trailing particles
            spawnInwardSpiralParticles(world, center, intensity);
        } else {
            // Normal swirling pattern for first 50% - MUCH BIGGER!
            int particleCount = (int) (8 + (intensity * 12)); // 8-20 particles based on progress
            
            // Spawn portal particles in a swirling pattern around the center
            for (int i = 0; i < particleCount; i++) {
                double angle = (world.getTime() * 0.03) + (i * Math.PI * 2 / particleCount); // Slower rotation
                double radius = 1.5 + (intensity * 2.0); // Much bigger radius: 1.5 to 3.5
                
                double x = center.x + Math.cos(angle) * radius;
                double y = center.y + Math.sin(world.getTime() * 0.01) * 0.5; // Slower vertical movement
                double z = center.z + Math.sin(angle) * radius;
                
                // Spawn multiple portal particles for more visibility and persistence
                world.spawnParticles(ParticleTypes.PORTAL, x, y, z, 5, 0.1, 0.1, 0.1, 0.02);
            }
        }
    }
    
    private static void spawnInwardSpiralParticles(ServerWorld world, Vec3d center, float intensity) {
        // Two portal particles spiraling inward with trails - MUCH BIGGER!
        double time = world.getTime() * 0.08; // Slower rotation for dramatic effect
        
        // Calculate inward spiral radius (starts at 3.0, goes to 0.5) - adjusted for 50% threshold
        double radius = 3.0 - (intensity - 0.5f) * 5.0; // Inward spiral effect over 50% of progress
        
        // First particle (clockwise spiral)
        double angle1 = time;
        double x1 = center.x + Math.cos(angle1) * radius;
        double y1 = center.y + Math.sin(time * 0.2) * 0.3;
        double z1 = center.z + Math.sin(angle1) * radius;
        
        // Second particle (counter-clockwise spiral)
        double angle2 = -time + Math.PI; // Offset by PI for opposite direction
        double x2 = center.x + Math.cos(angle2) * radius;
        double y2 = center.y + Math.sin(-time * 0.2) * 0.3;
        double z2 = center.z + Math.sin(angle2) * radius;
        
        // Spawn main particles with multiple portal particles for visibility and persistence
        world.spawnParticles(ParticleTypes.PORTAL, x1, y1, z1, 8, 0.2, 0.2, 0.2, 0.05);
        world.spawnParticles(ParticleTypes.PORTAL, x2, y2, z2, 8, 0.2, 0.2, 0.2, 0.05);
        
        // Spawn trail particles (smaller, more transparent effect)
        for (int i = 1; i <= 5; i++) {
            double trailRadius1 = radius + (i * 0.2);
            double trailRadius2 = radius + (i * 0.2);
            
            double trailX1 = center.x + Math.cos(angle1 - (i * 0.3)) * trailRadius1;
            double trailY1 = center.y + Math.sin((time - (i * 0.3)) * 0.2) * 0.3;
            double trailZ1 = center.z + Math.sin(angle1 - (i * 0.3)) * trailRadius1;
            
            double trailX2 = center.x + Math.cos(angle2 + (i * 0.3)) * trailRadius2;
            double trailY2 = center.y + Math.sin((-time + (i * 0.3)) * 0.2) * 0.3;
            double trailZ2 = center.z + Math.sin(angle2 + (i * 0.3)) * trailRadius2;
            
            // Spawn trail particles with more persistence
            world.spawnParticles(ParticleTypes.PORTAL, trailX1, trailY1, trailZ1, 3, 0.1, 0.1, 0.1, 0.02);
            world.spawnParticles(ParticleTypes.PORTAL, trailX2, trailY2, trailZ2, 3, 0.1, 0.1, 0.1, 0.02);
        }
    }

    private boolean hasRecipe() {
        // Find a matching recipe
        AlchemyRecipe recipe = findRecipe();
        if (recipe == null) {
            return false;
        }
        
        // Check if output slot can accept the result
        ItemStack output = inventory.get(0);
        ItemStack result = recipe.getOutput(null);
        
        if (output.isEmpty()) {
            return true;
        }
        
        if (!ItemStack.areItemsEqual(output, result)) {
            return false;
        }
        
        return output.getCount() + result.getCount() <= output.getMaxCount();
    }
    
    private AlchemyRecipe findRecipe() {
        if (world == null) return null;
        
        return world.getRecipeManager()
                .getFirstMatch(ModRecipes.ALCHEMY_RECIPE_TYPE, this, world)
                .orElse(null);
    }

    private void craftItem() {
        AlchemyRecipe recipe = findRecipe();
        if (recipe == null) return;
        
        // Consume ingredients
        recipe.consumeIngredients(this);
        
        // Create result
        ItemStack result = recipe.getOutput(null);
        ItemStack output = inventory.get(0);
        
        if (output.isEmpty()) {
            inventory.set(0, result.copy());
        } else {
            output.increment(result.getCount());
        }
        
        progress = 0;
        isProcessing = false;
        currentRecipe = null;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return pos.isWithinDistance(player.getBlockPos(), 4.5);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        // Input slots (1-10) can be accessed from any side
        // Output slot (0) can only be accessed from the top
        if (side == Direction.UP) {
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        } else {
            return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        }
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        // Only allow insertion into input slots (1-10)
        return slot >= 1 && slot <= 10;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        // Allow extraction from output slot (0)
        return slot == 0;
    }

    public boolean activateWithCatalyst(PlayerEntity player) {
        if (!isActivated && hasRecipe()) {
            AlchemyRecipe recipe = findRecipe();
            if (recipe != null) {
                int xpCost = recipe.getXpCost();
                
                // Check if player has enough XP
                if (player.experienceLevel >= xpCost) {
                    // Consume XP
                    player.addExperienceLevels(-xpCost);
                    
                    isActivated = true;
                    progress = 0;
                    markDirty();
                    return true;
                }
            }
        }
        return false;
    }
    
    public int getCurrentRecipeXpCost() {
        if (hasRecipe()) {
            AlchemyRecipe recipe = findRecipe();
            if (recipe != null) {
                return recipe.getXpCost();
            }
        }
        return 0;
    }
}
