package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Optional;
import net.willowins.animewitchery.entity.ISummonedEntity;
import net.minecraft.inventory.SimpleInventory;
import net.willowins.animewitchery.item.ModItems;

public class SummonerStaffItem extends Item {
    public SummonerStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            ItemStack offhand = user.getOffHandStack();
            if (offhand.getItem() instanceof SoulJarItem) {
                // Shift-Right-Click: Recall AOE
                if (user.isSneaking()) {
                    SimpleInventory inv = new SimpleInventory(27);
                    SoulJarItem.loadInventory(offhand, inv);

                    net.minecraft.util.math.Box box = user.getBoundingBox().expand(20);
                    java.util.List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box,
                            e -> e != user);

                    int recalledCount = 0;
                    for (LivingEntity entity : entities) {
                        boolean isMine = false;
                        if (entity instanceof net.minecraft.entity.passive.TameableEntity tameable) {
                            if (tameable.getOwnerUuid() != null && tameable.getOwnerUuid().equals(user.getUuid())) {
                                isMine = true;
                            }
                        }
                        if (!isMine) {
                            if (entity instanceof ISummonedEntity summoned && summoned.getSummonerUuid() != null) {
                                isMine = summoned.getSummonerUuid().equals(user.getUuid());
                            } else {
                                NbtCompound nbt = new NbtCompound();
                                entity.writeNbt(nbt);
                                if (nbt.contains("SummonerOwner")) {
                                    isMine = nbt.getUuid("SummonerOwner").equals(user.getUuid());
                                }
                            }
                        }

                        if (isMine) {
                            if (inv.isEmpty() || SoulJarItem.hasSpace(inv)) {
                                // Capture back to jar
                                NbtCompound data = new NbtCompound();
                                entity.saveNbt(data);

                                ItemStack soulStack = new ItemStack(ModItems.SOUL);
                                NbtCompound soulNbt = soulStack.getOrCreateNbt();
                                soulNbt.put("EntityData", data);
                                if (entity.hasCustomName()) {
                                    soulNbt.putString("EntityName", entity.getCustomName().getString());
                                } else {
                                    soulNbt.putString("EntityName", entity.getType().getName().getString());
                                }

                                SoulJarItem.addToInv(inv, soulStack);
                                entity.discard();
                                recalledCount++;
                                user.addExperience(10);
                            }
                            // If fail (full), do nothing
                        }
                    }

                    if (recalledCount > 0) {
                        // SoulJarItem.saveInventory(offhand, inv); // captureEntity handles saving
                        user.sendMessage(
                                Text.literal("Recalled " + recalledCount + " entities.").formatted(Formatting.GREEN),
                                true);
                        return TypedActionResult.success(user.getStackInHand(hand));
                    } else {
                        // Check fullness by trying to load (inefficient but safe or just rely on user
                        // feeedback)
                        SimpleInventory testInv = new SimpleInventory(27);
                        SoulJarItem.loadInventory(offhand, testInv);
                        if (testInv.isEmpty()) { // Not strictly empty, but if we found nothing to recall...
                            // Do nothing
                        } else {
                            // Maybe full?
                        }
                        // Actually, we can't easily know if it failed due to full or just found nothing
                        // without changing return of captureEntity but it's fine.
                        return TypedActionResult.success(user.getStackInHand(hand));
                    }
                }

                // Check XP
                int cost = 20;
                if (!user.isCreative() && user.totalExperience < cost) {
                    user.sendMessage(Text.literal("Not enough XP to summon. Cost: " + cost + " points.")
                            .formatted(Formatting.RED), true);
                    return TypedActionResult.fail(user.getStackInHand(hand));
                }

                // Load Jar Inventory
                SimpleInventory inv = new SimpleInventory(27);
                SoulJarItem.loadInventory(offhand, inv);

                // Find first Soul Item
                int soulSlot = -1;
                ItemStack soulStack = ItemStack.EMPTY;

                for (int i = 0; i < inv.size(); i++) {
                    ItemStack s = inv.getStack(i);
                    if (s.getItem() instanceof SoulItem && s.hasNbt() && s.getNbt().contains("EntityData")) {
                        soulSlot = i;
                        soulStack = s;
                        break;
                    }
                }

                if (soulSlot != -1 && !soulStack.isEmpty()) {
                    if (spawnSoul(world, user, soulStack)) {
                        if (!user.isCreative()) {
                            user.addExperience(-cost);
                        }

                        // Remove soul from jar
                        inv.setStack(soulSlot, ItemStack.EMPTY);
                        SoulJarItem.saveInventory(offhand, inv);
                    }
                } else {
                    user.sendMessage(Text.literal("The Soul Jar has no souls.").formatted(Formatting.RED), true);
                }
            } else {
                user.sendMessage(Text.literal("Hold a Soul Jar in your offhand to summon.").formatted(Formatting.RED),
                        true);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private boolean spawnSoul(World world, PlayerEntity owner, ItemStack soulItem) {
        if (!(world instanceof ServerWorld serverWorld))
            return false;

        NbtCompound nbt = soulItem.getNbt();
        if (nbt == null || !nbt.contains("EntityData"))
            return false;

        NbtCompound data = nbt.getCompound("EntityData");

        // Entity ID might be in 'id' field of EntityData or separate
        String entityId = nbt.contains("EntityId") ? nbt.getString("EntityId") : data.getString("id");

        EntityType<?> type = EntityType.get(entityId).orElse(null);
        if (type != null) {
            Entity entity = type.create(serverWorld);
            if (entity instanceof LivingEntity living) {
                // Restore data
                living.readNbt(data);
                living.setHealth(living.getMaxHealth());

                // Set position to owner's looking pos (Safe Summon)
                Vec3d start = owner.getEyePos();
                Vec3d direction = owner.getRotationVector();
                double range = 2.5;
                Vec3d end = start.add(direction.multiply(range));

                BlockHitResult hit = world.raycast(new RaycastContext(
                        start, end,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        owner));

                double x = end.x;
                double y = end.y;
                double z = end.z;

                if (hit.getType() == HitResult.Type.BLOCK) {
                    Vec3d hitPos = hit.getPos();
                    Vec3d safePos = hitPos.subtract(direction.multiply(0.5));
                    x = safePos.x;
                    y = safePos.y;
                    z = safePos.z;
                } else {
                    // Fallback at end of ray if no hit
                    x = end.x;
                    y = end.y;
                    z = end.z;
                    // Ensure consistency
                    if (x == end.x && y == end.y && z == end.z) {
                        // Should be fine
                    }
                }

                living.refreshPositionAndAngles(x, y, z, owner.getYaw(), 0);

                // Tame/Own logic
                if (entity instanceof ISummonedEntity summoned) {
                    summoned.setSummonerUuid(owner.getUuid());
                }

                if (entity instanceof net.minecraft.entity.passive.TameableEntity tameable) {
                    tameable.setOwner(owner);
                } else if (entity instanceof net.minecraft.entity.passive.AbstractHorseEntity horse) {
                    horse.setOwnerUuid(owner.getUuid());
                    horse.setTame(true);
                }

                if (entity instanceof net.minecraft.entity.mob.Angerable angerable) {
                    angerable.stopAnger();
                }

                serverWorld.spawnEntity(living);

                // Get name for message
                String name = nbt.getString("EntityName");
                owner.sendMessage(Text.literal("Summoned " + name).formatted(Formatting.GREEN), true);
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            boolean isMine = false;
            if (entity instanceof net.minecraft.entity.passive.TameableEntity tameable) {
                if (tameable.getOwnerUuid() != null && tameable.getOwnerUuid().equals(user.getUuid())) {
                    isMine = true;
                }
            }
            if (!isMine) {
                if (entity instanceof ISummonedEntity summoned && summoned.getSummonerUuid() != null) {
                    isMine = summoned.getSummonerUuid().equals(user.getUuid());
                } else {
                    NbtCompound nbt = new NbtCompound();
                    entity.writeNbt(nbt);
                    if (nbt.contains("SummonerOwner")) {
                        isMine = nbt.getUuid("SummonerOwner").equals(user.getUuid());
                    }
                }
            }

            if (isMine) {
                ItemStack offhand = user.getOffHandStack();
                if (offhand.getItem() instanceof SoulJarItem) {
                    if (SoulJarItem.captureEntity(user, entity, offhand)) {
                        entity.discard();
                        user.sendMessage(
                                Text.literal("Recalled " + entity.getName().getString()).formatted(Formatting.GREEN),
                                true);
                        user.addExperience(10);
                        return ActionResult.SUCCESS;
                    } else {
                        user.sendMessage(Text.literal("Soul Jar is full!").formatted(Formatting.RED), true);
                    }
                } else {
                    user.sendMessage(
                            Text.literal("Hold a Soul Jar in your offhand to recall.").formatted(Formatting.RED), true);
                }
            }
        }
        return ActionResult.PASS;
    }
}
