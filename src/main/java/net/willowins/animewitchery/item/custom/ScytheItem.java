package net.willowins.animewitchery.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

public class ScytheItem extends MiningToolItem {
    private static final ItemStack ENCHANT_WEAPON_PROXY = new ItemStack(Items.DIAMOND_SWORD);
    private final int radius;

    public ScytheItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, int radius, Settings settings) {
        super(attackDamage, attackSpeed, toolMaterial, BlockTags.HOE_MINEABLE, settings);
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public static boolean isEnchantmentCompatible(ItemStack stack, Enchantment enchantment) {
        if (!(stack.getItem() instanceof ScytheItem)) {
            return enchantment.isAcceptableItem(stack);
        }

        return enchantment == Enchantments.EFFICIENCY || enchantment.isAcceptableItem(ENCHANT_WEAPON_PROXY);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            // Lifesteal: 1 heart (2.0f) per hit.
            player.heal(2.0f);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        boolean didHarvest = false;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos targetPos = pos.add(x, 0, z);
                BlockState targetState = world.getBlockState(targetPos);

                // If it's a CropBlock and fully grown
                if (targetState.getBlock() instanceof net.minecraft.block.CropBlock cropBlock) {
                    if (cropBlock.isMature(targetState)) {
                        if (!world.isClient) {
                            world.breakBlock(targetPos, true, player);
                            world.setBlockState(targetPos, cropBlock.withAge(0), net.minecraft.block.Block.NOTIFY_ALL);
                        }
                        didHarvest = true;
                    }
                }
            }
        }

        if (didHarvest) {
            context.getStack().damage(1, player, (p) -> p.sendToolBreakStatus(context.getHand()));
            if (!world.isClient) {
                world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_CROP_PLANT, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.success(world.isClient);
        }

        return super.useOnBlock(context);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.isIn(BlockTags.HOE_MINEABLE) && miner instanceof PlayerEntity player && !player.isSneaking()) {
            boolean didMine = false;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos targetPos = pos.add(x, y, z);
                        if (targetPos.equals(pos)) continue;
                        BlockState targetState = world.getBlockState(targetPos);
                        // Only chain-mine appropriate blocks without breaking the tool excessively
                        if (targetState.isIn(BlockTags.HOE_MINEABLE)) {
                            world.breakBlock(targetPos, true, player);
                            didMine = true;
                        }
                    }
                }
            }
            if (didMine) {
                stack.damage(1, miner, (e) -> e.sendEquipmentBreakStatus(net.minecraft.entity.EquipmentSlot.MAINHAND));
            }
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.isIn(BlockTags.HOE_MINEABLE) ? this.miningSpeed : 1.0F;
    }
}
