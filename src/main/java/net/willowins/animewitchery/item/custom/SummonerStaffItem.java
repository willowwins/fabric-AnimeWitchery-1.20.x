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

import java.util.Optional;
import java.util.UUID;

public class SummonerStaffItem extends Item {
    public SummonerStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            // Check offhand for Soul Jar
            ItemStack offhand = user.getOffHandStack();
            if (offhand.getItem() instanceof SoulJarItem) {
                // Try to summon next soul
                Optional<NbtCompound> soulData = SoulJarItem.getNextSoul(offhand);
                if (soulData.isPresent()) {
                    spawnSoul(world, user, soulData.get(), offhand);
                } else {
                    user.sendMessage(Text.literal("The Soul Jar is empty.").formatted(Formatting.RED), true);
                }
            } else {
                user.sendMessage(Text.literal("Hold a Soul Jar in your offhand to summon.").formatted(Formatting.RED),
                        true);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private void spawnSoul(World world, PlayerEntity owner, NbtCompound soulEntry, ItemStack jar) {
        if (!(world instanceof ServerWorld serverWorld))
            return;

        String entityId = soulEntry.getString("EntityId");
        NbtCompound data = soulEntry.getCompound("Data");

        EntityType<?> type = EntityType.get(entityId).orElse(null);
        if (type != null) {
            Entity entity = type.create(serverWorld);
            if (entity instanceof LivingEntity living) {
                // Restore data
                living.readNbt(data);

                // Set position to owner's looking pos or just in front
                Vec3d pos = owner.getPos().add(owner.getRotationVector().multiply(2.0));
                living.refreshPositionAndAngles(pos.x, pos.y, pos.z, owner.getYaw(), 0);

                // Tame/Own logic
                // This helper needs to be implemented or we just use NBT
                if (entity instanceof net.minecraft.entity.passive.TameableEntity) {
                    ((net.minecraft.entity.passive.TameableEntity) entity).setOwner(owner);
                } else {
                    // Custom ownership tag using UUID
                    NbtCompound nbt = new NbtCompound();
                    living.writeNbt(nbt);
                    nbt.putUuid("SummonerOwner", owner.getUuid());
                    living.readNbt(nbt);
                }

                serverWorld.spawnEntity(living);

                // Remove from jar
                SoulJarItem.removeNextSoul(jar);

                owner.sendMessage(Text.literal("Summoned " + soulEntry.getString("Name")).formatted(Formatting.GREEN),
                        true);
            }
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            // Check if this entity is owned by the player (Summoned)
            boolean isMine = false;

            if (entity instanceof net.minecraft.entity.passive.TameableEntity tameable) {
                isMine = tameable.getOwner() == user;
            } else {
                NbtCompound nbt = new NbtCompound();
                entity.writeNbt(nbt);
                if (nbt.contains("SummonerOwner")) {
                    isMine = nbt.getUuid("SummonerOwner").equals(user.getUuid());
                }
            }

            if (isMine) {
                // Check offhand for jar
                ItemStack offhand = user.getOffHandStack();
                if (offhand.getItem() instanceof SoulJarItem) {
                    // Capture back to jar
                    NbtCompound data = new NbtCompound();
                    entity.writeNbt(data);
                    String id = EntityType.getId(entity.getType()).toString();

                    SoulJarItem.addSoul(offhand, id, data);

                    entity.discard(); // Remove entity
                    user.sendMessage(
                            Text.literal("Recalled " + entity.getName().getString()).formatted(Formatting.GREEN), true);
                    return ActionResult.SUCCESS;
                } else {
                    user.sendMessage(
                            Text.literal("Hold a Soul Jar in your offhand to recall.").formatted(Formatting.RED), true);
                }
            }
        }
        return ActionResult.PASS;
    }
}
