package net.willowins.animewitchery.mixin.client;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.CosmeticBagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.entity.player.PlayerEntity.class)
public class VanityLivingEntityMixin {

    @Inject(method = "getEquippedStack", at = @At("HEAD"), cancellable = true)
    private void getEquippedStackForVanity(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        // Only run this logic if we are currently rendering armor on the client
        if (net.willowins.animewitchery.client.CosmeticArmorState.isRendering) {
            net.minecraft.entity.player.PlayerEntity player = (net.minecraft.entity.player.PlayerEntity) (Object) this;
            // net.willowins.animewitchery.AnimeWitchery.LOGGER.info("Vanity: Intercepting
            // getEquippedStack for " + slot);

            // 1. Check Cosmetic Bag
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.getItem() instanceof CosmeticBagItem) {
                    // if (slot == EquipmentSlot.HEAD) {
                    // net.willowins.animewitchery.AnimeWitchery.LOGGER.info("Vanity: Found Bag in
                    // slot " + i);
                    // }
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
                                int itemSlot = itemNbt.getByte("Slot") & 255;

                                // if (slot == EquipmentSlot.HEAD) {
                                // net.willowins.animewitchery.AnimeWitchery.LOGGER
                                // .info("Vanity: Checking bag item in slot " + itemSlot + " vs target "
                                // + targetSlot);
                                // }
                                // if (slot == EquipmentSlot.HEAD) {
                                // net.willowins.animewitchery.AnimeWitchery.LOGGER
                                // .info("Vanity: Checking bag item in slot " + itemSlot + " vs target "
                                // + targetSlot);
                                // }

                                if (itemSlot == targetSlot) {
                                    ItemStack vanityStack = ItemStack.fromNbt(itemNbt);
                                    if (!vanityStack.isEmpty()) {
                                        // "Glass" item hides the armor
                                        if (vanityStack.getItem() == net.minecraft.item.Items.GLASS) {
                                            cir.setReturnValue(ItemStack.EMPTY);
                                            return;
                                        }

                                        // Found vanity item! Return it immediately.
                                        // net.willowins.animewitchery.AnimeWitchery.LOGGER.info("Vanity: FOUND " +
                                        // vanityStack.getItem() + " in Bag Slot " + itemSlot + ". Bag NBT: " +
                                        // list.toString());
                                        cir.setReturnValue(vanityStack);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /*
             * // 2. Try Trinkets (Compatibility)
             * try {
             * var trinketComponent =
             * dev.emi.trinkets.api.TrinketsApi.getTrinketComponent(player);
             * if (trinketComponent.isPresent()) {
             * String slotName = switch (slot) {
             * case HEAD -> "head";
             * case CHEST -> "chest";
             * case LEGS -> "legs";
             * case FEET -> "feet";
             * default -> "";
             * };
             * 
             * if (!slotName.isEmpty()) {
             * var groups = trinketComponent.get().getInventory();
             * var group = groups.get("vanity");
             * if (group == null)
             * group = groups.get("animewitchery:vanity");
             * 
             * if (group != null) {
             * var slots = group.get(slotName);
             * if (slots != null && !slots.isEmpty()) {
             * ItemStack trinketStack = slots.getStack(0);
             * if (!trinketStack.isEmpty()) {
             * cir.setReturnValue(trinketStack);
             * return;
             * }
             * }
             * }
             * }
             * }
             * } catch (NoClassDefFoundError | Exception e) {
             * // Ignore
             * }
             * 
             * // 3. Fallback to VanityComponent (Legacy/Deprecated)
             * var component =
             * net.willowins.animewitchery.mana.ModComponents.VANITY.getNullable(player);
             * if (component != null) {
             * var inventory = component.getInventory();
             * int index = switch (slot) {
             * case HEAD -> 0;
             * case CHEST -> 1;
             * case LEGS -> 2;
             * case FEET -> 3;
             * default -> -1;
             * };
             * 
             * if (index != -1) {
             * ItemStack vanityStack = inventory.getStack(index);
             * if (!vanityStack.isEmpty()) {
             * cir.setReturnValue(vanityStack);
             * }
             * }
             * }
             */
        }
    }
}
