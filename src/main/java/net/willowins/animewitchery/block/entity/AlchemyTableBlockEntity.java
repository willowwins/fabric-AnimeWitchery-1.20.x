package net.willowins.animewitchery.block.entity;

import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.recipe.AlchemyRecipe;
import net.willowins.animewitchery.recipe.ModRecipes;
import net.willowins.animewitchery.util.ImplementedInventory;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AlchemyTableBlockEntity extends BlockEntity
        implements GeoBlockEntity, ImplementedInventory, SidedInventory, ExtendedScreenHandlerFactory {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(11, ItemStack.EMPTY);

    private int progress = 0;
    private int maxProgress = 200;
    private boolean isProcessing = false;
    public boolean isActivated = false;
    private AlchemyRecipe currentRecipe = null;

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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 0,
                state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
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
    public @Nullable net.minecraft.screen.ScreenHandler createMenu(
            int syncId, net.minecraft.entity.player.PlayerInventory playerInventory, PlayerEntity player) {
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

    // === MAIN SERVER TICK LOGIC ===
    public static void tick(World world, BlockPos pos, BlockState state, AlchemyTableBlockEntity entity) {
        if (world.isClient) return;

        if (entity.isActivated && entity.hasRecipe()) {
            AlchemyRecipe recipe = entity.findRecipe();
            if (recipe != null && entity.currentRecipe != recipe) {
                entity.currentRecipe = recipe;
                entity.maxProgress = recipe.getProcessingTime();
            }

            entity.isProcessing = true;
            entity.progress++;

            if (world instanceof ServerWorld serverWorld) {
                spawnPortalParticles(serverWorld, pos, entity.progress, entity.maxProgress);
            }

            if (entity.progress >= entity.maxProgress) {
                entity.craftItem();
                entity.isActivated = false;
                world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL,
                        SoundCategory.BLOCKS, 1.0f, 1.5f);
                entity.sync();
            }
        } 
        // TEMPORARILY DISABLED: Spellbook combining
        /*else if (entity.isActivated && net.willowins.animewitchery.events.AlchemySpellbookHandler.canCombineSpellScroll(entity)) {
            // Spellbook + spell scroll combining
            if (entity.currentRecipe == null) {
                // Set up spellbook combining with fixed time
                entity.maxProgress = 200; // 10 seconds
            }

            entity.isProcessing = true;
            entity.progress++;

            if (world instanceof ServerWorld serverWorld) {
                spawnPortalParticles(serverWorld, pos, entity.progress, entity.maxProgress);
            }

            if (entity.progress >= entity.maxProgress) {
                // Process spellbook combining
                if (net.willowins.animewitchery.events.AlchemySpellbookHandler.tryProcessSpellbookCombining(entity, world)) {
                    entity.isActivated = false;
                    world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                            SoundCategory.BLOCKS, 1.0f, 1.5f);
                    entity.sync();
                }
            }
        }*/ else if (entity.isActivated && net.willowins.animewitchery.events.AlchemyEnchantmentHandler.canCombineEnchantments(entity)) {
            // Enchantment combining - use crafting animation
            if (entity.currentRecipe == null) {
                // Set up enchantment combining with fixed time
                entity.maxProgress = 300; // 15 seconds (15 * 20 ticks)
            }

            entity.isProcessing = true;
            entity.progress++;

            if (world instanceof ServerWorld serverWorld) {
                spawnPortalParticles(serverWorld, pos, entity.progress, entity.maxProgress);
            }

            if (entity.progress >= entity.maxProgress) {
                // Process enchantment combining
                if (net.willowins.animewitchery.events.AlchemyEnchantmentHandler.tryProcessEnchantmentCombining(entity, world)) {
                    entity.isActivated = false;
                    world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                            SoundCategory.BLOCKS, 1.0f, 1.0f);
                    entity.sync();
                }
            }
        } else if (entity.isActivated) {
            // No valid recipe or enchantment combining found
            entity.isProcessing = false;
            entity.progress = 0;
            entity.currentRecipe = null;
            entity.isActivated = false;
            entity.sync();
        } else {
            entity.isProcessing = false;
            entity.progress = 0;
            entity.currentRecipe = null;
        }

        entity.markDirty();
    }

    // === PARTICLE EFFECTS ===
    private static void spawnPortalParticles(ServerWorld world, BlockPos pos, int progress, int maxProgress) {
        Vec3d center = Vec3d.ofCenter(pos);
        float intensity = (float) progress / maxProgress;

        if (intensity >= 0.5f) {
            spawnInwardSpiralParticles(world, center, intensity);
        } else {
            int particleCount = (int) (8 + (intensity * 12));
            for (int i = 0; i < particleCount; i++) {
                double angle = (world.getTime() * 0.03) + (i * Math.PI * 2 / particleCount);
                double radius = 1.5 + (intensity * 2.0);

                double y = center.y + 0.7 + Math.sin(world.getTime() * 0.01) * 0.2;
                double x = center.x + Math.cos(angle) * radius;
                double z = center.z + Math.sin(angle) * radius;

                world.spawnParticles(ParticleTypes.ENCHANT, x, y, z, 5, 0.1, 0.05, 0.1, 0.02);
            }
        }
    }

    private static void spawnInwardSpiralParticles(ServerWorld world, Vec3d center, float intensity) {
        Vec3d floorCenter = new Vec3d(center.x, center.y - 0.3, center.z);
        double time = world.getTime() * 0.08;
        double radius = 3.0 - (intensity - 0.5f) * 5.0;

        // Both rotate clockwise, offset by π
        double angle1 = time;
        double angle2 = time + Math.PI;

        double x1 = floorCenter.x + Math.cos(angle1) * radius;
        double z1 = floorCenter.z + Math.sin(angle1) * radius;
        double y1 = floorCenter.y + Math.sin(time * 0.2) * 0.15;

        double x2 = floorCenter.x + Math.cos(angle2) * radius;
        double z2 = floorCenter.z + Math.sin(angle2) * radius;
        double y2 = floorCenter.y + Math.sin(time * 0.2) * 0.15;

        // Both follow the same rotational direction
        world.spawnParticles(ParticleTypes.END_ROD, x1, y1, z1, 8, 0.2, 0.1, 0.2, 0.05);
        world.spawnParticles(ParticleTypes.DRAGON_BREATH, x2, y2, z2, 8, 0.2, 0.1, 0.2, 0.05);
    }


    // === RECIPE LOGIC ===
    private boolean hasRecipe() {
        AlchemyRecipe recipe = findRecipe();
        if (recipe == null) return false;

        ItemStack output = inventory.get(0);
        ItemStack result = recipe.getOutput(null);

        if (output.isEmpty()) return true;
        if (!ItemStack.areItemsEqual(output, result)) return false;
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

        // Produce output
        ItemStack result = recipe.getOutput(null);
        ItemStack output = inventory.get(0);

        if (output.isEmpty()) {
            inventory.set(0, result.copy());
        } else if (ItemStack.areItemsEqual(output, result)) {
            output.increment(result.getCount());
        }

        progress = 0;
        isProcessing = false;
        currentRecipe = null;

        // Ensure inventory and visuals sync immediately
        markDirty();
        sync();

        if (world != null && !world.isClient) {
            // Force client re-render and BlockEntity update packet
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);

            // Send a manual update packet for this block entity’s NBT
            world.getServer().execute(() -> {
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.getChunkManager().markForUpdate(pos);
                }
            });
        }
    }


    private void sync() {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(pos);
        }
    }

    // === INVENTORY INTERACTION ===
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return pos.isWithinDistance(player.getBlockPos(), 4.5);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP)
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        else
            return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot >= 0 && slot <= 10;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 0;
    }

    // === ACTIVATION ===
    public boolean activateWithCatalyst(PlayerEntity player) {
        if (!isActivated) {
            // --- NEW: Check if player holds a *full* catalyst ---
            ItemStack held = player.getMainHandStack();
            if (!(held.getItem() instanceof net.willowins.animewitchery.item.custom.AlchemicalCatalystItem catalyst)) {
                // Try offhand instead if main hand isn't a catalyst
                held = player.getOffHandStack();
                if (!(held.getItem() instanceof net.willowins.animewitchery.item.custom.AlchemicalCatalystItem))
                    return false;
            }

            int storedMana = net.willowins.animewitchery.item.custom.AlchemicalCatalystItem.getStoredMana(held);
            int maxMana = net.willowins.animewitchery.item.custom.AlchemicalCatalystItem.MAX_MANA;

            if (storedMana < maxMana) {
                player.sendMessage(
                        Text.literal("⚠️ The catalyst must brim with power before alchemy may begin.")
                                .formatted(Formatting.GRAY),
                        true
                );
                return false;
            }

            // Check for valid recipe OR enchantment combining (spellbook combining TEMPORARILY DISABLED)
            boolean hasValidRecipe = hasRecipe();
            //boolean hasSpellbookCombining = net.willowins.animewitchery.events.AlchemySpellbookHandler.canCombineSpellScroll(this);
            boolean hasEnchantmentCombining = net.willowins.animewitchery.events.AlchemyEnchantmentHandler.canCombineEnchantments(this);
            
            if (hasValidRecipe || /*hasSpellbookCombining ||*/ hasEnchantmentCombining) {
                int xpCost = hasValidRecipe ? getCurrentRecipeXpCost() : 5; // 5 XP for enchantments (spellbook disabled)

                // --- If catalyst full and player has XP, proceed ---
                if (player.experienceLevel >= xpCost) {
                    player.addExperienceLevels(-xpCost);
                    isActivated = true;
                    progress = 0;

                    if (hasValidRecipe) {
                        player.sendMessage(
                                Text.literal("✨ The table hums to life as the catalyst discharges its energy.")
                                        .formatted(Formatting.DARK_AQUA),
                                true
                        );
                    } /*else if (hasSpellbookCombining) {
                        player.sendMessage(
                                Text.literal("✨ Arcane knowledge flows from scroll to book...")
                                        .formatted(Formatting.LIGHT_PURPLE),
                                true
                        );
                        player.sendMessage(
                                Text.literal("§7Processing time: 10 seconds")
                                        .formatted(Formatting.GRAY),
                                true
                        );
                    }*/ else {
                        player.sendMessage(
                                Text.literal("✨ Enchantment energies swirl as the catalyst activates the table.")
                                        .formatted(Formatting.DARK_PURPLE),
                                true
                        );
                        player.sendMessage(
                                Text.literal("§7Processing time: 15 seconds")
                                        .formatted(Formatting.GRAY),
                                true
                        );
                    }

                    markDirty();
                    sync();
                    return true;
                } else {
                    player.sendMessage(
                            Text.literal("You lack the experience to command the subvoid's flow.")
                                    .formatted(Formatting.RED),
                            true
                    );
                }
            } else {
                player.sendMessage(
                        Text.literal("§7No valid alchemy or enchantment combination found.")
                                .formatted(Formatting.GRAY),
                        true
                );
            }
        }
        return false;
    }

    public int getCurrentRecipeXpCost() {
        if (hasRecipe()) {
            AlchemyRecipe recipe = findRecipe();
            if (recipe != null) return recipe.getXpCost();
        }
        return 0;
    }
    public boolean isProcessing() {
        return isProcessing;
    }

}
