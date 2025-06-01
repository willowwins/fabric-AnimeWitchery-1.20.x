package net.willowins.animewitchery.block.entity;

import com.mojang.datafixers.types.Type;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.ModBlocks;

public class ModBlockEntities {
    public static final BlockEntityType<EffigyFountainBlockEntity> EFFIGY_FOUNTAIN_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AnimeWitchery.MOD_ID, "effigy_fountain_be"),
                    FabricBlockEntityTypeBuilder.create(EffigyFountainBlockEntity::new,
                            ModBlocks.EFFIGY_FOUNTAIN).build());

    public static final BlockEntityType<DecorativeFountainBlockEntity> DECORATIVE_FOUNTAIN_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AnimeWitchery.MOD_ID, "decorative_fountain_be"),
                    FabricBlockEntityTypeBuilder.create(DecorativeFountainBlockEntity::new,
                            ModBlocks.DECORATIVE_FOUNTAIN).build());

    public static final BlockEntityType<ActiveEffigyFountainBlockEntity> ACTIVE_EFFIGY_FOUNTAIN_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AnimeWitchery.MOD_ID, "active_effigy_fountain_be"),
                    FabricBlockEntityTypeBuilder.create(ActiveEffigyFountainBlockEntity::new,
                            ModBlocks.ACTIVE_EFFIGY_FOUNTAIN).build((Type) null));

    public static final BlockEntityType<ActiveBindingSpellBlockEntity> ACTIVE_BINDING_SPELL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AnimeWitchery.MOD_ID, "active_binding_spell_be"),
                    FabricBlockEntityTypeBuilder.create(ActiveBindingSpellBlockEntity::new,
                            ModBlocks.ACTIVE_BINDING_SPELL).build((Type) null));

    public static final BlockEntityType<BindingSpellBlockEntity> BINDING_SPELL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AnimeWitchery.MOD_ID, "binding_spell_be"),
                    FabricBlockEntityTypeBuilder.create(BindingSpellBlockEntity::new,
                            ModBlocks.BINDING_SPELL).build((Type) null));

    public static final BlockEntityType<AlchemyTableBlockEntity> ALCHEMY_TABLE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AnimeWitchery.MOD_ID, "alchemy_table_be"),
                    FabricBlockEntityTypeBuilder.create(AlchemyTableBlockEntity::new,
                            ModBlocks.ALCHEMY_TABLE).build((Type) null));

    public static final BlockEntityType<PlayerUseDispenserBlockEntity> PLAYER_USE_DISPENSER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AnimeWitchery.MOD_ID, "player_use_dispenser_be"),
                    FabricBlockEntityTypeBuilder.create(PlayerUseDispenserBlockEntity::new,
                            ModBlocks.PLAYER_USE_DISPENSER).build((Type) null));

    public static void registerBlockEntities() {
        AnimeWitchery.LOGGER.info("Registering Block Entities For {}", AnimeWitchery.MOD_ID);
    }
}