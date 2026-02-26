package net.willowins.animewitchery.fluid;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;

public class ModFluids {
    public static FlowableFluid STILL_STARLIGHT;
    public static FlowableFluid FLOWING_STARLIGHT;

    public static void register() {
        STILL_STARLIGHT = Registry.register(Registries.FLUID,
                new Identifier(AnimeWitchery.MOD_ID, "starlight"), new StarlightFluid.Still());
        FLOWING_STARLIGHT = Registry.register(Registries.FLUID,
                new Identifier(AnimeWitchery.MOD_ID, "flowing_starlight"), new StarlightFluid.Flowing());
    }
}
