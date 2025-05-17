package net.willowins.animewitchery.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.custom.LemonCropBlock;
import net.willowins.animewitchery.block.custom.StrawberryCropBlock;
import net.willowins.animewitchery.item.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BlockStateModelGenerator.BlockTexturePool silverPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.SILVER_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SILVER_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CHARCOAL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SOUND_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SOUND_BLOCK2);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_SILVER_ORE);

        silverPool.stairs(ModBlocks.SILVER_STAIRS);
        silverPool.slab(ModBlocks.SILVER_SLAB);
        silverPool.button(ModBlocks.SILVER_BUTTON);
        silverPool.pressurePlate(ModBlocks.SILVER_PRESSURE_PLATE);
        silverPool.fence(ModBlocks.SILVER_FENCE);
        silverPool.fenceGate(ModBlocks.SILVER_FENCE_GATE);
        silverPool.wall(ModBlocks.SILVER_WALL);

        blockStateModelGenerator.registerSimpleState(ModBlocks.EFFIGY_FOUNTAIN);
        blockStateModelGenerator.registerSimpleState(ModBlocks.DECORATIVE_FOUNTAIN);
        blockStateModelGenerator.registerSimpleState(ModBlocks.ACTIVE_EFFIGY_FOUNTAIN);

        blockStateModelGenerator.registerDoor(ModBlocks.SILVER_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.SILVER_TRAPDOOR);

        blockStateModelGenerator.registerCrop(ModBlocks.STRAWBERRY_CROP, StrawberryCropBlock.AGE, 0, 1, 2, 3);
        blockStateModelGenerator.registerCrop(ModBlocks.LEMON_CROP, LemonCropBlock.AGE, 0, 1, 2, 3, 4, 5, 6, 7, 8);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.LEMON, Models.GENERATED);
        itemModelGenerator.register(ModItems.STRAWBERRY, Models.GENERATED);
        itemModelGenerator.register(ModItems.TART_CRUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNBAKED_LEMON_TART, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNBAKED_STRAWBERRY_TART, Models.GENERATED);
        itemModelGenerator.register(ModItems.STRAWBERRY_TART, Models.GENERATED);
        itemModelGenerator.register(ModItems.LEMON_TART, Models.GENERATED);
        itemModelGenerator.register(ModItems.ALCHEMICAL_CATALYST, Models.GENERATED);
        itemModelGenerator.register(ModItems.BLAZE_SACK, Models.GENERATED);
        itemModelGenerator.register(ModItems.STAFF_HEAD, Models.GENERATED);

        itemModelGenerator.register(ModItems.SILVER_PENDANT, Models.GENERATED);




        itemModelGenerator.register(ModItems.SILVERSPOOL, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVER, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVERNUGGET, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPOOL, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAWSILVER, Models.GENERATED);
        itemModelGenerator.register(ModItems.METAL_DETECTOR, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILVER_PICKAXE, Models.HANDHELD);


        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SILVER_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SILVER_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SILVER_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.SILVER_BOOTS));
    }
}
