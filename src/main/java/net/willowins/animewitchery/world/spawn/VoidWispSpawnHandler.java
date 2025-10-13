package net.willowins.animewitchery.world.spawn;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;
import net.willowins.animewitchery.entity.ModEntities;
import net.willowins.animewitchery.entity.VoidWispEntity;

public class VoidWispSpawnHandler {
    
    public static void registerSpawns() {
        // Register spawn restrictions
        SpawnRestriction.register(
            ModEntities.VOID_WISP,
            SpawnRestriction.Location.NO_RESTRICTIONS,
            Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
            VoidWispEntity::canSpawn
        );
        
        // Spawn in Overworld near bedrock (Y -64 to -50)
        BiomeModifications.addSpawn(
            BiomeSelectors.foundInOverworld(),
            SpawnGroup.MONSTER,
            ModEntities.VOID_WISP,
            5,  // Lower weight for rarity near bedrock
            1,
            2
        );
        
        // Spawn in Nether near bottom bedrock (Y 0 to 15) and top bedrock/roof (Y 120 to 127)
        // Higher weight makes them more common, especially on the Nether roof
        BiomeModifications.addSpawn(
            BiomeSelectors.foundInTheNether(),
            SpawnGroup.MONSTER,
            ModEntities.VOID_WISP,
            20,  // Much more common in Nether, especially on the roof
            2,
            4
        );
        
        // Spawn rarely in the End
        BiomeModifications.addSpawn(
            BiomeSelectors.foundInTheEnd(),
            SpawnGroup.MONSTER,
            ModEntities.VOID_WISP,
            3,  // Very rare in the End
            1,
            1
        );
        
        System.out.println("[AnimeWitchery] Registered Void Wisp spawning");
    }
}

