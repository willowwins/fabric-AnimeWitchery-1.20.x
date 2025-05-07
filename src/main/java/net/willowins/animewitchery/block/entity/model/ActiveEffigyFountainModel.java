package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.ActiveEffigyFountainBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.DefaultedGeoModel;

public class ActiveEffigyFountainModel extends DefaultedBlockGeoModel<ActiveEffigyFountainBlockEntity> {
    public ActiveEffigyFountainModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "active_effigy_fountain"));
    }
}
