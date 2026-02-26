package net.willowins.animewitchery.mixin.client;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.CosmeticBagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Pseudo
@Mixin(GeoArmorRenderer.class)
public class GeckoLibVanityMixin {

    // GeckoLib's GeoArmorRenderer often calls getEquippedStack to update the
    // Animatable
    // or during standard rendering checks. We try to catch it here.
    @Redirect(method = "prepForRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"), remap = true)
    private ItemStack redirectGetEquippedStack(LivingEntity entity, EquipmentSlot slot) {
        // net.willowins.animewitchery.AnimeWitchery.LOGGER.info("GeckoLibMixin:
        // Redirecting getEquippedStack for " + slot);

        // 1. Try Cosmetic Bag Item (Prioritized)
        if (entity instanceof net.minecraft.entity.player.PlayerEntity player) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.getItem() instanceof CosmeticBagItem) {
                    net.minecraft.nbt.NbtCompound nbt = stack.getNbt();
                    if (nbt != null && nbt.contains("Items")) {
                        net.minecraft.nbt.NbtList list = nbt.getList("Items", 10);
                        int targetSlot = switch (slot) {
                            case HEAD -> 0;
                            case CHEST -> 1;
                            case LEGS -> 2;
                            case FEET -> 3;
                            default -> -1;
                        };

                        if (targetSlot != -1) {
                            for (int k = 0; k < list.size(); k++) {
                                net.minecraft.nbt.NbtCompound itemNbt = list.getCompound(k);
                                if ((itemNbt.getByte("Slot") & 255) == targetSlot) {
                                    ItemStack vanityStack = ItemStack.fromNbt(itemNbt);
                                    if (!vanityStack.isEmpty()) {
                                        // net.willowins.animewitchery.AnimeWitchery.LOGGER.info("GeckoLibMixin: Found
                                        // stack: " + vanityStack.getItem());
                                        return vanityStack;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Fallback to original
        return entity.getEquippedStack(slot);
    }
}
