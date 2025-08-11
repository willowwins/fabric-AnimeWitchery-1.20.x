package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.GuardianStatueBlockEntity;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class GuardianStatueModel extends DefaultedBlockGeoModel<GuardianStatueBlockEntity> {
    public GuardianStatueModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "guardian_statue"));
    }

}
