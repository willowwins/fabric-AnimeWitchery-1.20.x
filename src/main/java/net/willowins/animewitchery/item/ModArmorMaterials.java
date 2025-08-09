package net.willowins.animewitchery.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.willowins.animewitchery.AnimeWitchery;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    SILVER("silver",25,new int[] { 3,8,6,3},10,
            SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.5f, .5f, () -> Ingredient.ofItems(ModItems.SILVER)),
    RAILGUNNER("railgunner",250,new int[] { 3,8,6,3},8,
    SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3f, .1f, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),
    OBELISK("obelisk",500,new int[] { 4,9,7,4},15,
    SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 4f, .2f, () -> Ingredient.ofItems(ModItems.SILVER));



    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    private static final int[] BASE_DURABILITY = {11,16,15,13};

    ModArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound,
                      float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }


    @Override
    public int getDurability(ArmorItem.Type type) {
        return BASE_DURABILITY[type.ordinal()] * this.durabilityMultiplier;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return protectionAmounts[type.ordinal()];
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return AnimeWitchery.MOD_ID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
