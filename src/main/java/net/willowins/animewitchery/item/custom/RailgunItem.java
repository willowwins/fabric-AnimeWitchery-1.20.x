package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;


public class RailgunItem extends Item {
    public RailgunItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            if (!world.isClient) {
                if (player.getMainHandStack().isOf(this) && player.isUsingItem()) {
                    int i = this.getMaxUseTime(stack) - player.getItemUseTimeLeft();
                    player.sendMessage(Text.of(String.valueOf(getPullProgress(i))), true);
                    stack.getOrCreateNbt().putFloat("charge", getPullProgress(i));

                    if (stack.getOrCreateNbt().getFloat("charge") == 1.0f) {
                        player.getItemCooldownManager().set(this, 100);
                        shootLaser(player.getRotationVector(), world, player.getPos().add(0,1,0), player);
                        player.stopUsingItem();
                    }
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void shootLaser(Vec3d look, World world, Vec3d pos, PlayerEntity owner) {
        for (int i = 0; i < 16; i++) {
            findEntities(world, 2, new BlockPos((int) (pos.getX() + (i*look.x)), (int) (pos.getY() + (i*look.y)), (int) (pos.getZ() + (i*look.z))), owner);
        }
        for (int i = 0; i < 160; i++) {
            world.addParticle(ParticleTypes.CLOUD, (pos.getX() + (((double) i /10)*look.x)), (pos.getY() + (((double) i /10)*look.y)), (pos.getZ() + (((double) i /10)*look.z)), 0, 0, 0);
        }
    }

    private void findEntities(World world, double radius, BlockPos pos, PlayerEntity owner) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Box box = new Box(
                pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius
        );

        List<LivingEntity> player = serverWorld.getEntitiesByClass(LivingEntity.class, box, entity -> true);


        for (LivingEntity target : player) {
            if (target != owner) {
                target.kill();
            }
        }


    }

    private static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 100;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }


    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }
}
