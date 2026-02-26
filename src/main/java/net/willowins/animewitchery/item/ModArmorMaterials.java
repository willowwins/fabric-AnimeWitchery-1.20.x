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
        SILVER("silver", 25, new int[] { 3, 8, 6, 3 }, 10,
                        SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.5f, .5f, () -> Ingredient.ofItems(ModItems.SILVER)),
        ASSASSIN("assassin", 25, new int[] { 2, 6, 5, 2 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        BUTCHER("butcher", 25, new int[] { 3, 8, 6, 3 }, 10,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.5f, 0.5f, () -> Ingredient.ofItems(ModItems.SILVER)),
        BRUTE("brute", 35, new int[] { 4, 9, 7, 4 }, 10,
                        SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0f, 1.0f, () -> Ingredient.ofItems(ModItems.SILVER)), // Higher
                                                                                                                   // toughness/knockback
        MONK("monk", 25, new int[] { 2, 5, 4, 2 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        HEALER("healer", 25, new int[] { 2, 6, 5, 2 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        SANGUINE("sanguine", 25, new int[] { 3, 8, 6, 3 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.5f, 0.5f, () -> Ingredient.ofItems(ModItems.SILVER)),
        DRUID("druid", 25, new int[] { 2, 6, 5, 2 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        ALCHEMIST("alchemist", 25, new int[] { 2, 6, 5, 2 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),

        // Phase 3
        GUNSLINGER("gunslinger", 25, new int[] { 2, 6, 5, 2 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        FEMBOY("femboy", 15, new int[] { 1, 4, 3, 1 }, 25, // Weak but cute
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        GAMBLER("gambler", 25, new int[] { 2, 6, 5, 2 }, 20,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),

        // Phase 4
        KNIGHT("knight", 35, new int[] { 3, 8, 6, 3 }, 10,
                        SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        PALADIN("paladin", 35, new int[] { 3, 9, 7, 3 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0f, 0.1f, () -> Ingredient.ofItems(ModItems.SILVER)),
        DEATHBRINGER("deathbringer", 25, new int[] { 2, 6, 5, 2 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        SUMMONER("summoner", 25, new int[] { 2, 5, 4, 2 }, 20,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        MAGE("mage", 20, new int[] { 2, 5, 4, 2 }, 30,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),

        // Phase 5
        FARMER("farmer", 25, new int[] { 2, 5, 4, 2 }, 10,
                        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        PROSPECTOR("prospector", 25, new int[] { 2, 6, 5, 2 }, 10,
                        SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        HOARDER("hoarder", 25, new int[] { 3, 8, 6, 3 }, 50,
                        SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0f, 0.0f, () -> Ingredient.ofItems(ModItems.SILVER)),
        RAILGUNNER("railgunner", 250, new int[] { 3, 8, 6, 3 }, 8,
                        SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3f, .1f,
                        () -> Ingredient.ofItems(Items.NETHERITE_INGOT)),

        RESONANT("resonant", 37, new int[] { 3, 8, 6, 3 }, 25, // enchantability = 25 (like gold)
                        SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
                        3.0F, // toughness same as Netherite
                        0.1F, // knockback resistance same as Netherite
                        () -> Ingredient.ofItems(ModItems.RESONANT_CATALYST)),

        OBELISK("obelisk", 500, new int[] { 4, 9, 7, 4 }, 15,
                        SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 4f, .2f, () -> Ingredient.ofItems(ModItems.SILVER)),

        HALOIC("haloic", 75, new int[] { 4, 9, 7, 4 }, 25,
                        SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 4.0F, 0.2F, () -> Ingredient.ofItems(ModItems.HALOIC_INGOT));

        private final String name;
        private final int durabilityMultiplier;
        private final int[] protectionAmounts;
        private final int enchantability;
        private final SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;
        private final Supplier<Ingredient> repairIngredient;

        private static final int[] BASE_DURABILITY = { 11, 16, 15, 13 };

        ModArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability,
                        SoundEvent equipSound,
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
