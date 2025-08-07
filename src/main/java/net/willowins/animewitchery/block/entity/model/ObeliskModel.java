package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ObeliskModel extends DefaultedBlockGeoModel<ObeliskBlockEntity> {
    public ObeliskModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "obelisk"));
    }
} 