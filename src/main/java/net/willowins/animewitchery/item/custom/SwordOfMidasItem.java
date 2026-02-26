package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class SwordOfMidasItem extends SwordItem {
    public SwordOfMidasItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDead()) {
            if (!target.getWorld().isClient) {
                // 50% chance to drop gold nugget
                if (target.getWorld().random.nextFloat() < 0.5f) {
                    ItemEntity item = new ItemEntity(target.getWorld(), target.getX(), target.getY(), target.getZ(),
                            new ItemStack(Items.GOLD_NUGGET));
                    target.getWorld().spawnEntity(item);
                }
            }
        }

        // Transmute Equipment Logic
        if (!target.getWorld().isClient) {
            // 20% chance to transmute an item
            if (target.getWorld().random.nextFloat() < 0.2f) {
                net.minecraft.entity.EquipmentSlot[] slots = net.minecraft.entity.EquipmentSlot.values();
                for (net.minecraft.entity.EquipmentSlot slot : slots) {
                    if (slot.getType() == net.minecraft.entity.EquipmentSlot.Type.ARMOR
                            || slot == net.minecraft.entity.EquipmentSlot.MAINHAND
                            || slot == net.minecraft.entity.EquipmentSlot.OFFHAND) {
                        ItemStack equipped = target.getEquippedStack(slot);
                        if (!equipped.isEmpty()) {
                            net.minecraft.item.Item goldVariant = getGoldVariant(equipped.getItem());
                            if (goldVariant != null && equipped.getItem() != goldVariant) {
                                ItemStack goldStack = new ItemStack(goldVariant);
                                goldStack.setCount(equipped.getCount());
                                target.equipStack(slot, goldStack);
                                target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(),
                                        net.minecraft.sound.SoundEvents.BLOCK_ANVIL_USE,
                                        net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);
                                break; // Only one item per hit
                            }
                        }
                    }
                }
            }
        }

        // Ensure 100% Equipment Drop Chance
        if (target instanceof net.minecraft.entity.mob.MobEntity) {
            net.minecraft.entity.mob.MobEntity mob = (net.minecraft.entity.mob.MobEntity) target;
            for (net.minecraft.entity.EquipmentSlot slot : net.minecraft.entity.EquipmentSlot.values()) {
                mob.setEquipmentDropChance(slot, 1.0f);
            }
        }

        return super.postHit(stack, target, attacker);
    }

    private net.minecraft.item.Item getGoldVariant(net.minecraft.item.Item item) {
        // Swords
        if (item instanceof net.minecraft.item.SwordItem)
            return Items.GOLDEN_SWORD;
        // Axes
        if (item instanceof net.minecraft.item.AxeItem)
            return Items.GOLDEN_AXE;
        // Pickaxes
        if (item instanceof net.minecraft.item.PickaxeItem)
            return Items.GOLDEN_PICKAXE;
        // Shovels
        if (item instanceof net.minecraft.item.ShovelItem)
            return Items.GOLDEN_SHOVEL;
        // Hoes
        if (item instanceof net.minecraft.item.HoeItem)
            return Items.GOLDEN_HOE;

        // Armor
        if (item instanceof net.minecraft.item.ArmorItem) {
            net.minecraft.item.ArmorItem armor = (net.minecraft.item.ArmorItem) item;
            switch (armor.getSlotType()) {
                case HEAD:
                    return Items.GOLDEN_HELMET;
                case CHEST:
                    return Items.GOLDEN_CHESTPLATE;
                case LEGS:
                    return Items.GOLDEN_LEGGINGS;
                case FEET:
                    return Items.GOLDEN_BOOTS;
                default:
                    return null;
            }
        }

        return null;
    }
}
