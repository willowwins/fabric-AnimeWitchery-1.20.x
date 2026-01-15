package net.willowins.animewitchery.item.armor;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.item.armor.client.ResonantArmorRenderer;
import net.willowins.animewitchery.item.custom.ModArmorItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Resonant Armor — GeckoLib-based animated armor with status effects.
 * Plays idle animation when the full set is equipped.
 * Grants Resistance and Mana Regen when wearing full set.
 */
public final class ResonantArmorItem extends ModArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public ResonantArmorItem(ArmorMaterial armorMaterial, Type type, Settings properties) {
        super(armorMaterial, type, properties);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(
                    LivingEntity entity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    BipedEntityModel<LivingEntity> original) {
                if (renderer == null) {
                    renderer = new ResonantArmorRenderer();
                }
                renderer.prepForRender(entity, stack, slot, original);
                return renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 20, state -> {
            Entity entity = state.getData(DataTickets.ENTITY);
            if (entity == null)
                return PlayState.STOP;

            // Always play idle for armor stands
            if (entity instanceof ArmorStandEntity) {
                state.getController().setAnimation(DefaultAnimations.IDLE);
                return PlayState.CONTINUE;
            }

            // Only animate on players wearing the full set
            if (entity instanceof PlayerEntity player && hasFullSet(player)) {
                state.getController().setAnimation(DefaultAnimations.IDLE);
                return PlayState.CONTINUE;
            }

            return PlayState.STOP;
        }));
    }

    /** Checks whether the player is wearing the complete Resonant armor set. */
    private boolean hasFullSet(PlayerEntity player) {
        ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = player.getEquippedStack(EquipmentSlot.FEET);

        return head.isOf(ModItems.RESONANT_HELMET)
                && chest.isOf(ModItems.RESONANT_CHESTPLATE)
                && legs.isOf(ModItems.RESONANT_LEGGINGS)
                && feet.isOf(ModItems.RESONANT_BOOTS);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient() && entity instanceof PlayerEntity player) {
            if (hasFullSet(player) && shouldSpawnAura(player)) {
                spawnResonantAura(world, player);
            }
        }
        // Call parent to apply status effects
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private boolean shouldSpawnAura(PlayerEntity player) {
        // 1. Check for Full Mana
        var manaComponent = net.willowins.animewitchery.mana.ModComponents.PLAYER_MANA.get(player);
        if (manaComponent.getMana() < manaComponent.getMaxMana()) {
            return false;
        }

        // 2. Check for Resonant Greatsword in Main Hand
        ItemStack mainHand = player.getMainHandStack();
        if (!(mainHand.getItem() instanceof net.willowins.animewitchery.item.custom.ResonantGreatSwordItem)) {
            return false;
        }

        // 3. Check for >75% Charge
        float charge = net.willowins.animewitchery.item.custom.ResonantGreatSwordItem.getCharge(mainHand);
        float maxCharge = net.willowins.animewitchery.item.custom.ResonantGreatSwordItem.getMaxCharge();
        return charge >= (maxCharge * 0.75f);
    }

    private void spawnResonantAura(World world, PlayerEntity player) {
        double x = player.getX() + (world.random.nextDouble() - 0.5) * 1.5;
        double y = player.getY() + world.random.nextDouble() * 2.0;
        double z = player.getZ() + (world.random.nextDouble() - 0.5) * 1.5;

        // Rising Wisp Particles (Purple to Green)
        team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
                .create(team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry.WISP_PARTICLE)
                .setScaleData(
                        team.lodestar.lodestone.systems.particle.data.GenericParticleData.create(0.35f, 0f).build())
                .setTransparencyData(
                        team.lodestar.lodestone.systems.particle.data.GenericParticleData.create(0.7f, 0f).build())
                .setColorData(team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
                        .create(new java.awt.Color(148, 0, 211), new java.awt.Color(50, 205, 50)) // Dark Violet to Lime
                                                                                                  // Green
                        .setCoefficient(1.2f)
                        .setEasing(team.lodestar.lodestone.systems.easing.Easing.EXPO_OUT)
                        .build())
                .setLifetime(30)
                .addMotion(0, 0.12 + world.random.nextDouble() * 0.1, 0)
                .enableNoClip()
                .spawn(world, x, y, z);

        // Occasional intense sparkles
        if (world.random.nextInt(15) == 0) {
            team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
                    .create(team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry.SPARKLE_PARTICLE)
                    .setScaleData(
                            team.lodestar.lodestone.systems.particle.data.GenericParticleData.create(0.6f, 0f).build())
                    .setColorData(team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
                            .create(new java.awt.Color(200, 100, 255), new java.awt.Color(100, 255, 100))
                            .build())
                    .setLifetime(20)
                    .spawn(world, player.getX() + (world.random.nextDouble() - 0.5) * 2,
                            player.getY() + world.random.nextDouble() * 2.5,
                            player.getZ() + (world.random.nextDouble() - 0.5) * 2);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
