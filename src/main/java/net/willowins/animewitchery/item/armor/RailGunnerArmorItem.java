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
import net.willowins.animewitchery.item.armor.client.RailGunnerArmorRenderer;
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

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class RailGunnerArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public RailGunnerArmorItem(ArmorMaterial armorMaterial, Type type, Settings properties) {
        super(armorMaterial, type, properties);
    }

    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;

            public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                if (this.renderer == null) {
                    this.renderer = new RailGunnerArmorRenderer();
                }

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController[]{new AnimationController(this, 20, (state) -> {
            state.getController().setAnimation(DefaultAnimations.IDLE);
            Entity entity = (Entity)state.getData(DataTickets.ENTITY);
            if (entity instanceof ArmorStandEntity) {
                return PlayState.CONTINUE;
            } else {
                Set<Item> wornArmor = new ObjectOpenHashSet();
                Iterator var3 = entity.getArmorItems().iterator();

                while(var3.hasNext()) {
                    ItemStack stack = (ItemStack)var3.next();
                    if (stack.isEmpty()) {
                        return PlayState.STOP;
                    }

                    wornArmor.add(stack.getItem());
                }

                boolean isFullSet = true;
                return isFullSet ? PlayState.CONTINUE : PlayState.STOP;
            }
        })});
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}