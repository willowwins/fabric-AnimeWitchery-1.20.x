package net.willowins.animewitchery.block.entity;

import com.mojang.datafixers.types.Type;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.ActiveObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.GuardianStatueBlockEntity;
import net.willowins.animewitchery.block.entity.AlchemicalEnchanterBlockEntity;

import static net.willowins.animewitchery.AnimeWitchery.MOD_ID;

public class ModBlockEntities {
        public static final BlockEntityType<EffigyFountainBlockEntity> EFFIGY_FOUNTAIN_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "effigy_fountain_be"),
                        FabricBlockEntityTypeBuilder.create(EffigyFountainBlockEntity::new,
                                        ModBlocks.EFFIGY_FOUNTAIN).build());

        public static final BlockEntityType<DecorativeFountainBlockEntity> DECORATIVE_FOUNTAIN_BLOCK_ENTITY = Registry
                        .register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "decorative_fountain_be"),
                                        FabricBlockEntityTypeBuilder.create(DecorativeFountainBlockEntity::new,
                                                        ModBlocks.DECORATIVE_FOUNTAIN).build());

        public static final BlockEntityType<GrowthAcceleratorBlockEntity> GROWTH_ACCELERATOR_BLOCK_ENTITY = Registry
                        .register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "growth_accelerator_be"),
                                        FabricBlockEntityTypeBuilder
                                                        .create(GrowthAcceleratorBlockEntity::new,
                                                                        ModBlocks.GROWTH_ACCELERATOR)
                                                        .build());

        public static final BlockEntityType<ActiveEffigyFountainBlockEntity> ACTIVE_EFFIGY_FOUNTAIN_BLOCK_ENTITY = Registry
                        .register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "active_effigy_fountain_be"),
                                        FabricBlockEntityTypeBuilder.create(ActiveEffigyFountainBlockEntity::new,
                                                        ModBlocks.ACTIVE_EFFIGY_FOUNTAIN).build((Type) null));

        public static final BlockEntityType<ActiveBindingSpellBlockEntity> ACTIVE_BINDING_SPELL_BLOCK_ENTITY = Registry
                        .register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "active_binding_spell_be"),
                                        FabricBlockEntityTypeBuilder.create(ActiveBindingSpellBlockEntity::new,
                                                        ModBlocks.ACTIVE_BINDING_SPELL).build((Type) null));

        public static final BlockEntityType<BossObeliskBlockEntity> BOSS_OBELISK_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "boss_obelisk_be"),
                        FabricBlockEntityTypeBuilder.create(BossObeliskBlockEntity::new,
                                        ModBlocks.BOSS_OBELISK).build());
        public static final BlockEntityType<BindingSpellBlockEntity> BINDING_SPELL_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "binding_spell_be"),
                        FabricBlockEntityTypeBuilder.create(BindingSpellBlockEntity::new,
                                        ModBlocks.BINDING_SPELL).build((Type) null));

        public static final BlockEntityType<AlchemyTableBlockEntity> ALCHEMY_TABLE_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "alchemy_table_be"),
                        FabricBlockEntityTypeBuilder.create(AlchemyTableBlockEntity::new,
                                        ModBlocks.ALCHEMY_TABLE).build((Type) null));

        public static final BlockEntityType<PlateBlockEntity> PLATE_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "plate_be"),
                        FabricBlockEntityTypeBuilder.create(PlateBlockEntity::new,
                                        ModBlocks.PLATE_BLOCK).build((Type) null));

        public static final BlockEntityType<ObeliskBlockEntity> OBELISK_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "obelisk_be"),
                        FabricBlockEntityTypeBuilder.create(ObeliskBlockEntity::new,
                                        ModBlocks.OBELISK).build((Type) null));

        public static final BlockEntityType<ActiveObeliskBlockEntity> ACTIVE_OBELISK_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "active_obelisk_be"),
                        FabricBlockEntityTypeBuilder.create(ActiveObeliskBlockEntity::new,
                                        ModBlocks.ACTIVE_OBELISK).build((Type) null));

        public static final BlockEntityType<BarrierCircleBlockEntity> BARRIER_CIRCLE_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "barrier_circle_be"),
                        FabricBlockEntityTypeBuilder.create(BarrierCircleBlockEntity::new,
                                        ModBlocks.BARRIER_CIRCLE).build((Type) null));

        public static final BlockEntityType<BarrierDistanceGlyphBlockEntity> BARRIER_DISTANCE_GLYPH_BLOCK_ENTITY = Registry
                        .register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "barrier_distance_glyph_be"),
                                        FabricBlockEntityTypeBuilder.create(BarrierDistanceGlyphBlockEntity::new,
                                                        ModBlocks.BARRIER_DISTANCE_GLYPH).build((Type) null));

        public static final BlockEntityType<AutoCrafterBlockEntity> AUTO_CRAFTER_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE,
                        new Identifier(AnimeWitchery.MOD_ID, "auto_crafter_be"),
                        FabricBlockEntityTypeBuilder.create(AutoCrafterBlockEntity::new, ModBlocks.AUTO_CRAFTER_BLOCK)
                                        .build(null));

        public static final BlockEntityType<ItemActionBlockEntity> ITEM_ACTION_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE,
                        new Identifier(AnimeWitchery.MOD_ID, "interactor_be"),
                        FabricBlockEntityTypeBuilder.create(ItemActionBlockEntity::new, ModBlocks.INTERACTOR)
                                        .build(null));
        public static final BlockEntityType<BlockPlacerBlockEntity> BLOCK_PLACER_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE,
                        new Identifier(AnimeWitchery.MOD_ID, "block_placer"),
                        FabricBlockEntityTypeBuilder.create(BlockPlacerBlockEntity::new, ModBlocks.BLOCK_PLACER)
                                        .build(null));

        public static final BlockEntityType<BlockMinerBlockEntity> BLOCK_MINER_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE,
                        new Identifier(MOD_ID, "block_miner"),
                        FabricBlockEntityTypeBuilder.create(BlockMinerBlockEntity::new, ModBlocks.BLOCK_MINER)
                                        .build(null));

        public static final BlockEntityType<GuardianStatueBlockEntity> GUARDIAN_STATUE_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE,
                        new Identifier(MOD_ID, "guardian_statue_be"),
                        FabricBlockEntityTypeBuilder.create(GuardianStatueBlockEntity::new, ModBlocks.GUARDIAN_STATUE)
                                        .build(null));

        public static final BlockEntityType<SpellTriggerBlockEntity> SPELL_TRIGGER_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE,
                        new Identifier(MOD_ID, "spell_trigger_block_be"),
                        FabricBlockEntityTypeBuilder.create(SpellTriggerBlockEntity::new, ModBlocks.SPELL_TRIGGER_BLOCK)
                                        .build(null));

        public static BlockEntityType<TransmutationPyreBlockEntity> TRANSMUTATION_PYRE_BLOCK_ENTITY;

        public static final BlockEntityType<WorldButtonBlockEntity> WORLD_BUTTON_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE,
                        new Identifier(AnimeWitchery.MOD_ID, "world_button_be"),
                        FabricBlockEntityTypeBuilder.create(WorldButtonBlockEntity::new, ModBlocks.WORLD_BUTTON)
                                        .build(null));

        public static final BlockEntityType<ServerButtonBlockEntity> SERVER_BUTTON_BLOCK_ENTITY = Registry.register(
                        Registries.BLOCK_ENTITY_TYPE,
                        new Identifier(AnimeWitchery.MOD_ID, "server_button_be"),
                        FabricBlockEntityTypeBuilder.create(ServerButtonBlockEntity::new, ModBlocks.SERVER_BUTTON)
                                        .build(null));

        public static final BlockEntityType<DeepslateThresholdBlockEntity> DEEPSLATE_THRESHOLD_ENTITY = Registry
                        .register(
                                        Registries.BLOCK_ENTITY_TYPE,
                                        new Identifier(AnimeWitchery.MOD_ID, "deepslate_threshold_be"),
                                        FabricBlockEntityTypeBuilder
                                                        .create(DeepslateThresholdBlockEntity::new,
                                                                        ModBlocks.DEEPSLATE_THRESHOLD)
                                                        .build(null));

        public static final BlockEntityType<net.willowins.animewitchery.entity.custom.MonsterStatueBlockEntity> MONSTER_STATUE_BLOCK_ENTITY = Registry
                        .register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "monster_statue_be"),
                                        FabricBlockEntityTypeBuilder.create(
                                                        net.willowins.animewitchery.entity.custom.MonsterStatueBlockEntity::new,
                                                        ModBlocks.MONSTER_STATUE).build(null));

        public static final BlockEntityType<net.willowins.animewitchery.entity.custom.SoundBlockEntity> SOUND_BLOCK_ENTITY = Registry
                        .register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "sound_block_be"),
                                        FabricBlockEntityTypeBuilder.create(
                                                        net.willowins.animewitchery.entity.custom.SoundBlockEntity::new,
                                                        ModBlocks.SOUND_BLOCK).build(null));

        public static final BlockEntityType<AlchemicalEnchanterBlockEntity> ALCHEMICAL_ENCHANTER_BLOCK_ENTITY = Registry
                        .register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "alchemical_enchanter_be"),
                                        FabricBlockEntityTypeBuilder.create(AlchemicalEnchanterBlockEntity::new,
                                                        ModBlocks.ALCHEMICAL_ENCHANTER).build(null));

        public static void registerBlockEntities() {
                TRANSMUTATION_PYRE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                                new Identifier(AnimeWitchery.MOD_ID, "transmutation_pyre_be"),
                                FabricBlockEntityTypeBuilder.create(TransmutationPyreBlockEntity::new,
                                                ModBlocks.TRANSMUTATION_PYRE_BLOCK).build());
                AnimeWitchery.LOGGER.info("Registering Block Entities for " + AnimeWitchery.MOD_ID);
        }
}