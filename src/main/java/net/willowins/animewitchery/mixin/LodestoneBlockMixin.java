package net.willowins.animewitchery.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.world.dimension.ModDimensions;
import net.willowins.animewitchery.world.dimension.PocketManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.block.LodestoneBlock")
public class LodestoneBlockMixin {

    @Inject(method = "onPlaced", at = @At("TAIL"))
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack, CallbackInfo ci) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            if (world.getRegistryKey().equals(ModDimensions.POCKET_LEVEL_KEY)) {

                int x = pos.getX();
                int pocketId = PocketManager.getPocketIdFromPos(pos);
                if (pocketId != -1) {
                    PocketManager manager = PocketManager.getServerState(serverWorld);
                    manager.setLegacySpawn(pocketId, pos);

                    if (placer != null && placer instanceof net.minecraft.server.network.ServerPlayerEntity player) {
                        player.sendMessage(
                                Text.literal("Pocket ID " + pocketId + " spawn updated to " + pos.toShortString())
                                        .formatted(Formatting.GREEN),
                                true);
                    }
                }
            }
        }
    }
}
