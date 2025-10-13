package net.willowins.animewitchery.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.KineticBladeHitboxEntity;
import net.willowins.animewitchery.item.ModToolMaterial;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class KineticBladeItem extends MiningToolItem {

    private static final float BASE_DAMAGE = 1.0f;
    private static final int BOOST_MANA_COST = 500;
    private static final Map<PlayerEntity, KineticBladeHitboxEntity> ACTIVE_HITBOXES = new WeakHashMap<>();

    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("fa233e1c-4180-4865-b01b-bcce9785aca3");
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("22653b89-116e-49dc-9b6b-9971489b5be5");

    public KineticBladeItem(Settings settings) {
        super(1.0f, -3.4f, net.willowins.animewitchery.item.ModToolMaterial.RESONANT, BlockTags.PICKAXE_MINEABLE, settings);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        // Make it effective on both pickaxe and axe blocks
        if (state.isIn(BlockTags.PICKAXE_MINEABLE) || state.isIn(BlockTags.AXE_MINEABLE)) {
            return this.miningSpeed;
        }
        return super.getMiningSpeedMultiplier(stack, state);
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        // Allow mining both pickaxe and axe blocks
        return state.isIn(BlockTags.PICKAXE_MINEABLE) || state.isIn(BlockTags.AXE_MINEABLE);
    }

    // === Right-click block functionality ===
    @Override
    public net.minecraft.util.ActionResult useOnBlock(net.minecraft.item.ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        
        if (player == null) return net.minecraft.util.ActionResult.PASS;

        // Check for bedrock breaking first (highest priority)
        if (state.isOf(net.minecraft.block.Blocks.BEDROCK)) {
            // Check cooldown
            if (player.getItemCooldownManager().isCoolingDown(this)) {
                if (!world.isClient) {
                    player.sendMessage(Text.literal("§7⏳ Kinetic systems still cooling down..."), true);
                }
                return net.minecraft.util.ActionResult.FAIL;
            }
            
            if (!world.isClient) {
                // Manually drop bedrock item
                net.minecraft.block.Block.dropStack(world, pos, new ItemStack(net.minecraft.block.Blocks.BEDROCK));
                
                // Break bedrock block
                world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
                
                // Play powerful break sound
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.8f, 1.2f);
                world.playSound(null, pos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0f, 0.5f);
                
                // Damage tool
                context.getStack().damage(5, player, (p) -> p.sendToolBreakStatus(context.getHand()));
                
                // Apply 30 second cooldown (600 ticks)
                player.getItemCooldownManager().set(this, 600);
                
                player.sendMessage(Text.literal("§b⚡ Bedrock shattered!"), true);
            }
            
            return net.minecraft.util.ActionResult.success(world.isClient);
        }

        // Only strip wood when not gliding
        if (player.isFallFlying()) {
            return net.minecraft.util.ActionResult.PASS;
        }

        // Get the stripped version of the block
        BlockState strippedState = getStrippedState(state);
        
        if (strippedState != null) {
            // Play the stripping sound
            world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            
            if (!world.isClient) {
                // Set the stripped block
                world.setBlockState(pos, strippedState, 11);
                
                // Damage the tool
                context.getStack().damage(1, player, (p) -> p.sendToolBreakStatus(context.getHand()));
            }
            
            return net.minecraft.util.ActionResult.success(world.isClient);
        }
        
        return net.minecraft.util.ActionResult.PASS;
    }

    // Map of logs/wood to their stripped versions
    private BlockState getStrippedState(BlockState state) {
        Block block = state.getBlock();
        
        // Vanilla logs
        if (block == net.minecraft.block.Blocks.OAK_LOG) return net.minecraft.block.Blocks.STRIPPED_OAK_LOG.getDefaultState();
        if (block == net.minecraft.block.Blocks.SPRUCE_LOG) return net.minecraft.block.Blocks.STRIPPED_SPRUCE_LOG.getDefaultState();
        if (block == net.minecraft.block.Blocks.BIRCH_LOG) return net.minecraft.block.Blocks.STRIPPED_BIRCH_LOG.getDefaultState();
        if (block == net.minecraft.block.Blocks.JUNGLE_LOG) return net.minecraft.block.Blocks.STRIPPED_JUNGLE_LOG.getDefaultState();
        if (block == net.minecraft.block.Blocks.ACACIA_LOG) return net.minecraft.block.Blocks.STRIPPED_ACACIA_LOG.getDefaultState();
        if (block == net.minecraft.block.Blocks.DARK_OAK_LOG) return net.minecraft.block.Blocks.STRIPPED_DARK_OAK_LOG.getDefaultState();
        if (block == net.minecraft.block.Blocks.MANGROVE_LOG) return net.minecraft.block.Blocks.STRIPPED_MANGROVE_LOG.getDefaultState();
        if (block == net.minecraft.block.Blocks.CHERRY_LOG) return net.minecraft.block.Blocks.STRIPPED_CHERRY_LOG.getDefaultState();
        
        // Vanilla wood blocks
        if (block == net.minecraft.block.Blocks.OAK_WOOD) return net.minecraft.block.Blocks.STRIPPED_OAK_WOOD.getDefaultState();
        if (block == net.minecraft.block.Blocks.SPRUCE_WOOD) return net.minecraft.block.Blocks.STRIPPED_SPRUCE_WOOD.getDefaultState();
        if (block == net.minecraft.block.Blocks.BIRCH_WOOD) return net.minecraft.block.Blocks.STRIPPED_BIRCH_WOOD.getDefaultState();
        if (block == net.minecraft.block.Blocks.JUNGLE_WOOD) return net.minecraft.block.Blocks.STRIPPED_JUNGLE_WOOD.getDefaultState();
        if (block == net.minecraft.block.Blocks.ACACIA_WOOD) return net.minecraft.block.Blocks.STRIPPED_ACACIA_WOOD.getDefaultState();
        if (block == net.minecraft.block.Blocks.DARK_OAK_WOOD) return net.minecraft.block.Blocks.STRIPPED_DARK_OAK_WOOD.getDefaultState();
        if (block == net.minecraft.block.Blocks.MANGROVE_WOOD) return net.minecraft.block.Blocks.STRIPPED_MANGROVE_WOOD.getDefaultState();
        if (block == net.minecraft.block.Blocks.CHERRY_WOOD) return net.minecraft.block.Blocks.STRIPPED_CHERRY_WOOD.getDefaultState();
        
        // Nether stems
        if (block == net.minecraft.block.Blocks.CRIMSON_STEM) return net.minecraft.block.Blocks.STRIPPED_CRIMSON_STEM.getDefaultState();
        if (block == net.minecraft.block.Blocks.WARPED_STEM) return net.minecraft.block.Blocks.STRIPPED_WARPED_STEM.getDefaultState();
        if (block == net.minecraft.block.Blocks.CRIMSON_HYPHAE) return net.minecraft.block.Blocks.STRIPPED_CRIMSON_HYPHAE.getDefaultState();
        if (block == net.minecraft.block.Blocks.WARPED_HYPHAE) return net.minecraft.block.Blocks.STRIPPED_WARPED_HYPHAE.getDefaultState();
        
        // Bamboo
        if (block == net.minecraft.block.Blocks.BAMBOO_BLOCK) return net.minecraft.block.Blocks.STRIPPED_BAMBOO_BLOCK.getDefaultState();
        
        return null;
    }

    // === Right-click behavior ===
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // If not gliding, allow normal use (for wood stripping via useOnBlock)
        if (!player.isFallFlying()) {
            return TypedActionResult.pass(stack);
        }

        if (!world.isClient) {
            // Prevent duplicate activation
            if (ACTIVE_HITBOXES.containsKey(player)) {
                return TypedActionResult.fail(stack);
            }

            // Play activation sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.2f, 1.2f);

            // If flying and holding Mana Rocket — boost
            if (hasManaRocketInOffhand(player)) {
                triggerManaRocketBoost(world, player);
            }

            // Spawn hitbox entity
            KineticBladeHitboxEntity hitbox = new KineticBladeHitboxEntity(world, player);
            world.spawnEntity(hitbox);
            ACTIVE_HITBOXES.put(player, hitbox);
        }

        player.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        if (world.isClient) return;

        KineticBladeHitboxEntity hitbox = ACTIVE_HITBOXES.get(player);
        if (hitbox != null && !hitbox.isRemoved()) hitbox.updatePositionToPlayer(player);

        // Spiral particle trail
        if (player instanceof ServerPlayerEntity sp) {
            Vec3d look = player.getRotationVector().normalize();
            Vec3d start = player.getEyePos();

            double spiralRadius = 2.2;
            double spiralLength = 3.5;
            double spinSpeed = 0.35;
            double angleOffset = player.age * spinSpeed;
            int coils = 4;
            int particlesPerCoil = 12;

            for (int coil = 0; coil < coils; coil++) {
                double coilProgress = (double) coil / coils;
                double distance = spiralLength * coilProgress;
                Vec3d basePoint = start.add(look.multiply(distance));

                for (int i = 0; i < particlesPerCoil; i++) {
                    double theta = (i / (double) particlesPerCoil) * 2 * Math.PI + angleOffset;
                    double radius = spiralRadius * (1.0 - coilProgress * 0.25);
                    double xOff = Math.cos(theta) * radius;
                    double yOff = Math.sin(theta) * radius;

                    Vec3d right = new Vec3d(look.z, 0, -look.x).normalize();
                    Vec3d up = look.crossProduct(right).normalize();
                    Vec3d particlePos = basePoint.add(right.multiply(xOff)).add(up.multiply(yOff));

                    sp.getServerWorld().spawnParticles(
                            ParticleTypes.END_ROD, particlePos.x, particlePos.y, particlePos.z,
                            1, 0, 0, 0, 0
                    );
                }
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        // Remove the active hitbox
        KineticBladeHitboxEntity hitbox = ACTIVE_HITBOXES.remove(player);
        if (hitbox != null && !hitbox.isRemoved()) hitbox.discard();

        if (!world.isClient) {
            // Play deactivation sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.7f, 0.8f);

            // ⏳ Apply a 5-second cooldown (100 ticks)
            player.getItemCooldownManager().set(this, 100);

            // Optional feedback message
            if (player instanceof ServerPlayerEntity sp) {
                sp.sendMessage(Text.literal("§7⏳ Kinetic systems cooling down..."), true);
            }
        }
    }
    private void triggerManaRocketBoost(World world, PlayerEntity player) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        if (mana.getMana() < BOOST_MANA_COST) {
            if (player instanceof ServerPlayerEntity sp)
                sp.sendMessage(Text.literal("§7§oNot enough mana."), true);
            return;
        }

        mana.consume(BOOST_MANA_COST);
        ManaRocketItem.doRocketEffect(world, player);
        if (player instanceof ServerPlayerEntity sp)
            sp.sendMessage(Text.literal("§dMana Rocket Boost!"), true);
    }

    private boolean hasManaRocketInOffhand(PlayerEntity player) {
        return player.getOffHandStack().getItem() instanceof ManaRocketItem;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    public static boolean shouldCancelFallDamage(PlayerEntity p, DamageSource s) {
        return s == p.getWorld().getDamageSources().fall()
                || s == p.getWorld().getDamageSources().flyIntoWall();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof PlayerEntity player)) return super.postHit(stack, target, attacker);
        World world = player.getWorld();

        float baseDamage = BASE_DAMAGE;
        double speed = player.getVelocity().length();
        float kineticBonus = (float) (speed * 12.0f);

        boolean crit = player.fallDistance > 0.0F && !player.isOnGround() && !player.isClimbing() && !player.isTouchingWater();
        float finalDamage = baseDamage + kineticBonus;
        if (crit) finalDamage *= 1.5f;

        target.damage(world.getDamageSources().playerAttack(player), finalDamage);

        // 🛡 Apply Resistance V for 1 second on melee hit - only when gliding
        if (player.isFallFlying()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20, 4, false, false, true));
        }

        if (!world.isClient) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1.0f, 1.4f);
            if (player instanceof ServerPlayerEntity sp)
                sp.sendMessage(Text.literal(String.format("§b⚡ %.1f kinetic strike", finalDamage)), true);
        }

        return super.postHit(stack, target, attacker);
    }
}
