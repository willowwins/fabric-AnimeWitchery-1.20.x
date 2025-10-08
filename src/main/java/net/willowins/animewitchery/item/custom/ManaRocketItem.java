package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;

public class ManaRocketItem extends Item {
    private static final int USE_MANA_COST = 500;
    private static final float BOOST_POWER = 5.0f;

    public ManaRocketItem(Settings settings) {
        super(settings);
    }

    /** Shared effect: boost + sound + particle. Use this from other items too. */
    public static void doRocketEffect(World world, PlayerEntity user) {
        Vec3d look = user.getRotationVector().normalize();
        user.addVelocity(look.x * BOOST_POWER, look.y * BOOST_POWER * 0.6, look.z * BOOST_POWER);
        user.velocityModified = true;

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 2.0f, 0.9f);

        if (world instanceof ServerWorld sw) {
            Vec3d pos = user.getPos();
            for (int i = 0; i < 40; i++) {
                double ox = (world.random.nextDouble() - 0.5) * 0.5;
                double oy = (world.random.nextDouble() - 0.5) * 0.5;
                double oz = (world.random.nextDouble() - 0.5) * 0.5;
                sw.spawnParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, ox, oy, oz, 0.02);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient) {
            return TypedActionResult.pass(stack);
        }

        if (!user.isFallFlying()) {
            user.sendMessage(Text.literal("You must be gliding to use this rocket!")
                    .formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }

        IManaComponent mana = ModComponents.PLAYER_MANA.get(user);
        if (mana.getMana() < USE_MANA_COST) {
            user.sendMessage(Text.literal("Not enough mana!").formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }

        mana.consume(USE_MANA_COST);

        // Use the shared effect method
        doRocketEffect(world, user);

        user.getItemCooldownManager().set(this, 30);

        return TypedActionResult.success(stack, world.isClient());
    }
}
