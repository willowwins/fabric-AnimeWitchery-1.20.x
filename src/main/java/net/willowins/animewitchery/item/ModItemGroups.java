package net.willowins.animewitchery.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.ModBlocks;

public class ModItemGroups {
    public static final ItemGroup SILVER_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnimeWitchery.MOD_ID,"silver"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.silver"))
                    .icon(() -> new ItemStack(ModItems.SILVERSPOOL)).entries((displayContext, entries) -> {
                        entries.add(ModItems.SILVER);
                        entries.add(ModItems.SILVERNUGGET);
                        entries.add(ModItems.SILVERSPOOL);
                        entries.add(ModItems.SPOOL);
                        entries.add(ModItems.RAWSILVER);

                        entries.add(ModItems.LEMON);
                        entries.add(ModItems.STRAWBERRY);
                        entries.add(ModItems.TART_CRUST);
                        entries.add(ModItems.UNBAKED_LEMON_TART);
                        entries.add(ModItems.UNBAKED_STRAWBERRY_TART);
                        entries.add(ModItems.STRAWBERRY_TART);
                        entries.add(ModItems.LEMON_TART);




                        entries.add(ModItems.STRAWBERRY_SEEDS);
                        entries.add(ModItems.LEMON_SEEDS);

                        entries.add(ModItems.NEEDLE);


                        entries.add(ModItems.SILVER_PICKAXE);


                        entries.add(ModItems.METAL_DETECTOR);

                        entries.add(ModBlocks.CHARCOAL_BLOCK);
                        entries.add(ModBlocks.SOUND_BLOCK);
                        entries.add(ModBlocks.SOUND_BLOCK2);
                        entries.add(ModBlocks.SILVER_BLOCK);
                        entries.add(ModBlocks.SILVER_ORE);
                        entries.add(ModBlocks.DEEPSLATE_SILVER_ORE);

                        entries.add(ModBlocks.SILVER_BUTTON);
                        entries.add(ModBlocks.SILVER_PRESSURE_PLATE);

                        entries.add(ModBlocks.SILVER_DOOR);
                        entries.add(ModBlocks.SILVER_TRAPDOOR);

                        entries.add(ModBlocks.SILVER_FENCE);
                        entries.add(ModBlocks.SILVER_FENCE_GATE);
                        entries.add(ModBlocks.SILVER_WALL);

                        entries.add(ModBlocks.SILVER_SLAB);
                        entries.add(ModBlocks.SILVER_STAIRS);


                    } ).build());


    public static void registerItemGroups(){
        AnimeWitchery.LOGGER.info("registering Item Groups For " +AnimeWitchery.MOD_ID);
    }
}
