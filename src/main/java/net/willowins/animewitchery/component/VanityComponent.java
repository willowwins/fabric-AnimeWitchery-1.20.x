package net.willowins.animewitchery.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.willowins.animewitchery.mana.ModComponents;

public class VanityComponent implements IVanityComponent {
    private final PlayerEntity player;
    private final SimpleInventory inventory;

    public VanityComponent(PlayerEntity player) {
        this.player = player;
        this.inventory = new SimpleInventory(4);
        this.inventory.addListener(sender -> ModComponents.VANITY.sync(this.player));
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(this.inventory.size(), ItemStack.EMPTY);
        Inventories.readNbt(tag, list);
        for (int i = 0; i < list.size(); i++) {
            this.inventory.setStack(i, list.get(i));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(this.inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.inventory.size(); i++) {
            list.set(i, this.inventory.getStack(i));
        }
        Inventories.writeNbt(tag, list);
    }
}
