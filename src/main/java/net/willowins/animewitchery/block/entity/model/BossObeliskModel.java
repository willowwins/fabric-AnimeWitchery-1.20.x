package net.willowins.animewitchery.block.entity.model;

import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.BossObeliskBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class BossObeliskModel extends DefaultedBlockGeoModel<BossObeliskBlockEntity> {
    public BossObeliskModel() {
        super(new Identifier(AnimeWitchery.MOD_ID, "boss_obelisk"));
    }

    @Override
    public Identifier getTextureResource(BossObeliskBlockEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/block/eidolon_obelisk.png");
    }
}


