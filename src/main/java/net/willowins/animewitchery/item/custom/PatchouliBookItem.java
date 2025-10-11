package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliBookItem extends Item {
    private final String bookId;

    public PatchouliBookItem(Settings settings, String bookId) {
        super(settings);
        this.bookId = bookId;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        NbtCompound nbt = new NbtCompound();
        nbt.putString("patchouli:book", bookId);
        stack.setNbt(nbt);
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            PatchouliAPI.get().openBookGUI(new Identifier(bookId));
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }
}
