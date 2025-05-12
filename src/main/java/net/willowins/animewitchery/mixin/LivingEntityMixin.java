package net.willowins.animewitchery.mixin;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.enchantments.ModEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        if (EnchantmentHelper.getLevel(ModEnchantments.BOOT_ENCHANT, boots) > 0) {
            if (player.isSneaking()) {
                createPlatform(player.getWorld(), player.getBlockPos());
            }
        }
    }

    private void createPlatform(World world, BlockPos pos) {
        BlockState blockState = ModBlocks.FLOAT_BLOCK.getDefaultState();
        BlockPos blockPos = new BlockPos(pos.getX()-1, pos.getY()-1, pos.getZ()-1);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                if (world.getBlockState(new BlockPos(blockPos.getX() + x, blockPos.getY(), blockPos.getZ() + z)).isOf(Blocks.AIR)) {
                    world.setBlockState(new BlockPos(blockPos.getX() + x, blockPos.getY(), blockPos.getZ() + z), blockState);
                }
            }
        }
    }
}
