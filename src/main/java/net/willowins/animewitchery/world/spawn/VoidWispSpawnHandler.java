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
            2,  // Very rare (reduced from 5)
            1,
            1
        );
        
        // Spawn in Nether near bottom bedrock (Y 0 to 15) and top bedrock/roof (Y 120 to 127)
        // Reduced spawn rate significantly
        BiomeModifications.addSpawn(
            BiomeSelectors.foundInTheNether(),
            SpawnGroup.MONSTER,
            ModEntities.VOID_WISP,
            2,  // Rare (reduced from 20)
            1,
            2
        );
        
        // Spawn rarely in the End
        BiomeModifications.addSpawn(
            BiomeSelectors.foundInTheEnd(),
            SpawnGroup.MONSTER,
            ModEntities.VOID_WISP,
            2,  // Very rare in the End
            1,
            3
        );
        

    }
}

