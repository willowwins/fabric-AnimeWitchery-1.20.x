package net.willowins.animewitchery.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void allowPotionFlaskInSlots(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // Slots 0, 1, 2 are bottle slots
        if ((slot >= 0 && slot <= 2) && (stack.isOf(ModItems.POTION_FLASK) || stack.isOf(ModItems.EMPTY_FLASK))) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canCraft", at = @At("RETURN"), cancellable = true)
    private static void allowFlaskCrafting(net.minecraft.util.collection.DefaultedList<ItemStack> slots,
            CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) { // If vanilla says no
            ItemStack ingredient = slots.get(3);
            if (ingredient.isEmpty())
                return;

            for (int i = 0; i < 3; i++) {
                ItemStack flask = slots.get(i);
                if (flask.isOf(ModItems.POTION_FLASK) || flask.isOf(ModItems.EMPTY_FLASK)) {
                    // Create a fake vanilla potion stack with the same NBT
                    ItemStack fakeStack = new ItemStack(net.minecraft.item.Items.POTION);
                    fakeStack.setNbt(flask.getNbt());

                    if (net.minecraft.recipe.BrewingRecipeRegistry.hasRecipe(fakeStack, ingredient)) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }
    }

    @Inject(method = "craft", at = @At("HEAD"))
    private static void performFlaskCrafting(World world, BlockPos pos,
            net.minecraft.util.collection.DefaultedList<ItemStack> slots, CallbackInfo ci) {
        ItemStack ingredient = slots.get(3);
        if (ingredient.isEmpty())
            return;

        for (int i = 0; i < 3; i++) {
            ItemStack flask = slots.get(i);
            if (flask.isOf(ModItems.POTION_FLASK) || flask.isOf(ModItems.EMPTY_FLASK)) {
                // Check if this flask can be crafted
                ItemStack fakeStack = new ItemStack(net.minecraft.item.Items.POTION);
                fakeStack.setNbt(flask.getNbt());

                if (net.minecraft.recipe.BrewingRecipeRegistry.hasRecipe(fakeStack, ingredient)) {
                    // Calculate output using the fake stack
                    ItemStack output = net.minecraft.recipe.BrewingRecipeRegistry.craft(ingredient, fakeStack);

                    if (!output.isEmpty()) {
                        // Apply the new Potion type to the Flask
                        // We do NOT change the Item type (keep it as Flask), just the Potion content.
                        net.minecraft.potion.Potion newPotion = net.minecraft.potion.PotionUtil.getPotion(output);
                        if (flask.isOf(ModItems.EMPTY_FLASK)) {
                            // Empty Flask -> Potion Flask
                            ItemStack newFlask = new ItemStack(ModItems.POTION_FLASK);
                            net.minecraft.potion.PotionUtil.setPotion(newFlask, newPotion);
                            // Preserve charges if any? Empty flask usually has no charges.
                            slots.set(i, newFlask);
                        } else {
                            // Update existing flask
                            net.minecraft.potion.PotionUtil.setPotion(flask, newPotion);
                            // Reset charges? Or keep? Usually brewing implies new liquid.
                            // For now, simple potion update.
                        }
                        // Note: Vanilla 'craft' continues after this.
                        // But since standard registry doesn't match flask, vanilla won't modify this
                        // slot further.
                    }
                }
            }
        }
    }
}
