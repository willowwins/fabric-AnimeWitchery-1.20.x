package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class WeatherItem extends Item {
    public WeatherItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            serverWorld.setWeather(0, 6000, true, true); // Rain for 5 minutes
            if (player != null) {
                player.sendMessage(Text.literal("Let it rain!"), true);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
