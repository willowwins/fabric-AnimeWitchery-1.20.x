package net.willowins.animewitchery.mixin.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler {

    protected PlayerScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addVanitySlots(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        // Vanity slots disabled in favor of Cosmetic Bag Item
        /*
         * var vanityComponent = ModComponents.VANITY.getNullable(owner);
         * if (vanityComponent != null) {
         * var vanityInv = vanityComponent.getInventory();
         * 
         * // Head
         * this.addSlot(new Slot(vanityInv, 0, 77, 8) {
         * public Pair<Identifier, Identifier> getBackgroundSprite() {
         * return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
         * PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE);
         * }
         * });
         * 
         * // Chest
         * this.addSlot(new Slot(vanityInv, 1, 77, 26) {
         * public Pair<Identifier, Identifier> getBackgroundSprite() {
         * return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
         * PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE);
         * }
         * });
         * 
         * // Legs
         * this.addSlot(new Slot(vanityInv, 2, 77, 44) {
         * public Pair<Identifier, Identifier> getBackgroundSprite() {
         * return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
         * PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE);
         * }
         * });
         * 
         * // Feet
         * this.addSlot(new Slot(vanityInv, 3, 98, 62) {
         * public Pair<Identifier, Identifier> getBackgroundSprite() {
         * return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
         * PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE);
         * }
         * });
         * }
         */
    }
}
