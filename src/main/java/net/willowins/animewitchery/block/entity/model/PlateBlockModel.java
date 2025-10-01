package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.AlchemyTableBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class PlateBlockModel extends DefaultedBlockGeoModel<AlchemyTableBlockEntity> {
    public PlateBlockModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "plate_block"));
    }
}
