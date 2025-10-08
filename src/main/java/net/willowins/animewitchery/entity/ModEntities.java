package net.willowins.animewitchery.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.entity.projectile.NeedleProjectileEntity;
import net.willowins.animewitchery.entity.KamikazeRitualEntity;

public class ModEntities {
    public static final EntityType<NeedleProjectileEntity> NEEDLE_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AnimeWitchery.MOD_ID, "needle_projectile"),
            FabricEntityTypeBuilder.<NeedleProjectileEntity>create(
                            SpawnGroup.MISC,
                            NeedleProjectileEntity::new
                    )
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(10)
                    .build()
    );

    public static final EntityType<VoidWispEntity> VOID_WISP = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AnimeWitchery.MOD_ID, "void_wisp"),
            FabricEntityTypeBuilder.<VoidWispEntity>create(
                            SpawnGroup.MONSTER,
                            VoidWispEntity::new
                    )
                    .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
                    .trackRangeBlocks(8)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static final EntityType<KamikazeRitualEntity> KAMIKAZE_RITUAL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(AnimeWitchery.MOD_ID, "kamikaze_ritual"),
            FabricEntityTypeBuilder.<KamikazeRitualEntity>create(
                            SpawnGroup.MISC,
                            KamikazeRitualEntity::new
                    )
                    .dimensions(EntityDimensions.fixed(16.0f, 16.0f)) // Large ritual circle
                    .trackRangeBlocks(32)
                    .trackedUpdateRate(2)
                    .build()
    );
    public static final EntityType<KineticBladeHitboxEntity> KINETIC_BLADE_HITBOX =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(AnimeWitchery.MOD_ID, "kinetic_blade_hitbox"),
                    FabricEntityTypeBuilder.<KineticBladeHitboxEntity>create(SpawnGroup.MISC, KineticBladeHitboxEntity::new)
                            .dimensions(EntityDimensions.fixed(1.5f, 1.5f)) // size for hitbox
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(1)
                            .build()
            );
    public static void registerModEntities() {
        System.out.println("Registering Mod Entities for " + AnimeWitchery.MOD_ID);
    }
}
