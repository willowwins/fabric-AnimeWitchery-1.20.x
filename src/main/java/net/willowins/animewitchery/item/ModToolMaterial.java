package net.willowins.animewitchery.item;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

public enum ModToolMaterial implements ToolMaterial {
    SILVER(5, 1561, 13, 3, 22,
            () -> Ingredient.ofItems(ModItems.SILVER)),
    OBELISK(5, 2031, 12, 5, 25,
            () -> Ingredient.ofItems(ModItems.ENCHANTED_CRYSTAL)),
    RESONANT(
            5, // Mining level: same as Netherite (4)
            2031, // Durability: same as Netherite
            9.0F, // Mining speed: same as Netherite
            5.0F, // Attack damage bonus: same as Netherite
            25, // Enchantability: same as Gold
            () -> Ingredient.ofItems(ModItems.RESONANT_CATALYST)),
    COPPER(
            1,
            250,
            6.0f,
            2.0f,
            18,
            () -> Ingredient.ofItems(net.minecraft.item.Items.COPPER_INGOT));

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    ModToolMaterial(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability,
            Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
