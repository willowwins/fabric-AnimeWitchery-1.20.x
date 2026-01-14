package net.willowins.animewitchery.entity.projectile;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.ModEntities;
import net.willowins.animewitchery.enchantments.ModEnchantments;
import net.willowins.animewitchery.item.ModItems;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class NeedleProjectileEntity extends PersistentProjectileEntity implements GeoEntity {
    private static final TrackedData<Byte> ANCHOR_FLAGS = DataTracker.registerData(NeedleProjectileEntity.class,
            TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(NeedleProjectileEntity.class,
            TrackedDataHandlerRegistry.ITEM_STACK);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int returnTimer;

    public NeedleProjectileEntity(EntityType<? extends NeedleProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public NeedleProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.NEEDLE_PROJECTILE, owner, world);
        this.setItem(stack.copy()); // Use specific item or fallback
        this.setNoGravity(true);
        // Assuming Sliver enchantment > 0 enables "Reeling" (Grapple) behavior default?
        // Or is "Reeling" a specific enchantment in Arsenal?
        // Arsenal: Reeling pulls entity/player. Normal: knockback?
        // Let's assume Sliver level > 0 means "Has Reeling" (Grapple/Pull capability).
        this.setReeling(EnchantmentHelper.getLevel(ModEnchantments.SLIVER, stack) > 0);
    }

    // For Geckolib
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void setItem(ItemStack stack) {
        if (!stack.isOf(ModItems.NEEDLE) || stack.hasNbt()) {
            this.getDataTracker().set(ITEM, stack.copyWithCount(1));
        }
    }

    private ItemStack getTrackedItem() {
        return this.getDataTracker().get(ITEM);
    }

    public ItemStack getStack() {
        ItemStack itemStack = this.getTrackedItem();
        return itemStack.isEmpty() ? new ItemStack(ModItems.NEEDLE) : itemStack;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(ANCHOR_FLAGS, (byte) 0);
        this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
    }

    private static final double MAX_RANGE = 20.0;

    @Override
    public void tick() {
        Entity owner = this.getOwner();
        double d = 2;

        if (!this.getWorld().isClient) {
            if (owner == null || !owner.isAlive()) {
                this.discard();
                return;
            }
            // Arsenal logic modification: Needle shouldn't be "NoClip" forever or "return"
            // logic might differ.
            // Arsenal: if (this.hasDealtDamage() || this.isNoClip()) ...
            if (this.hasDealtDamage() || this.isNoClip()) {
                this.setNoClip(true);
                Vec3d vec3d = owner.getEyePos().subtract(this.getPos());
                if (this.getWorld().isClient) {
                    this.lastRenderY = this.getY();
                }

                double length = vec3d.length();
                // Pull projectile to owner if dealt damage (Retracting?)
                this.setVelocity(vec3d.normalize().multiply(Math.min(length, d * 3)));
            }
            if (this.getPos().distanceTo(owner.getPos()) > MAX_RANGE) {
                this.setDealtDamage(true);
            }
        }

        if (this.inGround && !this.hasDealtDamage()) {
            if (this.hasReeling()) {
                if (this.returnTimer++ > 100) {
                    this.setDealtDamage(true);
                }
                if (owner == null) {
                    this.setDealtDamage(true);
                    return;
                }
                float e = (float) (d / 5f);
                Vec3d vec3d = this.getPos().subtract(owner.getEyePos());
                // GRAPPLE PULL: Pull OWNER towards PROJECTILE
                owner.setVelocity(owner.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(e)));
                // Update owner fall distance
                owner.fallDistance = 0;
                owner.velocityModified = true; // Ensure updates
            } else {
                // Normal impact logic (Explosion/Search for entities) - Arsenal default
                float radius = 5f;
                this.getWorld().addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                for (LivingEntity hitLivingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class,
                        this.getBoundingBox().expand(radius), LivingEntity::isAlive)) {
                    float strength = this.getKnockbackForEntity(hitLivingEntity);
                    if (!(strength <= 0.0)) {
                        this.velocityDirty = true;
                        Vec3d distance = hitLivingEntity.getPos().add(0, hitLivingEntity.getHeight() / 2f, 0)
                                .subtract(this.getPos());
                        Vec3d footDistance = hitLivingEntity.getPos().subtract(this.getPos());
                        if (footDistance.y > distance.y) {
                            distance = footDistance;
                        }
                        float proximity = (float) MathHelper.lerp(MathHelper.clamp(distance.length() / radius, 0, 1), 1,
                                0);
                        Vec3d direction = distance.normalize().multiply(proximity * strength);
                        hitLivingEntity.addVelocity(direction.x, direction.y, direction.z);
                        hitLivingEntity.fallDistance = 0;
                    }
                }
                this.setDealtDamage(true);
            }
        }

        super.tick();
    }

    @Override
    public void setPitch(float pitch) {
        if (!this.hasDealtDamage()) {
            super.setPitch(pitch);
        }
    }

    @Override
    public void setYaw(float yaw) {
        if (!this.hasDealtDamage()) {
            super.setYaw(yaw);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        float damage = 10F;
        if (hitEntity instanceof LivingEntity livingEntity) {
            damage += EnchantmentHelper.getAttackDamage(this.getTrackedItem(), livingEntity.getGroup());
        }
        Entity owner = this.getOwner();
        this.setDealtDamage(true);
        SoundEvent soundEvent = this.getHitSound();
        hitEntity.timeUntilRegen = 0;

        // Damage source: Arrow for now
        if (hitEntity.damage(this.getDamageSources().arrow(this, owner), damage)) {
            if (hitEntity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (hitEntity instanceof LivingEntity hitLivingEntity) {
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(hitLivingEntity, owner);
                    EnchantmentHelper.onTargetDamaged((LivingEntity) owner, hitLivingEntity);
                    // knockback or reel in
                    float strength = this.getKnockbackForEntity(hitLivingEntity);
                    if (!(strength <= 0.0)) {
                        this.velocityDirty = true;
                        Vec3d dir = hitLivingEntity.getPos().subtract(owner.getPos()).normalize().multiply(strength);
                        if (this.hasReeling()) {
                            // Reverse pull? Or Pull towards owner?
                            // Arsenal code: dir =
                            // owner.getPos().subtract(hitLivingEntity.getPos()).multiply(strength / 10f);
                            // This pulls HIT ENTITY towards OWNER.
                            dir = owner.getPos().subtract(hitLivingEntity.getPos()).multiply(strength / 5f); // Increased
                                                                                                             // strength
                                                                                                             // slightly
                        }
                        hitLivingEntity.addVelocity(dir.x, dir.y, dir.z);
                    }
                }
                this.onHit(hitLivingEntity);
            }

            if (this.getOwner() instanceof PlayerEntity player && !player.isCreative()) {
                player.getItemCooldownManager().set(ModItems.NEEDLE, 30); // 1.5s Cooldown
            }
        }
        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        this.playSound(soundEvent, 1.0f, 1.0f);
    }

    private float getKnockbackForEntity(LivingEntity hitLivingEntity) {
        // Reverse Pull Logic: If target is resistant, maybe pull owner?
        // Arsenal code: return (float) (1f * (1.0 -
        // hitLivingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
        // This reduces strength if resistant.

        // Custom addition for Reverse Pull (since user requested it earlier):
        double resistance = hitLivingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity livingOwner) {
            double ownerResist = livingOwner.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
            if (resistance > ownerResist) {
                // Target is stronger! Pull OWNER to TARGET.
                Vec3d pullDir = hitLivingEntity.getPos().subtract(livingOwner.getPos()).normalize().multiply(1.5);
                livingOwner.addVelocity(pullDir.x, pullDir.y, pullDir.z);
                livingOwner.velocityModified = true;
                return 0; // Don't pull target
            }
        }

        return (float) (1f * (1.0 - resistance));
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) { // Allow pickup if owner or generic
            if (!player.getAbilities().creativeMode) {
                player.getInventory().insertStack(this.getStack());
            }
            this.discard();
            return true;
        }
        return false;
    }

    @Override
    protected float getDragInWater() {
        return 0.99F;
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ENTITY_ARROW_HIT;
    }

    @Override
    protected ItemStack asItemStack() {
        return this.getStack();
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    public boolean hasDealtDamage() {
        return this.getAnchorFlag(0);
    }

    public void setDealtDamage(boolean dealtDamage) {
        this.setAnchorFlag(0, dealtDamage);
    }

    public boolean hasReeling() {
        return this.getAnchorFlag(1);
    }

    public void setReeling(boolean reeling) {
        this.setAnchorFlag(1, reeling);
    }

    private boolean getAnchorFlag(int flag) {
        if (flag < 0 || flag > 8) {
            return false;
        }
        return (this.dataTracker.get(ANCHOR_FLAGS) >> flag & 0x01) == 1;
    }

    private void setAnchorFlag(int flag, boolean value) {
        if (flag < 0 || flag > 8) {
            return;
        }
        if (value) {
            this.dataTracker.set(ANCHOR_FLAGS, (byte) (this.dataTracker.get(ANCHOR_FLAGS) | 1 << flag));
        } else {
            this.dataTracker.set(ANCHOR_FLAGS, (byte) (this.dataTracker.get(ANCHOR_FLAGS) & ~(1 << flag)));
        }
    }
}
