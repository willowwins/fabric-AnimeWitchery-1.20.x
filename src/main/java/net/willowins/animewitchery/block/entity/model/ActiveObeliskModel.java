package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.ActiveObeliskBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ActiveObeliskModel extends DefaultedBlockGeoModel<ActiveObeliskBlockEntity> {
    public ActiveObeliskModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "obelisk"));
    }
} 