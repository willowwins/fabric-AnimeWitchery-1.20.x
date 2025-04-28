package net.willowins.animewitchery.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.custom.MetalDetectorItem;
import net.willowins.animewitchery.item.custom.ModArmorItem;

public class ModItems {
    public static final Item SILVER =registerItem("silveringot", new Item(new FabricItemSettings()));
    public static final Item SILVERNUGGET =registerItem("silvernugget", new Item(new FabricItemSettings()));
        public static final Item RAWSILVER =registerItem("rawsilver", new Item(new FabricItemSettings()));
    public static final Item SPOOL =registerItem("spool", new Item(new FabricItemSettings()));
    public static final Item SILVERSPOOL =registerItem("silverspool", new Item(new FabricItemSettings()));

    public static final Item METAL_DETECTOR =registerItem("metal_detector",
            new MetalDetectorItem(new FabricItemSettings().maxDamage(300)));

    public static final Item NEEDLE =registerItem("needle",
            new SwordItem(ModToolMaterial.SILVER,3,-2.2f,new FabricItemSettings().maxCount(1)));

    public static final Item SILVER_PICKAXE =registerItem("silver_pickaxe",
            new PickaxeItem(ModToolMaterial.SILVER,0, -3f,new FabricItemSettings()));

    public static final Item SILVER_HELMET =registerItem("silver_helmet",
            new ModArmorItem(ModArmorMaterials.SILVER,ArmorItem.Type.HELMET,new FabricItemSettings()));
    public static final Item SILVER_CHESTPLATE =registerItem("silver_chestplate",
            new ArmorItem(ModArmorMaterials.SILVER,ArmorItem.Type.CHESTPLATE,new FabricItemSettings()));
    public static final Item SILVER_LEGGINGS =registerItem("silver_leggings",
            new ArmorItem(ModArmorMaterials.SILVER,ArmorItem.Type.LEGGINGS,new FabricItemSettings()));
    public static final Item SILVER_BOOTS =registerItem("silver_boots",
            new ArmorItem(ModArmorMaterials.SILVER,ArmorItem.Type.BOOTS,new FabricItemSettings()));


    public static final Item LEMON =registerItem("lemon", new Item(new FabricItemSettings().food(ModFoodComponents.LEMON)));

    public static final Item STRAWBERRY =registerItem("strawberry", new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY)));

    public static final Item TART_CRUST =registerItem("tart_crust", new Item(new FabricItemSettings().food(ModFoodComponents.TART_CRUST)));

    public static final Item UNBAKED_LEMON_TART =registerItem("unbaked_lemon_tart", new Item(new FabricItemSettings().food(ModFoodComponents.UNBAKED_TART)));

    public static final Item UNBAKED_STRAWBERRY_TART =registerItem("unbaked_strawberry_tart", new Item(new FabricItemSettings().food(ModFoodComponents.UNBAKED_TART)));

    public static final Item LEMON_TART =registerItem("lemon_tart", new Item(new FabricItemSettings().food(ModFoodComponents.LEMON_TART)));

    public static final Item STRAWBERRY_TART =registerItem("strawberry_tart", new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY_TART)));

    public static final Item STRAWBERRY_SEEDS = registerItem("strawberry_seeds",
    new AliasedBlockItem(ModBlocks.STRAWBERRY_CROP, new FabricItemSettings()));

    public static final Item LEMON_SEEDS = registerItem("lemon_seeds",
            new AliasedBlockItem(ModBlocks.LEMON_CROP, new FabricItemSettings()));

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries){
        entries.add(SILVER);
        entries.add(SILVERNUGGET);
        entries.add(RAWSILVER);
        entries.add(SPOOL);
        entries.add(SILVERSPOOL);
    }


    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(AnimeWitchery.MOD_ID, name ), item);

    }

    public static void registerModItems( ){
        AnimeWitchery.LOGGER.info("registering Mod Items for"+AnimeWitchery.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }
}
