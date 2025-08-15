package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.ModEntities;
import java.util.Objects;

public class VoidWispSpawnEggItem extends Item {
    public VoidWispSpawnEggItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }

        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockPos spawnPos = blockPos.offset(direction);

        EntityType<?> entityType = ModEntities.VOID_WISP;
        if (entityType.spawnFromItemStack((ServerWorld) world, itemStack, context.getPlayer(), spawnPos, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, spawnPos) && direction == Direction.UP) != null) {
            itemStack.decrement(1);
        }

        return ActionResult.CONSUME;
    }
}
