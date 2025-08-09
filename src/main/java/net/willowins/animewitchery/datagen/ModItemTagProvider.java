package net.willowins.animewitchery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.SILVER_HELMET,ModItems.SILVER_CHESTPLATE,ModItems.SILVER_LEGGINGS,ModItems.SILVER_BOOTS)
                .add(ModItems.OBELISK_HELMET,ModItems.OBELISK_CHESTPLATE,ModItems.OBELISK_LEGGINGS,ModItems.OBELISK_BOOTS);
    }
}
