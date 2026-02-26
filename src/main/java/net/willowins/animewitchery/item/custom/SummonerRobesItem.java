package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.ISummonedEntity;
import net.willowins.animewitchery.item.ModItems;
import net.minecraft.entity.EquipmentSlot;

public class SummonerRobesItem extends ArmorItem {
    public SummonerRobesItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player && slot == EquipmentSlot.CHEST.getEntitySlotId()) {
            if (isFullSet(player) && player.age % 60 == 0) { // Every 3 seconds
                java.util.List<LivingEntity> nearby = world.getEntitiesByClass(LivingEntity.class,
                        player.getBoundingBox().expand(15), e -> e != player && e.isAlive());
                for (LivingEntity target : nearby) {
                    boolean isMine = false;
                    if (target instanceof TameableEntity tameable) {
                        if (tameable.getOwnerUuid() != null && tameable.getOwnerUuid().equals(player.getUuid()))
                            isMine = true;
                    } else if (target instanceof ISummonedEntity summoned && summoned.getSummonerUuid() != null) {
                        if (summoned.getSummonerUuid().equals(player.getUuid()))
                            isMine = true;
                    } else {
                        // Fallback check NBT
                        NbtCompound nbt = new NbtCompound();
                        target.writeNbt(nbt);
                        if (nbt.contains("SummonerOwner") && nbt.getUuid("SummonerOwner").equals(player.getUuid())) {
                            isMine = true;
                        }
                    }

                    if (isMine) {
                        target.heal(1.0f); // Heal 0.5 hearts
                    }
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private boolean isFullSet(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).getItem() == ModItems.SUMMONER_HOOD &&
                player.getEquippedStack(EquipmentSlot.CHEST).getItem() == ModItems.SUMMONER_ROBES &&
                player.getEquippedStack(EquipmentSlot.LEGS).getItem() == ModItems.SUMMONER_LEGGINGS &&
                player.getEquippedStack(EquipmentSlot.FEET).getItem() == ModItems.SUMMONER_BOOTS;
    }
}
