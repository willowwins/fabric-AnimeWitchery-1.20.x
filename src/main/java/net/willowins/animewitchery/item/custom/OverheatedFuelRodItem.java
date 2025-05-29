package net.willowins.animewitchery.item.custom;

import dev.emi.trinkets.api.TrinketItem;
import io.github.fabricators_of_create.porting_lib.item.CustomMaxCountItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;

public class OverheatedFuelRodItem  extends Item{
    public OverheatedFuelRodItem(Settings settings) {
    super(settings);
}
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.getMainHandStack().isOf(ModItems.OVERHEATED_FUEL_ROD)){
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
            user.getMainHandStack().decrement(1);
            user.giveItemStack(new ItemStack(ModItems.FUEL_ROD.asItem()));}
        if(user.getOffHandStack().isOf(ModItems.OVERHEATED_FUEL_ROD)){
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0f, 1.0f);
            user.getOffHandStack().decrement(1);
            user.giveItemStack(new ItemStack(ModItems.FUEL_ROD.asItem()));}
            return super.use(world, user, hand);
    }

}
